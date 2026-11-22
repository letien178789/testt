package data;

import model.ChiTietPhieu;
import model.PhieuKho;
import model.PhieuNhap;
import model.PhieuXuat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Phu trach: TienLe
// Truy cập bộ nhớ lưu phiếu kho (cả PhieuNhap và PhieuXuat) - tính đa hình:
// danh sách chỉ biết kiểu PhieuKho, khi ghi file mới kiểm tra instanceof.
//
// Định dạng file (mỗi dòng 1 phiếu):
//   XUAT|maPhieu;maKho;ngay;lyDo;trangThai;SP001:20,SP002:25     (do PhieuXuat tự tạo)
//   NHAP|maPhieu;maKho;ngay;maNCC;trangThai;SP001:20:15000,...   (SP:soLuong:donGia)
public class PhieuKhoDAO {
    private final List<PhieuKho> danhSach = new ArrayList<>();
    private final NhaCungCapDAO nhaCungCapDAO; // để nối lại nhà cung cấp khi đọc phiếu nhập

    public PhieuKhoDAO(NhaCungCapDAO nhaCungCapDAO) {
        this.nhaCungCapDAO = nhaCungCapDAO;
    }

    public List<PhieuKho> findAll() {
        return new ArrayList<>(danhSach);
    }

    public PhieuKho findById(String maPhieu) {
        for (PhieuKho p : danhSach) {
            if (p.getMaPhieu().equals(maPhieu)) {
                return p;
            }
        }
        return null;
    }

    public boolean existsById(String maPhieu) {
        return findById(maPhieu) != null;
    }

    public List<PhieuKho> findByTrangThai(String trangThai) {
        List<PhieuKho> ketQua = new ArrayList<>();
        for (PhieuKho p : danhSach) {
            if (p.getTrangThai().equals(trangThai)) {
                ketQua.add(p);
            }
        }
        return ketQua;
    }

    public void insert(PhieuKho phieu) {
        if (existsById(phieu.getMaPhieu())) {
            throw new IllegalArgumentException("Mã phiếu đã tồn tại: " + phieu.getMaPhieu());
        }
        danhSach.add(phieu);
    }

    public void update(PhieuKho phieu) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaPhieu().equals(phieu.getMaPhieu())) {
                danhSach.set(i, phieu);
                return;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy phiếu để cập nhật");
    }

    public void delete(String maPhieu) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaPhieu().equals(maPhieu)) {
                danhSach.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy phiếu để xóa");
    }

    public void clear() {
        danhSach.clear();
    }

    // ----- Ghi file -----
    public List<String> toDong() {
        List<String> dong = new ArrayList<>();
        for (PhieuKho p : danhSach) {
            if (p instanceof PhieuXuat) {
                dong.add("XUAT|" + ((PhieuXuat) p).toDongFile());
            } else if (p instanceof PhieuNhap) {
                dong.add("NHAP|" + phieuNhapToDong((PhieuNhap) p));
            }
        }
        return dong;
    }

    private String phieuNhapToDong(PhieuNhap pn) {
        StringBuilder sb = new StringBuilder();
        for (ChiTietPhieu ct : pn.getChiTiet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(ct.getMaSanPham()).append(":").append(ct.getSoLuong())
                    .append(":").append(ct.getDonGia());
        }
        String maNCC = pn.getNhaCungCap() == null ? "" : pn.getNhaCungCap().getMaNhaCungCap();
        return FileStorage.lamSach(pn.getMaPhieu()) + ";"
                + FileStorage.lamSach(pn.getMaKho()) + ";"
                + pn.getNgayLap() + ";"
                + FileStorage.lamSach(maNCC) + ";"
                + pn.getTrangThai() + ";"
                + sb;
    }

    // ----- Đọc file (gọi sau khi đã nạp xong nhà cung cấp) -----
    public void napTuDong(List<String> cacDong) {
        clear();
        for (String d : cacDong) {
            try {
                int gach = d.indexOf('|');
                if (gach < 0) {
                    continue;
                }
                String loai = d.substring(0, gach);
                String noiDung = d.substring(gach + 1);
                PhieuKho phieu = null;
                if (loai.equals("XUAT")) {
                    phieu = PhieuXuat.tuDongFile(noiDung);
                } else if (loai.equals("NHAP")) {
                    phieu = phieuNhapTuDong(noiDung);
                }
                if (phieu != null && !existsById(phieu.getMaPhieu())) {
                    danhSach.add(phieu);
                }
            } catch (RuntimeException e) {
                System.out.println("Bỏ qua dòng phiếu lỗi: " + d);
            }
        }
    }

    private PhieuNhap phieuNhapTuDong(String dong) {
        String[] p = dong.split(";", 6);
        if (p.length < 6) {
            return null;
        }
        PhieuNhap pn = new PhieuNhap();
        pn.setMaPhieu(p[0]);
        pn.setMaKho(p[1]);
        pn.setNgayLap(LocalDate.parse(p[2]));
        if (!p[3].isEmpty()) {
            pn.setNhaCungCap(nhaCungCapDAO.findById(p[3]));
        }
        pn.setTrangThai(p[4]);
        List<ChiTietPhieu> chiTiet = new ArrayList<>();
        if (!p[5].trim().isEmpty()) {
            for (String s : p[5].split(",")) {
                String[] kv = s.split(":");
                ChiTietPhieu ct = new ChiTietPhieu(kv[0], Integer.parseInt(kv[1]));
                ct.setDonGia(Double.parseDouble(kv[2]));
                chiTiet.add(ct);
            }
        }
        pn.setChiTiet(chiTiet);
        return pn;
    }
}
package function;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.ChiTietPhieu;
import model.PhieuKho;
import model.PhieuXuat;

// Phu trach: MinhNgoc
// Danh sách phiếu xuất dùng MẢNG ĐỘNG (ArrayList). Thực thi interface IQuanLy.
public class XuatKhoService implements IQuanLy<PhieuXuat, String> {
    private ArrayList<PhieuXuat> ds;
    private TonKhoService tonKhoService;

    public XuatKhoService(TonKhoService tonKhoService) {
        this.ds = new ArrayList<>();
        this.tonKhoService = tonKhoService;
    }

    public int size() {
        return ds.size();
    }

    // ================= Thực thi interface IQuanLy =================
    @Override
    public List<PhieuXuat> findAll() {
        return new ArrayList<>(ds);
    }

    @Override
    public PhieuXuat findById(String ma) {
        return timTheoMa(ma);
    }

    @Override
    public boolean existsById(String ma) {
        return timTheoMa(ma) != null;
    }

    @Override
    public void insert(PhieuXuat px) {
        if (!them(px)) {
            throw new IllegalArgumentException("Không thêm được phiếu xuất (null hoặc trùng mã)");
        }
    }

    // Chỉ thay thế được phiếu đang CHO_XU_LY
    @Override
    public void update(PhieuXuat px) {
        for (int i = 0; i < ds.size(); i++) {
            if (ds.get(i).getMaPhieu().equals(px.getMaPhieu())) {
                if (!PhieuKho.CHO_XU_LY.equals(ds.get(i).getTrangThai())) {
                    throw new IllegalArgumentException("Phiếu đã xử lý, không được sửa");
                }
                ds.set(i, px);
                return;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy phiếu xuất để cập nhật");
    }

    @Override
    public void delete(String ma) {
        if (!xoa(ma)) {
            throw new IllegalArgumentException("Không xóa được phiếu (không có hoặc đã xử lý)");
        }
    }

    // Đọc 1 số nguyên, nhập sai thì nhập lại
    private int docSo(Scanner sc, String nhac) {
        while (true) {
            System.out.print(nhac);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số nguyên.");
            }
        }
    }

    // ================= a. Nhập n phiếu đầu tiên =================
    public void nhapDanhSachDau(Scanner sc, int soPhieu) {
        for (int i = 0; i < soPhieu; i++) {
            System.out.println("--- Nhập phiếu xuất thứ " + (i + 1) + " ---");
            System.out.print("Mã phiếu: ");
            String ma = sc.nextLine().trim();
            System.out.print("Mã kho: ");
            String kho = sc.nextLine().trim();
            System.out.print("Lý do xuất: ");
            String lyDo = sc.nextLine().trim();
            System.out.print("Người nhận hàng: ");
            String nguoiNhan = sc.nextLine().trim();
            PhieuXuat px = new PhieuXuat(ma, kho, lyDo, nguoiNhan);
            while (true) {
                System.out.print("Mã sản phẩm (Enter để kết thúc): ");
                String sp = sc.nextLine().trim();
                if (sp.isEmpty()) {
                    break;
                }
                px.themChiTiet(sp, docSo(sc, "Số lượng: "));
            }
            if (!them(px)) {
                i--;
            }
        }
    }

    // ================= b. Xuất danh sách =================
    public void xuat() {
        System.out.println("----- DANH SÁCH PHIẾU XUẤT (" + ds.size() + " phiếu) -----");
        for (PhieuXuat px : ds) {
            px.inPhieu();
        }
    }

    // ================= c. Thêm 1 phiếu, k phiếu =================
    public boolean them(PhieuXuat px) {
        if (px == null) {
            return false;
        }
        if (timTheoMa(px.getMaPhieu()) != null) {
            System.out.println("Mã phiếu " + px.getMaPhieu() + " đã tồn tại.");
            return false;
        }
        ds.add(px);
        return true;
    }

    public int themK(PhieuXuat[] mang, int k) {
        int daThem = 0;
        for (int i = 0; i < k && i < mang.length; i++) {
            if (them(mang[i])) {
                daThem++;
            }
        }
        return daThem;
    }

    // ================= d. Sửa theo mã (chỉ sửa phiếu chưa xử lý) =================
    public boolean suaLyDo(String maPhieu, String lyDoMoi) {
        PhieuXuat px = timTheoMa(maPhieu);
        if (px == null || !PhieuKho.CHO_XU_LY.equals(px.getTrangThai())) {
            return false;
        }
        px.setLyDo(lyDoMoi);
        return true;
    }

    // ================= e. Xóa theo mã (chỉ xóa phiếu chưa xử lý) =================
    public boolean xoa(String maPhieu) {
        PhieuXuat px = timTheoMa(maPhieu);
        if (px == null || !PhieuKho.CHO_XU_LY.equals(px.getTrangThai())) {
            return false;
        }
        ds.remove(px);
        return true;
    }

    // ================= f. Tìm kiếm =================
    public PhieuXuat timTheoMa(String maPhieu) {  // chính xác
        if (maPhieu == null) {
            return null;
        }
        for (PhieuXuat px : ds) {
            if (px.getMaPhieu().equals(maPhieu.trim())) {
                return px;
            }
        }
        return null;
    }

    /** Tìm nhiều khóa: kho (chính xác), lý do (gần đúng), trạng thái (chính xác). "" = bỏ qua. */
    public ArrayList<PhieuXuat> timNhieuKhoa(String maKho, String tuKhoaLyDo, String trangThai) {
        ArrayList<PhieuXuat> ketQua = new ArrayList<>();
        String tk = tuKhoaLyDo.toLowerCase();
        for (PhieuXuat px : ds) {
            boolean khop = true;
            if (!maKho.isEmpty() && !px.getMaKho().equals(maKho)) {
                khop = false;
            }
            String lyDo = (px.getLyDo() == null) ? "" : px.getLyDo().toLowerCase();
            if (!tk.isEmpty() && !lyDo.contains(tk)) {
                khop = false;
            }
            if (!trangThai.isEmpty() && !px.getTrangThai().equals(trangThai)) {
                khop = false;
            }
            if (khop) {
                ketQua.add(px);
            }
        }
        return ketQua;
    }

    // Tìm phiếu lập trong khoảng ngày [tuNgay, denNgay]
    public ArrayList<PhieuXuat> timTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        ArrayList<PhieuXuat> ketQua = new ArrayList<>();
        for (PhieuXuat px : ds) {
            if (!px.getNgay().isBefore(tuNgay) && !px.getNgay().isAfter(denNgay)) {
                ketQua.add(px);
            }
        }
        return ketQua;
    }

    public void inKetQua(String tieuDe, ArrayList<PhieuXuat> ketQua) {
        System.out.println("----- " + tieuDe + " (" + ketQua.size() + " kết quả) -----");
        for (PhieuXuat px : ketQua) {
            px.inPhieu();
        }
    }

    // ================= g. Thống kê =================
    public int demTheoTrangThai(String trangThai) {
        int dem = 0;
        for (PhieuXuat px : ds) {
            if (px.getTrangThai().equals(trangThai)) {
                dem++;
            }
        }
        return dem;
    }

    // Tổng số lượng một sản phẩm đã xuất (chỉ tính phiếu đã xử lý)
    public int tongDaXuatTheoSanPham(String maSanPham) {
        int tong = 0;
        for (PhieuXuat px : ds) {
            if (PhieuKho.DA_XU_LY.equals(px.getTrangThai())) {
                for (ChiTietPhieu ct : px.getDanhSachChiTiet()) {
                    if (ct.getMaSanPham().equals(maSanPham)) {
                        tong += ct.getSoLuong();
                    }
                }
            }
        }
        return tong;
    }

    // Tổng số lượng đã xuất của 1 kho (chỉ tính phiếu đã xử lý)
    public int tongDaXuatTheoKho(String maKho) {
        int tong = 0;
        for (PhieuXuat px : ds) {
            if (px.getMaKho().equals(maKho) && PhieuKho.DA_XU_LY.equals(px.getTrangThai())) {
                tong += px.tinhTongSoLuong();
            }
        }
        return tong;
    }

    public void inThongKe() {
        System.out.println("----- THỐNG KÊ PHIẾU XUẤT -----");
        System.out.println("Tổng số phiếu : " + ds.size());
        System.out.println("Đã xuất       : " + demTheoTrangThai(PhieuKho.DA_XU_LY));
        System.out.println("Bị từ chối    : " + demTheoTrangThai(PhieuKho.TU_CHOI));
        System.out.println("Chờ xử lý     : " + demTheoTrangThai(PhieuKho.CHO_XU_LY));
    }

    // ================= h. Nghiệp vụ chính: xuất kho =================
    /** Bước 1: kiểm tra mọi dòng có đủ hàng không, in rõ dòng nào thiếu. */
    public boolean kiemTraDuHang(PhieuXuat phieu) {
        boolean duHang = true;
        for (ChiTietPhieu ct : phieu.getDanhSachChiTiet()) {
            int ton = tonKhoService.laySoLuongTon(phieu.getMaKho(), ct.getMaSanPham());
            if (ton < ct.getSoLuong()) {
                System.out.println("  Không đủ hàng: SP " + ct.getMaSanPham()
                        + " cần " + ct.getSoLuong() + ", tồn chỉ có " + ton);
                duHang = false;
            }
        }
        return duHang;
    }

    /**
     * Bước 2: nếu đủ thì trừ tồn. Việc trừ tồn gọi capNhatTon() qua biến kiểu PhieuKho
     * -> ĐA HÌNH: chạy đúng hàm của PhieuXuat. TonKhoService tự ghi lịch sử và cảnh báo.
     */
    public boolean xuatKho(PhieuXuat phieu) {
        System.out.println(">> Xử lý phiếu xuất " + phieu.getMaPhieu());
        if (timTheoMa(phieu.getMaPhieu()) == null) {
            ds.add(phieu); // phiếu mới thì lưu vào danh sách
        }
        if (!PhieuKho.CHO_XU_LY.equals(phieu.getTrangThai())) {
            System.out.println("  Phiếu này đã được xử lý rồi (" + phieu.getTrangThai() + ").");
            return false;
        }
        if (phieu.getDanhSachChiTiet().isEmpty()) {
            System.out.println("  Phiếu không có mặt hàng nào.");
            return false;
        }
        if (!kiemTraDuHang(phieu)) {
            phieu.setTrangThai(PhieuKho.TU_CHOI);
            System.out.println("  => TỪ CHỐI phiếu " + phieu.getMaPhieu() + " (tồn kho không đổi).");
            return false;
        }
        PhieuKho p = phieu;                       // đa hình: p là PhieuKho nhưng trỏ tới PhieuXuat
        p.capNhatTon(tonKhoService);
        System.out.println("  => XUẤT KHO THÀNH CÔNG phiếu " + phieu.getMaPhieu());
        return true;
    }

    // Xử lý lần lượt mọi phiếu đang chờ, trả về số phiếu xuất thành công
    public int xuLyTatCaPhieuCho() {
        int thanhCong = 0;
        for (PhieuXuat px : new ArrayList<>(ds)) {
            if (PhieuKho.CHO_XU_LY.equals(px.getTrangThai()) && xuatKho(px)) {
                thanhCong++;
            }
        }
        return thanhCong;
    }

    // ================= File text =================
    public boolean ghiFile(String duongDan) {
        try {
            Path path = Paths.get(duongDan);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            List<String> cacDong = new ArrayList<>();
            for (PhieuXuat px : ds) {
                cacDong.add(px.toDongFile());
            }
            Files.write(path, cacDong, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
            return false;
        }
    }

    public boolean docFile(String duongDan) {
        try {
            List<String> cacDong = Files.readAllLines(Paths.get(duongDan), StandardCharsets.UTF_8);
            ds.clear();
            for (String dong : cacDong) {
                if (dong.trim().isEmpty()) {
                    continue;
                }
                PhieuXuat px = PhieuXuat.tuDongFile(dong);
                if (px != null) {
                    ds.add(px);
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
            return false;
        }
    }
}
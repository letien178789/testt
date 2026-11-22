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
import model.PhieuNhap;
import model.PhieuXuat;

// Phu trach: MinhNgoc
// Danh sách phiếu kho dùng MẢNG TĨNH kiểu PhieuKho[] - mỗi ô có thể chứa PhieuNhap HOẶC PhieuXuat (ĐA HÌNH).
// Thực thi interface IQuanLy.
public class DanhSachPhieuKho implements IQuanLy<PhieuKho, String> {
    // ----- Thành phần static -----
    public static final int SUC_CHUA_MAC_DINH = 500;
    public static final String LOAI_NHAP = "NHAP";
    public static final String LOAI_XUAT = "XUAT";

    // Hàm static: cho biết 1 phiếu là loại nào (dựa vào kiểu thật của đối tượng)
    public static String layLoai(PhieuKho p) {
        if (p instanceof PhieuXuat) {
            return LOAI_XUAT;
        }
        if (p instanceof PhieuNhap) {
            return LOAI_NHAP;
        }
        return "KHAC";
    }

    // ----- Thuộc tính -----
    private PhieuKho[] ds;   // mảng tĩnh: kích thước cố định khi tạo
    private int n;           // số phiếu đang có

    // ----- Hàm thiết lập (constructor) -----
    public DanhSachPhieuKho() {
        this(SUC_CHUA_MAC_DINH);
    }

    public DanhSachPhieuKho(int sucChua) {
        if (sucChua <= 0) {
            sucChua = SUC_CHUA_MAC_DINH;
        }
        this.ds = new PhieuKho[sucChua];
        this.n = 0;
    }

    public int size() {
        return n;
    }

    public int getSucChua() {
        return ds.length;
    }

    public PhieuKho getPhieu(int viTri) {
        if (viTri < 0 || viTri >= n) {
            return null;
        }
        return ds[viTri];
    }

    // Vị trí của phiếu trong mảng, -1 nếu không có
    private int timViTri(String maPhieu) {
        if (maPhieu == null) {
            return -1;
        }
        for (int i = 0; i < n; i++) {
            if (ds[i].getMaPhieu().equals(maPhieu.trim())) {
                return i;
            }
        }
        return -1;
    }

    // ================= Thực thi interface IQuanLy =================
    @Override
    public List<PhieuKho> findAll() {
        List<PhieuKho> ketQua = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ketQua.add(ds[i]);
        }
        return ketQua;
    }

    @Override
    public PhieuKho findById(String ma) {
        return timTheoMa(ma);
    }

    @Override
    public boolean existsById(String ma) {
        return timViTri(ma) != -1;
    }

    @Override
    public void insert(PhieuKho phieu) {
        if (!them(phieu)) {
            throw new IllegalArgumentException("Không thêm được phiếu (null, trùng mã hoặc mảng đầy)");
        }
    }

    // Chỉ thay thế được phiếu đang CHO_XU_LY
    @Override
    public void update(PhieuKho phieu) {
        int vt = timViTri(phieu.getMaPhieu());
        if (vt == -1) {
            throw new IllegalArgumentException("Không tìm thấy phiếu để cập nhật");
        }
        if (!PhieuKho.CHO_XU_LY.equals(ds[vt].getTrangThai())) {
            throw new IllegalArgumentException("Phiếu đã xử lý, không được sửa");
        }
        ds[vt] = phieu;
    }

    @Override
    public void delete(String ma) {
        if (!xoa(ma)) {
            throw new IllegalArgumentException("Không xóa được phiếu (không có hoặc đã xử lý)");
        }
    }

    // ================= a. Nhập n phiếu đầu tiên =================
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

    public void nhapDanhSachDau(Scanner sc, int soPhieu) {
        for (int i = 0; i < soPhieu; i++) {
            if (n >= ds.length) {
                System.out.println("Mảng đã đầy, dừng nhập.");
                break;
            }
            System.out.println("--- Nhập phiếu thứ " + (i + 1) + " ---");
            System.out.print("Loại phiếu (N = nhập, X = xuất): ");
            String loai = sc.nextLine().trim();
            System.out.print("Mã phiếu: ");
            String ma = sc.nextLine().trim();
            System.out.print("Mã kho: ");
            String kho = sc.nextLine().trim();
            List<ChiTietPhieu> chiTiet = new ArrayList<>();
            while (true) {
                System.out.print("Mã sản phẩm (Enter để kết thúc): ");
                String sp = sc.nextLine().trim();
                if (sp.isEmpty()) {
                    break;
                }
                int sl = docSo(sc, "Số lượng: ");
                if (sl > 0) {
                    chiTiet.add(new ChiTietPhieu(sp, sl));
                }
            }
            PhieuKho phieu;
            if (loai.equalsIgnoreCase("N")) {
                phieu = new PhieuNhap(ma, LocalDate.now(), kho, null, chiTiet);
            } else {
                System.out.print("Lý do xuất: ");
                PhieuXuat px = new PhieuXuat(ma, kho, sc.nextLine().trim());
                for (ChiTietPhieu ct : chiTiet) {
                    px.themChiTiet(ct.getMaSanPham(), ct.getSoLuong());
                }
                phieu = px;
            }
            if (!them(phieu)) {
                i--;
            }
        }
    }

    // ================= b. Xuất danh sách (đa hình) =================
    public void xuat() {
        System.out.println("----- DANH SÁCH PHIẾU KHO (" + n + "/" + ds.length + ") -----");
        for (int i = 0; i < n; i++) {
            ds[i].inPhieu(); // đa hình: PhieuXuat in theo kiểu của nó, PhieuNhap in theo kiểu của nó
        }
    }

    // ================= c. Thêm 1 phiếu, k phiếu =================
    public boolean them(PhieuKho phieu) {
        if (phieu == null) {
            return false;
        }
        if (n >= ds.length) {
            System.out.println("Mảng đã đầy (" + ds.length + "), không thêm được.");
            return false;
        }
        if (timViTri(phieu.getMaPhieu()) != -1) {
            System.out.println("Mã phiếu " + phieu.getMaPhieu() + " đã tồn tại.");
            return false;
        }
        ds[n] = phieu;
        n++;
        return true;
    }

    // Thêm k phiếu lấy từ mảng 'mang' (mảng có thể trộn lẫn PhieuNhap và PhieuXuat)
    public int themK(PhieuKho[] mang, int k) {
        int daThem = 0;
        for (int i = 0; i < k && i < mang.length; i++) {
            if (them(mang[i])) {
                daThem++;
            }
        }
        return daThem;
    }

    // Xóa hết rồi nạp lại từ 1 danh sách (Main dùng để đồng bộ với PhieuKhoDAO)
    public void napTu(List<PhieuKho> danhSach) {
        for (int i = 0; i < n; i++) {
            ds[i] = null;
        }
        n = 0;
        for (PhieuKho p : danhSach) {
            if (n < ds.length && timViTri(p.getMaPhieu()) == -1) {
                ds[n] = p;
                n++;
            }
        }
    }

    // ================= d. Sửa theo mã (chỉ sửa phiếu chưa xử lý) =================
    public boolean suaLyDo(String maPhieu, String lyDoMoi) {
        int vt = timViTri(maPhieu);
        if (vt == -1 || !PhieuKho.CHO_XU_LY.equals(ds[vt].getTrangThai())) {
            return false;
        }
        ds[vt].setLyDo(lyDoMoi);
        return true;
    }

    // ================= e. Xóa theo mã (dồn mảng sang trái) =================
    public boolean xoa(String maPhieu) {
        int vt = timViTri(maPhieu);
        if (vt == -1 || !PhieuKho.CHO_XU_LY.equals(ds[vt].getTrangThai())) {
            return false;
        }
        for (int i = vt; i < n - 1; i++) {
            ds[i] = ds[i + 1];
        }
        ds[n - 1] = null;
        n--;
        return true;
    }

    // ================= f. Tìm kiếm: từ chính xác đến gần đúng =================
    public PhieuKho timTheoMa(String maPhieu) {   // chính xác
        int vt = timViTri(maPhieu);
        if (vt == -1) {
            return null;
        }
        return ds[vt];
    }

    /**
     * Tìm NHIỀU KHÓA cùng lúc ("" = bỏ qua khóa đó):
     * loai       : "NHAP" hoặc "XUAT" (chính xác)
     * maKho      : trùng chính xác
     * trangThai  : CHO_XU_LY / DA_XU_LY / TU_CHOI (chính xác)
     * tuKhoaLyDo : lý do chứa từ khóa (gần đúng, không phân biệt hoa thường)
     */
    public PhieuKho[] timNhieuKhoa(String loai, String maKho, String trangThai, String tuKhoaLyDo) {
        String tk = (tuKhoaLyDo == null) ? "" : tuKhoaLyDo.trim().toLowerCase();
        PhieuKho[] tam = new PhieuKho[n];
        int dem = 0;
        for (int i = 0; i < n; i++) {
            boolean khop = true;
            if (!loai.isEmpty() && !layLoai(ds[i]).equalsIgnoreCase(loai)) {
                khop = false;
            }
            if (!maKho.isEmpty() && !ds[i].getMaKho().equals(maKho)) {
                khop = false;
            }
            if (!trangThai.isEmpty() && !ds[i].getTrangThai().equals(trangThai)) {
                khop = false;
            }
            String lyDo = (ds[i].getLyDo() == null) ? "" : ds[i].getLyDo().toLowerCase();
            if (!tk.isEmpty() && !lyDo.contains(tk)) {
                khop = false;
            }
            if (khop) {
                tam[dem] = ds[i];
                dem++;
            }
        }
        PhieuKho[] ketQua = new PhieuKho[dem];
        for (int i = 0; i < dem; i++) {
            ketQua[i] = tam[i];
        }
        return ketQua;
    }

    // Tìm phiếu lập trong khoảng ngày [tuNgay, denNgay]
    public PhieuKho[] timTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        PhieuKho[] tam = new PhieuKho[n];
        int dem = 0;
        for (int i = 0; i < n; i++) {
            LocalDate ngay = ds[i].getNgay();
            if (!ngay.isBefore(tuNgay) && !ngay.isAfter(denNgay)) {
                tam[dem] = ds[i];
                dem++;
            }
        }
        PhieuKho[] ketQua = new PhieuKho[dem];
        for (int i = 0; i < dem; i++) {
            ketQua[i] = tam[i];
        }
        return ketQua;
    }

    public void inKetQua(String tieuDe, PhieuKho[] ketQua) {
        System.out.println("----- " + tieuDe + " (" + ketQua.length + " kết quả) -----");
        for (PhieuKho p : ketQua) {
            p.inPhieu();
        }
    }

    // ================= Sắp xếp theo mã phiếu (chọn trực tiếp) =================
    public void sapXepTheoMa() {
        for (int i = 0; i < n - 1; i++) {
            int chon = i;
            for (int j = i + 1; j < n; j++) {
                if (ds[j].getMaPhieu().compareTo(ds[chon].getMaPhieu()) < 0) {
                    chon = j;
                }
            }
            PhieuKho tam = ds[i];
            ds[i] = ds[chon];
            ds[chon] = tam;
        }
    }

    // ================= g. Thống kê theo nhiều khóa =================
    public int demTheoLoai(String loai) {
        int dem = 0;
        for (int i = 0; i < n; i++) {
            if (layLoai(ds[i]).equalsIgnoreCase(loai)) {
                dem++;
            }
        }
        return dem;
    }

    public int demTheoTrangThai(String trangThai) {
        int dem = 0;
        for (int i = 0; i < n; i++) {
            if (ds[i].getTrangThai().equals(trangThai)) {
                dem++;
            }
        }
        return dem;
    }

    public int demTheoKho(String maKho) {
        int dem = 0;
        for (int i = 0; i < n; i++) {
            if (ds[i].getMaKho().equals(maKho)) {
                dem++;
            }
        }
        return dem;
    }

    // Tổng số lượng hàng đã xuất khỏi 1 kho (chỉ tính phiếu xuất đã xử lý)
    public int tongSoLuongDaXuat(String maKho) {
        int tong = 0;
        for (int i = 0; i < n; i++) {
            if (ds[i] instanceof PhieuXuat && ds[i].getMaKho().equals(maKho)
                    && PhieuKho.DA_XU_LY.equals(ds[i].getTrangThai())) {
                tong += ds[i].tinhTongSoLuong();
            }
        }
        return tong;
    }

    public void inThongKe() {
        System.out.println("----- THỐNG KÊ PHIẾU KHO -----");
        System.out.println("Tổng số phiếu : " + n);
        System.out.println("Phiếu nhập    : " + demTheoLoai(LOAI_NHAP));
        System.out.println("Phiếu xuất    : " + demTheoLoai(LOAI_XUAT));
        System.out.println("Chờ xử lý     : " + demTheoTrangThai(PhieuKho.CHO_XU_LY));
        System.out.println("Đã xử lý      : " + demTheoTrangThai(PhieuKho.DA_XU_LY));
        System.out.println("Bị từ chối    : " + demTheoTrangThai(PhieuKho.TU_CHOI));
    }

    // ================= h. Nghiệp vụ: xử lý phiếu bằng ĐA HÌNH =================
    /**
     * Xử lý 1 phiếu đang chờ. Không cần biết là phiếu nhập hay xuất:
     * gọi capNhatTon() thì PhieuNhap sẽ cộng tồn, PhieuXuat sẽ trừ tồn.
     * Không xử lý được (thiếu hàng...) thì phiếu bị TU_CHOI.
     */
    public boolean xuLyPhieu(String maPhieu, TonKhoService tonKhoService) {
        PhieuKho p = timTheoMa(maPhieu);
        if (p == null) {
            System.out.println("Không tìm thấy phiếu " + maPhieu);
            return false;
        }
        if (!PhieuKho.CHO_XU_LY.equals(p.getTrangThai())) {
            System.out.println("Phiếu " + maPhieu + " đã được xử lý rồi (" + p.getTrangThai() + ").");
            return false;
        }
        System.out.println(">> Xử lý " + p.loaiPhieu() + " " + p.getMaPhieu());
        if (p instanceof PhieuXuat && p.getDanhSachChiTiet().isEmpty()) {
            System.out.println("  Phiếu không có mặt hàng nào.");
            return false;
        }
        boolean ok = p.capNhatTon(tonKhoService); // đa hình
        if (ok) {
            System.out.println("  => Thành công phiếu " + p.getMaPhieu());
        } else {
            p.setTrangThai(PhieuKho.TU_CHOI);
            System.out.println("  => TỪ CHỐI phiếu " + p.getMaPhieu() + " (tồn kho không đổi).");
        }
        return ok;
    }

    // Xử lý lần lượt mọi phiếu đang chờ, trả về số phiếu thành công
    public int xuLyTatCaPhieuCho(TonKhoService tonKhoService) {
        int thanhCong = 0;
        for (int i = 0; i < n; i++) {
            if (PhieuKho.CHO_XU_LY.equals(ds[i].getTrangThai())
                    && xuLyPhieu(ds[i].getMaPhieu(), tonKhoService)) {
                thanhCong++;
            }
        }
        return thanhCong;
    }

    // ================= Đọc / ghi file text =================
    // XUAT|maPhieu;maKho;ngay;lyDo;trangThai;SP001:20,SP002:25;nguoiNhan   (PhieuXuat tự tạo dòng)
    // NHAP|maPhieu;maKho;ngay;trangThai;SP001:20:15000,SP002:25:8000   (SP:soLuong:donGia)
    public boolean ghiFile(String duongDan) {
        try {
            Path path = Paths.get(duongDan);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            List<String> cacDong = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (ds[i] instanceof PhieuXuat) {
                    cacDong.add("XUAT|" + ((PhieuXuat) ds[i]).toDongFile());
                } else if (ds[i] instanceof PhieuNhap) {
                    cacDong.add("NHAP|" + phieuNhapToDong((PhieuNhap) ds[i]));
                }
            }
            Files.write(path, cacDong, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
            return false;
        }
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
        return pn.getMaPhieu() + ";" + pn.getMaKho() + ";" + pn.getNgayLap() + ";"
                + pn.getTrangThai() + ";" + sb;
    }

    private PhieuNhap phieuNhapTuDong(String dong) {
        String[] p = dong.split(";", 5);
        if (p.length < 5) {
            return null;
        }
        List<ChiTietPhieu> chiTiet = new ArrayList<>();
        if (!p[4].trim().isEmpty()) {
            for (String s : p[4].split(",")) {
                String[] kv = s.split(":");
                double donGia = (kv.length > 2) ? Double.parseDouble(kv[2]) : 0;
                chiTiet.add(new ChiTietPhieu(kv[0], Integer.parseInt(kv[1]), donGia));
            }
        }
        PhieuNhap pn = new PhieuNhap(p[0], LocalDate.parse(p[2]), p[1], null, chiTiet);
        pn.setTrangThai(p[3]);
        return pn;
    }

    public boolean docFile(String duongDan) {
        try {
            List<String> cacDong = Files.readAllLines(Paths.get(duongDan), StandardCharsets.UTF_8);
            for (int i = 0; i < n; i++) {
                ds[i] = null;
            }
            n = 0;
            for (String dong : cacDong) {
                int gach = dong.indexOf('|');
                if (gach < 0) {
                    continue;
                }
                try {
                    String loai = dong.substring(0, gach);
                    String noiDung = dong.substring(gach + 1);
                    PhieuKho phieu = null;
                    if (loai.equals(LOAI_XUAT)) {
                        phieu = PhieuXuat.tuDongFile(noiDung);
                    } else if (loai.equals(LOAI_NHAP)) {
                        phieu = phieuNhapTuDong(noiDung);
                    }
                    if (phieu != null) {
                        them(phieu);
                    }
                } catch (RuntimeException e) {
                    System.out.println("Bỏ qua dòng phiếu lỗi: " + dong);
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
            return false;
        }
    }
}
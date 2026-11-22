package function;

import data.LichSuGiaoDichDAO;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.LichSuGiaoDich;
import model.TonKho;

// Phu trach: MinhNgoc
// Danh sách tồn kho dùng MẢNG TĨNH (TonKho[]): tra cứu, cập nhật tồn, cảnh báo, ghi lịch sử.
public class TonKhoService {
    public static final int SUC_CHUA_MAC_DINH = 1000;   // static: sức chứa mặc định của mảng

    private TonKho[] ds;                   // mảng tĩnh: kích thước cố định khi tạo
    private int n;                         // số phần tử đang dùng
    private LichSuGiaoDichDAO lichSuDAO;   // nơi ghi lịch sử (null = không ghi)

    public TonKhoService() {
        this(SUC_CHUA_MAC_DINH);
    }

    public TonKhoService(int sucChua) {
        if (sucChua <= 0) {
            sucChua = SUC_CHUA_MAC_DINH;
        }
        this.ds = new TonKho[sucChua];
        this.n = 0;
        this.lichSuDAO = null;
    }

    // Main gọi hàm này để nối với nơi lưu lịch sử
    public void setLichSuDAO(LichSuGiaoDichDAO lichSuDAO) {
        this.lichSuDAO = lichSuDAO;
    }

    public int getSoLuongDong() {
        return n;
    }

    public int getSucChua() {
        return ds.length;
    }

    public TonKho getTonKho(int viTri) {
        if (viTri < 0 || viTri >= n) {
            return null;
        }
        return ds[viTri];
    }

    // Ghi 1 dòng lịch sử (nếu đã nối với lichSuDAO)
    private void ghiLichSu(String loai, String maKho, String maSP, int soLuong,
                           int tonTruoc, int tonSau, String maPhieu, String ghiChu) {
        if (lichSuDAO == null) {
            return;
        }
        lichSuDAO.insert(LichSuGiaoDich.tao(maKho, maSP, loai, soLuong,
                tonTruoc, tonSau, maPhieu, ghiChu));
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

    // ================= a. Nhập n phần tử đầu tiên =================
    public void nhapDanhSachDau(Scanner sc, int soPhanTu) {
        for (int i = 0; i < soPhanTu; i++) {
            if (n >= ds.length) {
                System.out.println("Mảng đã đầy, dừng nhập.");
                break;
            }
            System.out.println("--- Nhập dòng tồn thứ " + (i + 1) + " ---");
            System.out.print("Mã kho: ");
            String maKho = sc.nextLine().trim();
            System.out.print("Mã sản phẩm: ");
            String maSP = sc.nextLine().trim();
            int sl = docSo(sc, "Số lượng tồn: ");
            int muc = docSo(sc, "Mức tối thiểu cảnh báo: ");
            if (!them(new TonKho(maKho, maSP, sl, muc))) {
                i--; // nhập lại dòng này nếu thêm thất bại
            }
        }
    }

    // ================= b. Xuất danh sách =================
    public void xuat() {
        System.out.println("----- DANH SÁCH TỒN KHO (" + n + "/" + ds.length + ") -----");
        for (int i = 0; i < n; i++) {
            System.out.println(ds[i]);
        }
    }

    public void inTonKho(String maKho) {
        System.out.println("----- TỒN KHO " + maKho + " -----");
        for (int i = 0; i < n; i++) {
            if (ds[i].getMaKho().equals(maKho)) {
                System.out.println(ds[i]);
            }
        }
    }

    // ================= c. Thêm 1 phần tử, k phần tử =================
    public boolean them(TonKho t) {
        if (t == null) {
            return false;
        }
        if (n >= ds.length) {
            System.out.println("Mảng đã đầy (" + ds.length + "), không thêm được.");
            return false;
        }
        if (timViTri(t.getMaKho(), t.getMaSanPham()) != -1) {
            System.out.println("Đã tồn tại dòng tồn " + t.getMaKho() + "-" + t.getMaSanPham());
            return false;
        }
        ds[n] = t;
        n++;
        return true;
    }

    // Thêm k phần tử lấy từ mảng 'mang'
    public int themK(TonKho[] mang, int k) {
        int daThem = 0;
        for (int i = 0; i < k && i < mang.length; i++) {
            if (them(mang[i])) {
                daThem++;
            }
        }
        return daThem;
    }

    // ================= d. Sửa theo mã (kiểm kê điều chỉnh) =================
    public boolean sua(String maKho, String maSanPham, int soLuongMoi, int mucToiThieuMoi) {
        int vt = timViTri(maKho, maSanPham);
        if (vt == -1 || !TonKho.hopLeSoLuong(soLuongMoi)) {
            return false;
        }
        int truoc = ds[vt].getSoLuong();
        ds[vt].setSoLuong(soLuongMoi);
        ds[vt].setMucToiThieu(mucToiThieuMoi);
        if (truoc != soLuongMoi) { // số lượng đổi thì ghi lịch sử điều chỉnh
            ghiLichSu(LichSuGiaoDich.LOAI_DIEU_CHINH, maKho, maSanPham,
                    Math.abs(soLuongMoi - truoc), truoc, soLuongMoi, "KIEM-KE", "Kiểm kê điều chỉnh tồn");
        }
        return true;
    }

    // ================= e. Xóa theo mã (dồn mảng sang trái) =================
    public boolean xoa(String maKho, String maSanPham) {
        int vt = timViTri(maKho, maSanPham);
        if (vt == -1) {
            return false;
        }
        for (int i = vt; i < n - 1; i++) {
            ds[i] = ds[i + 1];
        }
        ds[n - 1] = null;
        n--;
        return true;
    }

    // ================= f. Tìm kiếm nhiều khóa =================
    // Trả về vị trí trong mảng, -1 nếu không có
    private int timViTri(String maKho, String maSanPham) {
        for (int i = 0; i < n; i++) {
            if (ds[i].getMaKho().equals(maKho) && ds[i].getMaSanPham().equals(maSanPham)) {
                return i;
            }
        }
        return -1;
    }

    // Tìm CHÍNH XÁC theo (mã kho, mã sản phẩm)
    public TonKho timChinhXac(String maKho, String maSanPham) {
        int vt = timViTri(maKho, maSanPham);
        if (vt == -1) {
            return null;
        }
        return ds[vt];
    }

    public int laySoLuongTon(String maKho, String maSanPham) {
        TonKho t = timChinhXac(maKho, maSanPham);
        if (t == null) {
            return 0;
        }
        return t.getSoLuong();
    }

    // Tìm GẦN ĐÚNG: mã sản phẩm chứa từ khóa (không phân biệt hoa thường)
    public TonKho[] timGanDungMaSanPham(String tuKhoa) {
        return timNhieuKhoa("", tuKhoa, 0, Integer.MAX_VALUE);
    }

    // Tìm theo khoảng số lượng tồn
    public TonKho[] timTheoKhoangSoLuong(int min, int max) {
        return timNhieuKhoa("", "", min, max);
    }

    /**
     * Tìm NHIỀU KHÓA cùng lúc:
     * maKho   : để "" nếu không lọc, ngược lại phải trùng chính xác
     * tuKhoaSP: để "" nếu không lọc, ngược lại mã SP chứa từ khóa (gần đúng)
     * min,max : khoảng số lượng tồn
     */
    public TonKho[] timNhieuKhoa(String maKho, String tuKhoaSP, int min, int max) {
        if (maKho == null) {
            maKho = "";
        }
        String tk = (tuKhoaSP == null) ? "" : tuKhoaSP.trim().toLowerCase();
        TonKho[] tam = new TonKho[n];
        int dem = 0;
        for (int i = 0; i < n; i++) {
            boolean khop = true;
            if (!maKho.isEmpty() && !ds[i].getMaKho().equals(maKho)) {
                khop = false;
            }
            if (!tk.isEmpty() && !ds[i].getMaSanPham().toLowerCase().contains(tk)) {
                khop = false;
            }
            if (ds[i].getSoLuong() < min || ds[i].getSoLuong() > max) {
                khop = false;
            }
            if (khop) {
                tam[dem] = ds[i];
                dem++;
            }
        }
        TonKho[] ketQua = new TonKho[dem];
        for (int i = 0; i < dem; i++) {
            ketQua[i] = tam[i];
        }
        return ketQua;
    }

    public void inKetQua(String tieuDe, TonKho[] ketQua) {
        System.out.println("----- " + tieuDe + " (" + ketQua.length + " kết quả) -----");
        for (TonKho t : ketQua) {
            System.out.println(t);
        }
    }

    // ================= Sắp xếp (chọn trực tiếp) =================
    public void sapXepTheoSoLuong(boolean tangDan) {
        for (int i = 0; i < n - 1; i++) {
            int chon = i;
            for (int j = i + 1; j < n; j++) {
                boolean doi;
                if (tangDan) {
                    doi = ds[j].getSoLuong() < ds[chon].getSoLuong();
                } else {
                    doi = ds[j].getSoLuong() > ds[chon].getSoLuong();
                }
                if (doi) {
                    chon = j;
                }
            }
            TonKho tam = ds[i];
            ds[i] = ds[chon];
            ds[chon] = tam;
        }
    }

    // ================= g. Thống kê theo nhiều khóa =================
    public int tongTonTheoKho(String maKho) {
        int tong = 0;
        for (int i = 0; i < n; i++) {
            if (ds[i].getMaKho().equals(maKho)) {
                tong += ds[i].getSoLuong();
            }
        }
        return tong;
    }

    public int tongTonTheoSanPham(String maSanPham) {
        int tong = 0;
        for (int i = 0; i < n; i++) {
            if (ds[i].getMaSanPham().equals(maSanPham)) {
                tong += ds[i].getSoLuong();
            }
        }
        return tong;
    }

    public int demHetHang(String maKho) {
        int dem = 0;
        for (int i = 0; i < n; i++) {
            if (ds[i].getMaKho().equals(maKho) && ds[i].hetHang()) {
                dem++;
            }
        }
        return dem;
    }

    public int demSapHet(String maKho) {
        int dem = 0;
        for (int i = 0; i < n; i++) {
            if (ds[i].getMaKho().equals(maKho) && ds[i].sapHet()) {
                dem++;
            }
        }
        return dem;
    }

    public void inThongKe(String maKho) {
        System.out.println("----- THỐNG KÊ KHO " + maKho + " -----");
        System.out.println("Tổng số lượng tồn  : " + tongTonTheoKho(maKho));
        System.out.println("Số mặt hàng hết    : " + demHetHang(maKho));
        System.out.println("Số mặt hàng sắp hết: " + demSapHet(maKho));
    }

    // ================= h. Nghiệp vụ: nhập/xuất tồn + cảnh báo + lịch sử =================
    /** Cộng tồn (khi nhập). Chưa có dòng tồn thì tự tạo mới với mức tối thiểu mucToiThieuMoi. */
    public boolean tangTon(String maKho, String maSanPham, int soLuong, int mucToiThieuMoi,
                           String maPhieu, String ghiChu) {
        if (soLuong <= 0) {
            System.out.println("Số lượng nhập phải lớn hơn 0.");
            return false;
        }
        TonKho t = timChinhXac(maKho, maSanPham);
        if (t == null) {
            t = new TonKho(maKho, maSanPham, 0, mucToiThieuMoi);
            if (!them(t)) {
                return false;
            }
        }
        int truoc = t.getSoLuong();
        t.tang(soLuong);
        ghiLichSu(LichSuGiaoDich.LOAI_NHAP, maKho, maSanPham, soLuong,
                truoc, t.getSoLuong(), maPhieu, ghiChu);
        return true;
    }

    // Bản ngắn: dùng mức tối thiểu mặc định khi phải tạo dòng tồn mới
    public boolean tangTon(String maKho, String maSanPham, int soLuong, String maPhieu, String ghiChu) {
        return tangTon(maKho, maSanPham, soLuong, TonKho.MUC_TOI_THIEU_MAC_DINH, maPhieu, ghiChu);
    }

    /** Trừ tồn (khi xuất). Trả về false nếu không đủ hàng. Có ghi lịch sử và cảnh báo hết/sắp hết. */
    public boolean giamTon(String maKho, String maSanPham, int soLuong, String maPhieu, String ghiChu) {
        if (soLuong <= 0) {
            return false;
        }
        TonKho t = timChinhXac(maKho, maSanPham);
        if (t == null || !t.duHang(soLuong)) {
            return false;
        }
        int truoc = t.getSoLuong();
        t.giam(soLuong);
        ghiLichSu(LichSuGiaoDich.LOAI_XUAT, maKho, maSanPham, soLuong,
                truoc, t.getSoLuong(), maPhieu, ghiChu);
        if (t.canCanhBao()) {
            System.out.println("  " + t.thongBaoCanhBao());
        }
        return true;
    }

    // Lấy các dòng tồn của 1 kho đang hết hoặc sắp hết
    public TonKho[] timCanhBao(String maKho) {
        TonKho[] tam = new TonKho[n];
        int dem = 0;
        for (int i = 0; i < n; i++) {
            if (ds[i].getMaKho().equals(maKho) && ds[i].canCanhBao()) {
                tam[dem] = ds[i];
                dem++;
            }
        }
        TonKho[] ketQua = new TonKho[dem];
        for (int i = 0; i < dem; i++) {
            ketQua[i] = tam[i];
        }
        return ketQua;
    }

    public void inCanhBao(String maKho) {
        System.out.println("----- DANH SÁCH CẢNH BÁO KHO " + maKho + " -----");
        TonKho[] canhBao = timCanhBao(maKho);
        for (TonKho t : canhBao) {
            System.out.println("  " + t.thongBaoCanhBao());
        }
        if (canhBao.length == 0) {
            System.out.println("Không có mặt hàng nào sắp hết.");
        }
    }

    // ================= Đọc / ghi file text =================
    public boolean ghiFile(String duongDan) {
        try {
            Path path = Paths.get(duongDan);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            List<String> cacDong = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                cacDong.add(ds[i].toDongFile());
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
            for (int i = 0; i < n; i++) { // xóa dữ liệu cũ trong mảng
                ds[i] = null;
            }
            n = 0;
            for (String dong : cacDong) {
                if (dong.trim().isEmpty()) {
                    continue;
                }
                TonKho t = TonKho.tuDongFile(dong);
                if (t != null) {
                    them(t);
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
            return false;
        }
    }
}
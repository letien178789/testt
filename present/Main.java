package present;

import data.DataStore;
import data.ExcelExporter;
import function.DangNhapService;
import function.DanhMucService;
import function.DanhSachPhieuKho;
import function.NhaCungCapService;
import function.SanPhamService;
import function.ThongKeService;
import function.TonKhoService;
import function.XuatKhoService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import model.DanhMuc;
import model.Kho;
import model.LichSuGiaoDich;
import model.NhaCungCap;
import model.PhieuKho;
import model.PhieuXuat;
import model.SanPham;
import model.TaiKhoan;
import model.TonKho;

// Phu trach: TienLe
// Giao diện Console: đăng nhập -> menu -> Service (nghiệp vụ) -> DAO/DataStore (lưu file).
// Phân quyền: ADMIN làm mọi việc; NHAN_VIEN chỉ xem và xuất kho. Mọi menu con có "0. Quay lại".
// Chạy bình thường : java present.Main        (dữ liệu lưu ở thư mục data/)
// Chạy demo tự động: java present.Main demo   (dữ liệu demo ở thư mục data_demo/)
public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static DataStore ds;
    private static DanhMucService dmSv;
    private static SanPhamService spSv;
    private static ThongKeService tkSv;
    private static NhaCungCapService nccSv;
    private static TonKhoService tonSv;
    private static XuatKhoService xuatSv;
    private static DanhSachPhieuKho dsPhieu = new DanhSachPhieuKho();
    private static DangNhapService dnSv = new DangNhapService();
    private static TaiKhoan nguoiDung;   // tài khoản đang đăng nhập

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("demo")) {
            chayDemo();
            return;
        }
        ds = new DataStore("data");
        ds.loadAll(); // nạp dữ liệu đã lưu lần trước (nếu có)
        // 3 Service này đang dùng phiên bản mảng tĩnh.
        dmSv = new DanhMucService();
        spSv = new SanPhamService(ds.getSanPhamDAO(), dmSv);
        tkSv = new ThongKeService();

        // DataStore vẫn lưu dữ liệu sản phẩm bằng DAO, nên sau khi load file
        // ta đưa danh mục từ DAO vào mảng tĩnh của DanhMucService.
        for (DanhMuc dm : ds.getDanhMucDAO().findAll()) {
            dmSv.themDanhMuc(dm);
        }
        tonSv = ds.getTonKhoService();
        tonSv.setLichSuDAO(ds.getLichSuGiaoDichDAO()); // từ giờ nhập/xuất tồn tự ghi lịch sử
        xuatSv = new XuatKhoService(tonSv);
        for (PhieuKho p : ds.getPhieuKhoDAO().findAll()) {
            if (p instanceof PhieuXuat) {
                xuatSv.them((PhieuXuat) p); // đưa các phiếu xuất đã lưu vào danh sách xuất kho
            }
        }
        nccSv = new NhaCungCapService();
        for (NhaCungCap n : ds.getNhaCungCapDAO().findAll()) {
            nccSv.them(n); // NhaCungCapService có kho riêng -> nạp dữ liệu đã lưu vào đó
        }

        boolean thoat = false;
        while (!thoat) {
            if (!dangNhap()) {
                break; // đóng chương trình khi người dùng thoát hoặc sai quá 3 lần
            }
            boolean dangXuat = false;
            while (!dangXuat && !thoat) {
                System.out.println("\n===== QUẢN LÝ KHO =====");
                System.out.println("Tài khoản: " + nguoiDung);
                System.out.println("1. Danh mục");
                System.out.println("2. Sản phẩm");
                System.out.println("3. Kho & Nhà cung cấp");
                System.out.println("4. Tồn kho");
                System.out.println("5. Xuất kho");
                System.out.println("6. Thống kê");
                System.out.println("7. Xuất CSV (mở bằng Excel)");
                System.out.println("8. Lưu dữ liệu");
                System.out.println("9. Chạy DEMO tự động (nhập tồn -> xuất -> cảnh báo -> lịch sử)");
                System.out.println("10. Đăng xuất (đổi tài khoản)");
                System.out.println("0. Thoát");
                String chon = hoi("Chọn: ");
                switch (chon) {
                    case "1": menuDanhMuc(); break;
                    case "2": menuSanPham(); break;
                    case "3": menuKhoVaNCC(); break;
                    case "4": menuTonKho(); break;
                    case "5": menuXuatKho(); break;
                    case "6": menuThongKe(); break;
                    case "7": menuXuatCSV(); break;
                    case "8": luuTatCa(); break;
                    case "9": chayDemo(); break;
                    case "10":
                        if (hoi("Lưu trước khi đăng xuất? (y/n): ").equalsIgnoreCase("y")) {
                            luuTatCa();
                        }
                        nguoiDung = null;
                        dangXuat = true;
                        break;
                    case "0":
                        if (hoi("Lưu trước khi thoát? (y/n): ").equalsIgnoreCase("y")) {
                            luuTatCa();
                        }
                        thoat = true;
                        break;
                    default: System.out.println("Lựa chọn không hợp lệ.");
                }
            }
        }
        System.out.println("Tạm biệt!");
    }

    // ================= Đăng nhập và phân quyền =================
    // Cho nhập tối đa 3 lần. Trả về false nếu người dùng thoát (nhập 0) hoặc sai quá 3 lần.
    private static boolean dangNhap() {
        System.out.println("\n===== ĐĂNG NHẬP =====");
        for (int lan = 1; lan <= 3; lan++) {
            String ten = hoi("Tên đăng nhập (0 = thoát): ");
            if (ten.equals("0")) {
                return false;
            }
            String mk = hoi("Mật khẩu: ");
            TaiKhoan tk = dnSv.dangNhap(ten, mk);
            if (tk != null) {
                nguoiDung = tk;
                System.out.println("Xin chào " + tk + ".");
                return true;
            }
            System.out.println("Sai tên đăng nhập hoặc mật khẩu (" + lan + "/3).");
        }
        System.out.println("Nhập sai 3 lần, chương trình đóng.");
        return false;
    }

    // Việc cần quyền Admin: nhân viên bị chặn lại và hiện thông báo
    private static boolean coQuyenAdmin() {
        if (nguoiDung != null && nguoiDung.laAdmin()) {
            return true;
        }
        System.out.println("Bạn không có quyền: chức năng này chỉ dành cho Quản trị viên.");
        return false;
    }

    // Người dùng nhập 0 ở câu hỏi đầu tiên nghĩa là muốn hủy thao tác đang làm
    private static boolean huy(String s) {
        if (s.equals("0")) {
            System.out.println("Đã hủy, quay lại menu.");
            return true;
        }
        return false;
    }

    // ================= Nhập liệu an toàn =================
    private static String hoi(String nhac) {
        System.out.print(nhac);
        if (!sc.hasNextLine()) { // hết dữ liệu nhập (Ctrl+D / đóng luồng) thì thoát êm, không báo lỗi
            System.out.println("\nKết thúc nhập liệu, thoát chương trình.");
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    private static int hoiSo(String nhac) {
        while (true) {
            try {
                return Integer.parseInt(hoi(nhac));
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số nguyên.");
            }
        }
    }

    private static BigDecimal hoiGia(String nhac) {
        while (true) {
            try {
                return new BigDecimal(hoi(nhac));
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số (ví dụ 15000).");
            }
        }
    }

    // ================= Lưu =================
    private static void luuTatCa() {
        // DanhMucService đang giữ danh mục bằng mảng tĩnh -> chép sang DAO trước khi ghi file.
        ds.getDanhMucDAO().clear();
        for (DanhMuc dm : dmSv.layTatCa()) {
            ds.getDanhMucDAO().insert(dm);
        }

        // NhaCungCapService giữ dữ liệu riêng -> chép sang DAO trước khi ghi file
        ds.getNhaCungCapDAO().clear();
        for (NhaCungCap n : nccSv.layTatCa()) {
            ds.getNhaCungCapDAO().insert(n);
        }
        System.out.println(ds.saveAll() ? "Đã lưu dữ liệu vào thư mục data/." : "Lưu dữ liệu có lỗi!");
    }

    // ================= 1. Danh mục =================
    private static void menuDanhMuc() {
        while (true) {
            System.out.println("\n--- DANH MỤC ---");
            System.out.println("1. Xem  2. Thêm (Admin)  3. Xóa (Admin)  0. Quay lại");
            String c = hoi("Chọn: ");
            if (c.equals("0")) {
                return;
            }
            try {
                if (c.equals("1")) {
                    for (DanhMuc d : dmSv.layTatCa()) {
                        System.out.println(d.getMaDanhMuc() + " | " + d.getTenDanhMuc() + " | " + d.getMoTa());
                    }
                } else if (c.equals("2")) {
                    if (!coQuyenAdmin()) {
                        continue;
                    }
                    String ma = hoi("Mã danh mục (0 = hủy): ");
                    if (huy(ma)) {
                        continue;
                    }
                    String ten = hoi("Tên danh mục: ");
                    String moTa = hoi("Mô tả: ");
                    dmSv.themDanhMuc(new DanhMuc(ma, ten, moTa));
                    System.out.println("Đã thêm danh mục.");
                } else if (c.equals("3")) {
                    if (!coQuyenAdmin()) {
                        continue;
                    }
                    String ma = hoi("Mã danh mục cần xóa (0 = hủy): ");
                    if (huy(ma)) {
                        continue;
                    }
                    dmSv.xoaDanhMuc(ma);
                    System.out.println("Đã xóa danh mục.");
                } else {
                    System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    // ================= 2. Sản phẩm =================
    private static void inSanPham(List<SanPham> danhSach) {
        System.out.println(String.format("%-8s %-8s %-25s %-6s %12s %12s",
                "Mã", "Mã DM", "Tên", "ĐVT", "Giá nhập", "Giá bán"));
        for (SanPham sp : danhSach) {
            System.out.println(String.format("%-8s %-8s %-25s %-6s %12s %12s",
                    sp.getMaSanPham(), sp.getMaDanhMuc(), sp.getTenSanPham(), sp.getDonViTinh(),
                    sp.getGiaNhap(), sp.getGiaBan()));
        }
    }

    private static void menuSanPham() {
        while (true) {
            System.out.println("\n--- SẢN PHẨM ---");
            System.out.println("1. Xem tất cả  2. Thêm (Admin)  3. Tìm theo tên  4. Lọc theo danh mục  5. Xóa (Admin)  0. Quay lại");
            String c = hoi("Chọn: ");
            if (c.equals("0")) {
                return;
            }
            try {
                if (c.equals("1")) {
                    inSanPham(spSv.layTatCaSanPham());
                } else if (c.equals("2")) {
                    if (!coQuyenAdmin()) {
                        continue;
                    }
                    String ma = hoi("Mã sản phẩm (0 = hủy): ");
                    if (huy(ma)) {
                        continue;
                    }
                    String maDM = hoi("Mã danh mục: ");
                    String ten = hoi("Tên sản phẩm: ");
                    String dvt = hoi("Đơn vị tính: ");
                    BigDecimal giaNhap = hoiGia("Giá nhập: ");
                    BigDecimal giaBan = hoiGia("Giá bán: ");
                    spSv.themSanPham(new SanPham(ma, maDM, ten, dvt, giaNhap, giaBan));
                    System.out.println("Đã thêm sản phẩm.");
                } else if (c.equals("3")) {
                    inSanPham(spSv.timSanPhamTheoTen(hoi("Từ khóa: ")));
                } else if (c.equals("4")) {
                    inSanPham(spSv.laySanPhamTheoDanhMuc(hoi("Mã danh mục: ")));
                } else if (c.equals("5")) {
                    if (!coQuyenAdmin()) {
                        continue;
                    }
                    String ma = hoi("Mã sản phẩm cần xóa (0 = hủy): ");
                    if (huy(ma)) {
                        continue;
                    }
                    spSv.xoaSanPham(ma);
                    System.out.println("Đã xóa sản phẩm.");
                } else {
                    System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    // ================= 3. Kho & Nhà cung cấp =================
    private static void menuKhoVaNCC() {
        while (true) {
            System.out.println("\n--- KHO & NHÀ CUNG CẤP ---");
            System.out.println("1. Xem kho  2. Thêm kho (Admin)  3. Xem NCC  4. Thêm NCC (Admin)  0. Quay lại");
            String c = hoi("Chọn: ");
            if (c.equals("0")) {
                return;
            }
            try {
                if (c.equals("1")) {
                    for (Kho k : ds.getKhoDAO().findAll()) {
                        System.out.println(k);
                    }
                } else if (c.equals("2")) {
                    if (!coQuyenAdmin()) {
                        continue;
                    }
                    String ma = hoi("Mã kho (0 = hủy): ");
                    if (huy(ma)) {
                        continue;
                    }
                    ds.getKhoDAO().insert(new Kho(ma, hoi("Tên kho: "), hoi("Địa chỉ: ")));
                    System.out.println("Đã thêm kho.");
                } else if (c.equals("3")) {
                    for (NhaCungCap n : nccSv.layTatCa()) {
                        System.out.println(n);
                    }
                } else if (c.equals("4")) {
                    if (!coQuyenAdmin()) {
                        continue;
                    }
                    String ma = hoi("Mã NCC (0 = hủy): ");
                    if (huy(ma)) {
                        continue;
                    }
                    String ten = hoi("Tên NCC: ");
                    String dc = hoi("Địa chỉ: ");
                    String sdt = hoi("Số điện thoại: ");
                    String email = hoi("Email: ");
                    nccSv.them(new NhaCungCap(ma, ten, dc, sdt, email));
                    System.out.println("Đã thêm nhà cung cấp.");
                } else {
                    System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    // ================= 4. Tồn kho =================
    // Nhập/xuất tồn đã được TonKhoService tự ghi lịch sử, Main không cần ghi nữa.
    private static void menuTonKho() {
        while (true) {
            System.out.println("\n--- TỒN KHO ---");
            System.out.println("1. Xem tồn  2. Nhập thêm hàng (Admin)  3. Cảnh báo sắp hết  4. Tìm theo mã SP");
            System.out.println("5. Xem lịch sử  6. Kiểm kê - sửa tồn (Admin)  7. Tìm nhiều khóa  8. Sắp xếp theo số lượng  0. Quay lại");
            String c = hoi("Chọn: ");
            if (c.equals("0")) {
                return;
            }
            if (c.equals("1")) {
                String maKho = hoi("Mã kho (Enter = xem tất cả): ");
                if (maKho.isEmpty()) {
                    tonSv.xuat();
                } else {
                    tonSv.inTonKho(maKho);
                }
            } else if (c.equals("2")) {
                if (!coQuyenAdmin()) {
                    continue;
                }
                String maKho = hoi("Mã kho (0 = hủy): ");
                if (huy(maKho)) {
                    continue;
                }
                String maSP = hoi("Mã sản phẩm: ");
                if (!ds.getKhoDAO().existsById(maKho)) {
                    System.out.println("Kho chưa tồn tại - hãy thêm kho ở mục 3 trước.");
                    continue;
                }
                if (!ds.getSanPhamDAO().existsById(maSP)) {
                    System.out.println("Sản phẩm chưa tồn tại - hãy thêm ở mục 2 trước.");
                    continue;
                }
                int soLuong = hoiSo("Số lượng nhập: ");
                if (soLuong <= 0) {
                    System.out.println("Số lượng phải > 0.");
                    continue;
                }
                int muc = TonKho.MUC_TOI_THIEU_MAC_DINH;
                if (tonSv.timChinhXac(maKho, maSP) == null) {
                    muc = hoiSo("Mức tồn tối thiểu cho dòng tồn mới: ");
                }
                if (tonSv.tangTon(maKho, maSP, soLuong, muc, "NHAP-TAY", "Nhập hàng thủ công")) {
                    System.out.println("Đã cập nhật tồn kho: " + tonSv.timChinhXac(maKho, maSP));
                }
            } else if (c.equals("3")) {
                tonSv.inCanhBao(hoi("Mã kho: "));
            } else if (c.equals("4")) {
                tonSv.inKetQua("Kết quả tìm kiếm", tonSv.timGanDungMaSanPham(hoi("Từ khóa mã SP: ")));
            } else if (c.equals("5")) {
                xemLichSu();
            } else if (c.equals("6")) {
                if (!coQuyenAdmin()) {
                    continue;
                }
                String maKho = hoi("Mã kho (0 = hủy): ");
                if (huy(maKho)) {
                    continue;
                }
                String maSP = hoi("Mã sản phẩm: ");
                int sl = hoiSo("Số lượng thực tế đếm được: ");
                int muc = hoiSo("Mức tối thiểu mới: ");
                System.out.println(tonSv.sua(maKho, maSP, sl, muc) ? "Đã điều chỉnh tồn." : "Không tìm thấy dòng tồn hoặc số lượng không hợp lệ.");
            } else if (c.equals("7")) {
                String maKho = hoi("Mã kho (Enter = bỏ qua): ");
                String tk = hoi("Từ khóa mã SP (Enter = bỏ qua): ");
                int min = hoiSo("Tồn tối thiểu cần tìm (nhập 0 nếu không lọc): ");
                int max = hoiSo("Tồn tối đa cần tìm (nhập -1 nếu không lọc): ");
                if (max < 0) {
                    max = Integer.MAX_VALUE;
                }
                tonSv.inKetQua("Kết quả tìm nhiều khóa", tonSv.timNhieuKhoa(maKho, tk, min, max));
            } else if (c.equals("8")) {
                tonSv.sapXepTheoSoLuong(hoi("Tăng dần? (y = tăng, n = giảm): ").equalsIgnoreCase("y"));
                tonSv.xuat();
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    private static void xemLichSu() {
        String maSP = hoi("Lọc theo mã SP (Enter = không lọc): ");
        List<LichSuGiaoDich> ls;
        if (maSP.isEmpty()) {
            ls = ds.getLichSuGiaoDichDAO().findAll();
        } else {
            ls = ds.getLichSuGiaoDichDAO().findByMaSanPham(maSP);
        }
        if (ls.isEmpty()) {
            System.out.println("Chưa có giao dịch nào.");
        }
        for (LichSuGiaoDich x : ls) {
            System.out.println(x);
        }
    }

    // ================= 5. Xuất kho (nhân viên và admin đều dùng được) =================
    private static String taoMaPhieuXuat() {
        int n = ds.getPhieuKhoDAO().findAll().size() + 1;
        String ma;
        do {
            ma = String.format("PX%03d", n++);
        } while (ds.getPhieuKhoDAO().existsById(ma));
        return ma;
    }

    private static void menuXuatKho() {
        while (true) {
            System.out.println("\n--- XUẤT KHO ---");
            System.out.println("1. Tạo phiếu xuất và xử lý ngay  2. Xử lý phiếu đang chờ  3. Danh sách phiếu");
            System.out.println("4. Thống kê phiếu xuất  5. Tất cả phiếu kho (nhập + xuất)  0. Quay lại");
            String c = hoi("Chọn: ");
            if (c.equals("0")) {
                return;
            }
            if (c.equals("1")) {
                String maKho = hoi("Mã kho (0 = hủy): ");
                if (huy(maKho)) {
                    continue;
                }
                if (!ds.getKhoDAO().existsById(maKho)) {
                    System.out.println("Kho chưa tồn tại.");
                    continue;
                }
                PhieuXuat px = new PhieuXuat(taoMaPhieuXuat(), maKho, hoi("Lý do xuất: "), hoi("Người nhận hàng: "));
                while (true) {
                    String maSP = hoi("Mã sản phẩm (Enter để kết thúc): ");
                    if (maSP.isEmpty()) {
                        break;
                    }
                    int sl = hoiSo("Số lượng: ");
                    if (sl <= 0) {
                        System.out.println("Số lượng phải > 0.");
                        continue;
                    }
                    px.themChiTiet(maSP, sl);
                }
                if (px.getDanhSachChiTiet().isEmpty()) {
                    System.out.println("Phiếu chưa có mặt hàng nào, đã hủy phiếu.");
                    continue;
                }
                ds.getPhieuKhoDAO().insert(px);
                xuatSv.xuatKho(px);
            } else if (c.equals("2")) {
                String ma = hoi("Mã phiếu (0 = hủy): ");
                if (huy(ma)) {
                    continue;
                }
                PhieuKho p = ds.getPhieuKhoDAO().findById(ma);
                if (p instanceof PhieuXuat) {
                    xuatSv.xuatKho((PhieuXuat) p);
                } else {
                    System.out.println("Không tìm thấy phiếu xuất.");
                }
            } else if (c.equals("3")) {
                List<PhieuKho> dsP = ds.getPhieuKhoDAO().findAll();
                if (dsP.isEmpty()) {
                    System.out.println("Chưa có phiếu nào.");
                }
                for (PhieuKho p : dsP) {
                    p.inPhieu(); // đa hình: mỗi loại phiếu tự in theo cách của nó
                }
            } else if (c.equals("4")) {
                xuatSv.inThongKe();
            } else if (c.equals("5")) {
                menuTatCaPhieu();
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    // Quản lý chung phiếu nhập + phiếu xuất bằng DanhSachPhieuKho (mảng PhieuKho[] - đa hình)
    private static void menuTatCaPhieu() {
        while (true) {
            dsPhieu.napTu(ds.getPhieuKhoDAO().findAll()); // luôn lấy dữ liệu mới nhất
            System.out.println("\n--- TẤT CẢ PHIẾU KHO ---");
            System.out.println("1. Xem tất cả  2. Tìm nhiều khóa  3. Thống kê  4. Xử lý mọi phiếu đang chờ (Admin)  5. Sắp xếp theo mã  0. Quay lại");
            String c = hoi("Chọn: ");
            if (c.equals("0")) {
                return;
            }
            if (c.equals("1")) {
                dsPhieu.xuat();
            } else if (c.equals("2")) {
                String loai = hoi("Loại NHAP/XUAT (Enter = bỏ qua): ");
                String kho = hoi("Mã kho (Enter = bỏ qua): ");
                String tt = hoi("Trạng thái CHO_XU_LY/DA_XU_LY/TU_CHOI (Enter = bỏ qua): ");
                String lyDo = hoi("Từ khóa lý do (Enter = bỏ qua): ");
                dsPhieu.inKetQua("Kết quả tìm kiếm", dsPhieu.timNhieuKhoa(loai, kho, tt, lyDo));
            } else if (c.equals("3")) {
                dsPhieu.inThongKe();
            } else if (c.equals("4")) {
                if (!coQuyenAdmin()) { // có thể xử lý cả phiếu nhập (cộng tồn) nên chỉ Admin
                    continue;
                }
                System.out.println("Đã xử lý thành công " + dsPhieu.xuLyTatCaPhieuCho(tonSv) + " phiếu.");
            } else if (c.equals("5")) {
                dsPhieu.sapXepTheoMa();
                dsPhieu.xuat();
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    // ================= 6. Thống kê =================
    private static void menuThongKe() {
        List<SanPham> danhSachSanPham = spSv.layTatCaSanPham();
        SanPham[] mangSanPham = danhSachSanPham.toArray(new SanPham[0]);
        int soLuongSP = mangSanPham.length;

        DanhMuc[] mangDanhMuc = dmSv.layTatCa();
        int soLuongDM = dmSv.getSoLuong();

        System.out.println("Tổng số sản phẩm: "
                + tkSv.demSoSanPham(mangSanPham, soLuongSP));
        System.out.println("Tổng số danh mục: "
                + tkSv.demSoDanhMuc(mangDanhMuc, soLuongDM));

        for (int i = 0; i < soLuongDM; i++) {
            DanhMuc dm = mangDanhMuc[i];
            int soSanPham = tkSv.demSanPhamTheoDanhMuc(
                    mangSanPham, soLuongSP, dm.getMaDanhMuc());
            System.out.println("  Danh mục " + dm.getMaDanhMuc()
                    + ": " + soSanPham + " sản phẩm");
        }
        xuatSv.inThongKe();
        String maKho = hoi("Nhập mã kho để xem thống kê tồn (Enter để bỏ qua): ");
        if (!maKho.isEmpty()) {
            tonSv.inThongKe(maKho);
            System.out.println("Tổng đã xuất khỏi kho: " + xuatSv.tongDaXuatTheoKho(maKho));
        }
    }

    // ================= 7. Xuất CSV =================
    private static void menuXuatCSV() {
        String thuMuc = ds.getFileStorage().getThuMuc() + "/export/";
        while (true) {
            System.out.println("\n--- XUẤT CSV ---");
            System.out.println("1. Sản phẩm  2. Tồn kho  3. Lịch sử giao dịch  4. Một phiếu kho  0. Quay lại");
            String c = hoi("Chọn: ");
            if (c.equals("0")) {
                return;
            }
            String file = null;
            boolean ok = false;
            if (c.equals("1")) {
                file = thuMuc + "sanpham.csv";
                ok = ExcelExporter.xuatSanPham(ds.getSanPhamDAO().findAll(), file);
            } else if (c.equals("2")) {
                file = thuMuc + "tonkho.csv";
                ok = ExcelExporter.xuatTonKho(ds.getTonKhoDAO().findAll(), file);
            } else if (c.equals("3")) {
                file = thuMuc + "lichsu.csv";
                ok = ExcelExporter.xuatLichSu(ds.getLichSuGiaoDichDAO().findAll(), file);
            } else if (c.equals("4")) {
                String ma = hoi("Mã phiếu (0 = hủy): ");
                if (huy(ma)) {
                    continue;
                }
                PhieuKho p = ds.getPhieuKhoDAO().findById(ma);
                if (p == null) {
                    System.out.println("Không tìm thấy phiếu.");
                    continue;
                }
                file = thuMuc + "phieu_" + ma + ".csv";
                ok = ExcelExporter.xuatPhieu(p, file);
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
            }
            if (file != null) {
                System.out.println(ok ? "Đã xuất file: " + file : "Xuất file thất bại.");
            }
        }
    }

    // ================= 9. DEMO TỰ ĐỘNG =================
    // Chạy trên 1 DataStore riêng (thư mục data_demo/), không đụng tới dữ liệu thật trong data/.
    private static void tieuDe(String s) {
        System.out.println("\n########## " + s + " ##########");
    }

    private static void chayDemo() {
        System.out.println("===== DEMO: NHẬP TỒN -> XUẤT HÀNG -> CẬP NHẬT TỒN -> CẢNH BÁO -> LƯU LỊCH SỬ =====");
        DataStore d = new DataStore("data_demo", 100); // không loadAll: demo luôn bắt đầu từ trống
        TonKhoService ton = d.getTonKhoService();
        ton.setLichSuDAO(d.getLichSuGiaoDichDAO());
        XuatKhoService xuat = new XuatKhoService(ton);
        DanhMucService dm = new DanhMucService();
        SanPhamService sp = new SanPhamService(d.getSanPhamDAO(), dm);

        tieuDe("BƯỚC 0: Chuẩn bị danh mục, kho, sản phẩm");
        dm.themDanhMuc(new DanhMuc("DM01", "Đồ uống", "Nước giải khát"));
        d.getKhoDAO().insert(new Kho("K01", "Kho Tổng", "Quận 1, TP.HCM"));
        sp.themSanPham(new SanPham("SP001", "DM01", "Nước suối 500ml", "chai",
                new BigDecimal("3000"), new BigDecimal("5000")));
        sp.themSanPham(new SanPham("SP002", "DM01", "Nước cam 330ml", "lon",
                new BigDecimal("8000"), new BigDecimal("12000")));
        sp.themSanPham(new SanPham("SP003", "DM01", "Trà xanh 455ml", "chai",
                new BigDecimal("7000"), new BigDecimal("10000")));
        System.out.println("Đã tạo 1 danh mục, 1 kho (K01), 3 sản phẩm.");

        tieuDe("BƯỚC 1: Nhập tồn đầu kỳ (SL, mức tối thiểu): SP001 (50,10)  SP002 (15,10)  SP003 (30,5)");
        ton.tangTon("K01", "SP001", 50, 10, "NHAP-DEMO", "Nhập tồn đầu kỳ");
        ton.tangTon("K01", "SP002", 15, 10, "NHAP-DEMO", "Nhập tồn đầu kỳ");
        ton.tangTon("K01", "SP003", 30, 5, "NHAP-DEMO", "Nhập tồn đầu kỳ");
        ton.inTonKho("K01");

        tieuDe("BƯỚC 2: Phiếu PX001 xuất SP001 x35 và SP002 x6 -> đủ hàng, trừ tồn, SP002 còn 9 (sắp hết)");
        PhieuXuat px1 = new PhieuXuat("PX001", "K01", "Bán cho đại lý A", "Đại lý A");
        px1.themChiTiet("SP001", 35);
        px1.themChiTiet("SP002", 6);
        d.getPhieuKhoDAO().insert(px1);
        xuat.xuatKho(px1);

        tieuDe("BƯỚC 3: Phiếu PX002 xuất SP002 x9 -> SP002 về 0 (HẾT HÀNG)");
        PhieuXuat px2 = new PhieuXuat("PX002", "K01", "Bán lẻ", "Khách lẻ");
        px2.themChiTiet("SP002", 9);
        d.getPhieuKhoDAO().insert(px2);
        xuat.xuatKho(px2);

        tieuDe("BƯỚC 4: Phiếu PX003 xuất SP001 x100 (tồn chỉ còn 15) -> bị TỪ CHỐI, tồn không đổi");
        PhieuXuat px3 = new PhieuXuat("PX003", "K01", "Đơn hàng lớn", "Đại lý B");
        px3.themChiTiet("SP001", 100);
        d.getPhieuKhoDAO().insert(px3);
        xuat.xuatKho(px3);

        tieuDe("BƯỚC 5: Tồn kho sau khi xuất và danh sách cảnh báo");
        ton.inTonKho("K01");
        ton.inCanhBao("K01");
        ton.inThongKe("K01");

        tieuDe("BƯỚC 6: Lịch sử giao dịch (PX003 bị từ chối nên không có dòng lịch sử)");
        for (LichSuGiaoDich ls : d.getLichSuGiaoDichDAO().findAll()) {
            System.out.println(ls);
        }
        xuat.inThongKe();

        tieuDe("BƯỚC 6b: DanhSachPhieuKho (mảng PhieuKho[] đa hình) thống kê các phiếu vừa tạo");
        DanhSachPhieuKho dsp = new DanhSachPhieuKho(50);
        dsp.napTu(d.getPhieuKhoDAO().findAll());
        dsp.inThongKe();
        dsp.inKetQua("Phiếu xuất bị từ chối", dsp.timNhieuKhoa("XUAT", "", PhieuKho.TU_CHOI, ""));
        System.out.println("Tổng đã xuất khỏi K01: " + dsp.tongSoLuongDaXuat("K01"));

        tieuDe("BƯỚC 7: Lưu file rồi nạp lại bằng DataStore mới để kiểm tra");
        System.out.println(d.saveAll() ? "Đã lưu vào thư mục data_demo/." : "Lưu có lỗi!");
        DataStore d2 = new DataStore("data_demo", 100);
        d2.loadAll();
        System.out.println("Sau khi nạp lại: " + d2.getTonKhoDAO().findAll().size() + " dòng tồn, "
                + d2.getLichSuGiaoDichDAO().findAll().size() + " dòng lịch sử, "
                + d2.getPhieuKhoDAO().findAll().size() + " phiếu.");
        for (PhieuKho p : d2.getPhieuKhoDAO().findAll()) {
            p.inPhieu();
        }
        System.out.println("===== HẾT DEMO =====");
    }
}

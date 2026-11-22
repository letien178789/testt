package model;

// Phu trach: MinhNgoc
// Tài khoản đăng nhập: tên đăng nhập, mật khẩu và vai trò (ADMIN hoặc NHAN_VIEN).
public class TaiKhoan {
    // ----- Thành phần static -----
    public static final String ADMIN = "ADMIN";          // được làm mọi việc
    public static final String NHAN_VIEN = "NHAN_VIEN";  // chỉ xem và xuất kho
    private static int tongdatao = 0;                    // đếm số tài khoản đã tạo

    public static int getTongdatao() {
        return tongdatao;
    }

    public static boolean hopLeVaiTro(String vaiTro) {
        return ADMIN.equals(vaiTro) || NHAN_VIEN.equals(vaiTro);
    }

    // ----- Thuộc tính -----
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;

    // ----- Hàm thiết lập (constructor) -----
    public TaiKhoan() {
        this.tenDangNhap = "";
        this.matKhau = "";
        this.vaiTro = NHAN_VIEN;
        tongdatao++;
    }

    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro) {
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setVaiTro(vaiTro);
        tongdatao++;
    }

    // ----- Getter / Setter -----
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = (tenDangNhap == null) ? "" : tenDangNhap.trim();
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = (matKhau == null) ? "" : matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    // Vai trò sai thì mặc định là nhân viên (quyền thấp nhất cho an toàn)
    public void setVaiTro(String vaiTro) {
        if (hopLeVaiTro(vaiTro)) {
            this.vaiTro = vaiTro;
        } else {
            this.vaiTro = NHAN_VIEN;
        }
    }

    // ----- Nghiệp vụ nhỏ -----
    public boolean laAdmin() {
        return ADMIN.equals(vaiTro);
    }

    public boolean khopMatKhau(String matKhauNhap) {
        return matKhau.equals(matKhauNhap);
    }

    public String tenVaiTro() {
        if (laAdmin()) {
            return "Quản trị viên";
        }
        return "Nhân viên";
    }

    // Không in mật khẩu ra màn hình
    @Override
    public String toString() {
        return tenDangNhap + " (" + tenVaiTro() + ")";
    }
}
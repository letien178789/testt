package function;

import java.util.ArrayList;
import model.TaiKhoan;

// Phu trach: MinhNgoc
// Quản lý danh sách tài khoản (mảng động ArrayList) và kiểm tra đăng nhập.
public class DangNhapService {
    private ArrayList<TaiKhoan> ds;

    // Tạo sẵn 2 tài khoản mẫu để demo
    public DangNhapService() {
        this.ds = new ArrayList<>();
        them(new TaiKhoan("admin", "admin123", TaiKhoan.ADMIN));
        them(new TaiKhoan("nhanvien", "nv123", TaiKhoan.NHAN_VIEN));
    }

    public int size() {
        return ds.size();
    }

    // Thêm tài khoản, không cho trùng tên đăng nhập
    public boolean them(TaiKhoan tk) {
        if (tk == null || tk.getTenDangNhap().isEmpty()) {
            return false;
        }
        if (timTheoTen(tk.getTenDangNhap()) != null) {
            return false;
        }
        ds.add(tk);
        return true;
    }

    // Tìm theo tên đăng nhập (chính xác)
    public TaiKhoan timTheoTen(String ten) {
        if (ten == null) {
            return null;
        }
        for (TaiKhoan tk : ds) {
            if (tk.getTenDangNhap().equals(ten.trim())) {
                return tk;
            }
        }
        return null;
    }

    // Đúng tên và mật khẩu thì trả về tài khoản, sai thì trả về null
    public TaiKhoan dangNhap(String ten, String matKhau) {
        TaiKhoan tk = timTheoTen(ten);
        if (tk != null && tk.khopMatKhau(matKhau)) {
            return tk;
        }
        return null;
    }
}
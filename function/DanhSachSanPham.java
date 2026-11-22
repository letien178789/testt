//Note: DanhSachSanPham.java
//Bao gồm: Các phương thức để quản lý sản phẩm : Lấy tất cả sản phẩm, tìm sản phẩm theo mã, tìm sản phẩm theo danh mục, tìm sản phẩm theo tên, kiểm tra tồn tại theo mã, thêm, cập nhật, xóa sản phẩm
//Setting chuẩn cho hàm SanPhamDAO: findAll, findById, existsById, insert, update, delete, findByDanhMuc, searchByName


package function;
import data.SanPhamDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import model.SanPham;

public class DanhSachSanPham implements SanPhamDAO {
    private final List<SanPham> danhSach = new ArrayList<>();
//Lấy tất cả sản phẩm
    @Override
    public List<SanPham> findAll() {
        List<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : danhSach) {
            ketQua.add(new SanPham(sp));
        }
        return ketQua;
    }
//Tìm sản phẩm theo mã
    @Override
    public SanPham findById(String maSanPham) {
        if (maSanPham == null) {
            return null;
        }
        String maCanTim = maSanPham.trim();
        for (SanPham sp : danhSach) {
            if (sp.getMaSanPham().equals(maCanTim)) {
                return new SanPham(sp);
            }
        }
        return null;
    }
//Đảm bảo ko có sản phẩm trùng mã
    @Override
    public boolean existsById(String maSanPham) {
        return findById(maSanPham) != null;
    }
//Thêm sản phẩm
    @Override
    public void insert(SanPham sanPham) {
        if (sanPham == null
                || sanPham.getMaSanPham() == null
                || sanPham.getMaSanPham().trim().isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm hoặc mã sản phẩm không hợp lệ");
        }
        String ma = sanPham.getMaSanPham().trim();
        if (existsById(ma)) {
            throw new IllegalArgumentException("Mã sản phẩm đã tồn tại");
        }
        SanPham banSao = new SanPham(sanPham);
        banSao.setMaSanPham(ma);
        danhSach.add(banSao);
    }
//Cập nhật sản phẩm
    @Override
    public void update(SanPham sanPham) {
        if (sanPham == null
                || sanPham.getMaSanPham() == null
                || sanPham.getMaSanPham().trim().isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm hoặc mã sản phẩm không hợp lệ");
        }
        String ma = sanPham.getMaSanPham().trim();
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaSanPham().equals(ma)) {
                SanPham banSao = new SanPham(sanPham);
                banSao.setMaSanPham(ma);
                danhSach.set(i, banSao);
                return;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy sản phẩm để cập nhật");
    }
//Xóa sản phẩm
    @Override
    public void delete(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ");
        }
        String ma = maSanPham.trim();
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaSanPham().equals(ma)) {
                danhSach.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy sản phẩm để xóa");
    }
//Tìm theo danh mục
    @Override
    public List<SanPham> findByDanhMuc(String maDanhMuc) {
        List<SanPham> ketQua = new ArrayList<>();
        if (maDanhMuc == null || maDanhMuc.trim().isEmpty()) {
            return ketQua;
        }
        String maCanTim = maDanhMuc.trim();
        for (SanPham sp : danhSach) {
            if (sp.getMaDanhMuc().equals(maCanTim)) {
                ketQua.add(new SanPham(sp));
            }
        }
        return ketQua;
    }
//Tìm theo tên (gần đúng)
    @Override
    public List<SanPham> searchByName(String tuKhoa) {
        List<SanPham> ketQua = new ArrayList<>();
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return ketQua;
        }
        String tuKhoaNho = tuKhoa.trim().toLowerCase(Locale.ROOT);
        for (SanPham sp : danhSach) {
            if (sp.getTenSanPham() != null
                    && sp.getTenSanPham().toLowerCase(Locale.ROOT).contains(tuKhoaNho)) {
                ketQua.add(new SanPham(sp));
            }
        }
        return ketQua;
    }
}
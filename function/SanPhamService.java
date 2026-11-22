//Note: SanPhamService.java
//Bao gồm: Các phương thức để quản lý sản phẩm : Ktra dữ liệu, thêm, cập nhật, xóa sản phẩm, lấy tất cả sản phẩm, tìm sản phẩm theo mã, tìm sản phẩm theo danh mục, tìm sản phẩm theo tên
//Setting chuẩn cho hàm SanPhamDAO: findAll, findById, existsById, insert, update, delete, findByDanhMuc, searchByName

package function;
import data.SanPhamDAO;
import java.math.BigDecimal;
import java.util.List;
import model.SanPham;

public class SanPhamService {
    private final SanPhamDAO sanPhamDAO;
    private final DanhMucService danhMucService;

    public SanPhamService(SanPhamDAO sanPhamDAO, DanhMucService danhMucService) {
        this.sanPhamDAO = sanPhamDAO;
        this.danhMucService = danhMucService;
    }
//Ktra dữ liệu đầu vào sản phẩm
    private void kiemTraDuLieu(SanPham sp) {
        if (sp == null) {
            throw new IllegalArgumentException(
                "Sản phẩm không được null"
            );
        }
        if (sp.getMaSanPham() == null
                || sp.getMaSanPham().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Mã sản phẩm không được để trống"
            );
        }
        if (sp.getTenSanPham() == null
                || sp.getTenSanPham().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Tên sản phẩm không được để trống"
            );
        }
        if (sp.getMaDanhMuc() == null
                || sp.getMaDanhMuc().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Mã danh mục không được để trống"
            );
        }
        if (sp.getGiaNhap() == null
                || sp.getGiaNhap().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "Giá nhập không được null hoặc âm"
            );
        }
        if (sp.getGiaBan() == null
                || sp.getGiaBan().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "Giá bán không được null hoặc âm"
            );
        }
    }
//Thêm sản phẩm mới
    public void themSanPham(SanPham sp) {
        kiemTraDuLieu(sp);
        if (danhMucService.timTheoMa(sp.getMaDanhMuc()) == null) {
            throw new IllegalArgumentException(
                "Danh mục không tồn tại"
            );
        }
        if (sanPhamDAO.existsById(sp.getMaSanPham())) {
            throw new IllegalArgumentException(
                "Mã sản phẩm đã tồn tại"
            );
        }
        sanPhamDAO.insert(sp);
    }
//Cập nhật dữ liệu sản phẩm
    public void capNhatSanPham(SanPham sp) {
        kiemTraDuLieu(sp);
        if (!sanPhamDAO.existsById(sp.getMaSanPham())) {
            throw new IllegalArgumentException(
                "Sản phẩm không tồn tại"
            );
        }
        if (danhMucService.timTheoMa(sp.getMaDanhMuc()) == null) {
            throw new IllegalArgumentException(
                "Danh mục không tồn tại"
            );
        }
        sanPhamDAO.update(sp);
    }
//Xóa sản phẩm
    public void xoaSanPham(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Mã sản phẩm không hợp lệ"
            );
        }
        String ma = maSanPham.trim();
        if (!sanPhamDAO.existsById(ma)) {
            throw new IllegalArgumentException(
                "Sản phẩm không tồn tại"
            );
        }
        sanPhamDAO.delete(ma);
    }
//Lấy tất cả sản phẩm
    public List<SanPham> layTatCaSanPham() {
        return sanPhamDAO.findAll();
    }
//Tìm sản phẩm theo mã
    public SanPham timSanPhamTheoMa(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Mã sản phẩm không hợp lệ"
            );
        }
        return sanPhamDAO.findById(maSanPham.trim());
    }
//Tìm sản phẩm theo danh mục
    public List<SanPham> laySanPhamTheoDanhMuc(String maDanhMuc) {
        if (maDanhMuc == null || maDanhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Mã danh mục không hợp lệ"
            );
        }
        String ma = maDanhMuc.trim();
        if (danhMucService.timTheoMa(ma) == null) {
            throw new IllegalArgumentException(
                "Danh mục không tồn tại"
            );
        }
        return sanPhamDAO.findByDanhMuc(ma);
    }
//Tìm sản phẩm theo tên
    public List<SanPham> timSanPhamTheoTen(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Từ khóa tìm kiếm không hợp lệ"
            );
        }
        return sanPhamDAO.searchByName(tuKhoa.trim());
    }
}
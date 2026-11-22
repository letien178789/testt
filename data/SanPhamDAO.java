package data;

import java.util.List;
import model.SanPham;

public interface SanPhamDAO {
    List<SanPham> findAll();
    SanPham findById(String ma);
    boolean existsById(String ma);
    void insert(SanPham sp);
    void update(SanPham sp);
    void delete(String ma);
    List<SanPham> findByDanhMuc(String maDanhMuc);
    List<SanPham> searchByName(String tuKhoa);
}
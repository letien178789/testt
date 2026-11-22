package data;

import java.util.ArrayList;
import java.util.List;
import model.DanhMuc;

// Phu trach: TienLe
// Truy cập bộ nhớ lưu danh mục (List trong RAM).
public class DanhMucDAO {
    private final List<DanhMuc> danhSach = new ArrayList<>();

    public List<DanhMuc> findAll() {
        List<DanhMuc> ketQua = new ArrayList<>();
        for (DanhMuc dm : danhSach) {
            ketQua.add(new DanhMuc(dm)); // trả bản sao, không lộ danh sách gốc
        }
        return ketQua;
    }

    public DanhMuc findById(String ma) {
        if (ma == null) {
            return null;
        }
        for (DanhMuc dm : danhSach) {
            if (dm.getMaDanhMuc().equals(ma.trim())) {
                return new DanhMuc(dm);
            }
        }
        return null;
    }

    public boolean existsById(String ma) {
        return findById(ma) != null;
    }

    public void insert(DanhMuc danhMuc) {
        if (existsById(danhMuc.getMaDanhMuc())) {
            throw new IllegalArgumentException("Mã danh mục đã tồn tại");
        }
        DanhMuc banSao = new DanhMuc(danhMuc);
        banSao.setMaDanhMuc(danhMuc.getMaDanhMuc().trim());
        danhSach.add(banSao);
    }

    public void update(DanhMuc danhMuc) {
        String ma = danhMuc.getMaDanhMuc().trim();
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaDanhMuc().equals(ma)) {
                DanhMuc banSao = new DanhMuc(danhMuc);
                banSao.setMaDanhMuc(ma);
                danhSach.set(i, banSao);
                return;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy danh mục để cập nhật");
    }

    public void delete(String ma) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaDanhMuc().equals(ma.trim())) {
                danhSach.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy danh mục để xóa");
    }

    public void clear() {
        danhSach.clear();
    }

    // ----- Ghi/đọc file: maDanhMuc;tenDanhMuc;moTa -----
    public List<String> toDong() {
        List<String> dong = new ArrayList<>();
        for (DanhMuc dm : danhSach) {
            dong.add(FileStorage.lamSach(dm.getMaDanhMuc()) + ";"
                    + FileStorage.lamSach(dm.getTenDanhMuc()) + ";"
                    + FileStorage.lamSach(dm.getMoTa()));
        }
        return dong;
    }

    public void napTuDong(List<String> cacDong) {
        clear();
        for (String d : cacDong) {
            String[] p = d.split(";", -1);
            if (p.length >= 3) {
                danhSach.add(new DanhMuc(p[0], p[1], p[2]));
            }
        }
    }
}
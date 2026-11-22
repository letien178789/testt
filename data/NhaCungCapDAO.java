package data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.NhaCungCap;

// Phu trach: TienLe
// Truy cập bộ nhớ lưu nhà cung cấp (Map giữ thứ tự thêm vào).
public class NhaCungCapDAO {
    private final Map<String, NhaCungCap> danhSach = new LinkedHashMap<>();

    public List<NhaCungCap> findAll() {
        return new ArrayList<>(danhSach.values());
    }

    public NhaCungCap findById(String ma) {
        return danhSach.get(ma);
    }

    public boolean existsById(String ma) {
        return danhSach.containsKey(ma);
    }

    public void insert(NhaCungCap ncc) {
        if (danhSach.containsKey(ncc.getMaNhaCungCap())) {
            throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại");
        }
        danhSach.put(ncc.getMaNhaCungCap(), ncc);
    }

    public void update(NhaCungCap ncc) {
        if (!danhSach.containsKey(ncc.getMaNhaCungCap())) {
            throw new IllegalArgumentException("Không tìm thấy nhà cung cấp");
        }
        danhSach.put(ncc.getMaNhaCungCap(), ncc);
    }

    public void delete(String ma) {
        if (danhSach.remove(ma) == null) {
            throw new IllegalArgumentException("Không tìm thấy nhà cung cấp");
        }
    }

    public void clear() {
        danhSach.clear();
    }

    // ----- File: ma;ten;diaChi;soDienThoai;email -----
    public List<String> toDong() {
        List<String> dong = new ArrayList<>();
        for (NhaCungCap n : danhSach.values()) {
            dong.add(FileStorage.lamSach(n.getMaNhaCungCap()) + ";"
                    + FileStorage.lamSach(n.getTenNhaCungCap()) + ";"
                    + FileStorage.lamSach(n.getDiaChi()) + ";"
                    + FileStorage.lamSach(n.getSoDienThoai()) + ";"
                    + FileStorage.lamSach(n.getEmail()));
        }
        return dong;
    }

    public void napTuDong(List<String> cacDong) {
        clear();
        for (String d : cacDong) {
            String[] p = d.split(";", -1);
            if (p.length >= 5) {
                danhSach.put(p[0], new NhaCungCap(p[0], p[1], p[2], p[3], p[4]));
            }
        }
    }
}
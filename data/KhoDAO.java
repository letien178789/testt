package data;

import java.util.ArrayList;
import java.util.List;
import model.Kho;

// Phu trach: TienLe  (BẢN GỢI Ý: file KhoDAO.java trong bản Word đang là bản sao của FileStorage)
// Truy cập bộ nhớ lưu danh sách kho (List trong RAM). Main/DataStore cần: findAll, insert, existsById, toDong, napTuDong.
public class KhoDAO {
    private final List<Kho> danhSach = new ArrayList<>();

    public List<Kho> findAll() {
        return new ArrayList<>(danhSach);
    }

    public Kho findById(String ma) {
        if (ma == null) {
            return null;
        }
        for (Kho k : danhSach) {
            if (k.getMaKho().equals(ma.trim())) {
                return k;
            }
        }
        return null;
    }

    public boolean existsById(String ma) {
        return findById(ma) != null;
    }

    public void insert(Kho kho) {
        if (existsById(kho.getMaKho())) {
            throw new IllegalArgumentException("Mã kho đã tồn tại");
        }
        danhSach.add(kho);
    }

    public void clear() {
        danhSach.clear();
    }

    // ----- Ghi/đọc file: maKho;tenKho;diaChi -----
    public List<String> toDong() {
        List<String> dong = new ArrayList<>();
        for (Kho k : danhSach) {
            dong.add(k.toDongFile());
        }
        return dong;
    }

    public void napTuDong(List<String> cacDong) {
        clear();
        for (String d : cacDong) {
            Kho k = Kho.tuDongFile(d);
            if (k != null) {
                danhSach.add(k);
            }
        }
    }
}
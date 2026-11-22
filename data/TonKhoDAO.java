package data;

import function.TonKhoService;
import java.util.ArrayList;
import java.util.List;
import model.TonKho;

// Phu trach: TienLe
// Truy cập tồn kho. Dữ liệu thật nằm trong mảng của TonKhoService (MinhNgoc),
// DAO này chỉ là lớp bọc để data/Main dùng chung 1 nguồn dữ liệu, tránh lệch số liệu.
public class TonKhoDAO {
    private final TonKhoService service;

    public TonKhoDAO(TonKhoService service) {
        this.service = service;
    }

    public List<TonKho> findAll() {
        List<TonKho> ketQua = new ArrayList<>();
        for (int i = 0; i < service.getSoLuongDong(); i++) {
            ketQua.add(service.getTonKho(i));
        }
        return ketQua;
    }

    public List<TonKho> findByKho(String maKho) {
        List<TonKho> ketQua = new ArrayList<>();
        for (TonKho t : findAll()) {
            if (t.getMaKho().equals(maKho)) {
                ketQua.add(t);
            }
        }
        return ketQua;
    }

    public TonKho findById(String maKho, String maSanPham) {
        return service.timChinhXac(maKho, maSanPham);
    }

    public boolean existsById(String maKho, String maSanPham) {
        return findById(maKho, maSanPham) != null;
    }

    public boolean insert(TonKho t) {
        return service.them(t);
    }

    public boolean update(String maKho, String maSanPham, int soLuong, int mucToiThieu) {
        return service.sua(maKho, maSanPham, soLuong, mucToiThieu);
    }

    public boolean delete(String maKho, String maSanPham) {
        return service.xoa(maKho, maSanPham);
    }

    // File: maKho;maSP;soLuong;mucToiThieu (dùng sẵn ghiFile/docFile của TonKhoService)
    public boolean ghiFile(String duongDan) {
        return service.ghiFile(duongDan);
    }

    public boolean docFile(String duongDan) {
        return service.docFile(duongDan);
    }
}
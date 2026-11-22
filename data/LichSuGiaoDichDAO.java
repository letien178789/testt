package data;

import java.util.ArrayList;
import java.util.List;
import model.LichSuGiaoDich;

// Phu trach: TienLe
// Truy cập bộ nhớ lưu lịch sử giao dịch. Lịch sử chỉ thêm, không sửa/xóa.
public class LichSuGiaoDichDAO {
    private final List<LichSuGiaoDich> danhSach = new ArrayList<>();

    public List<LichSuGiaoDich> findAll() {
        return new ArrayList<>(danhSach);
    }

    public void insert(LichSuGiaoDich ls) {
        danhSach.add(ls);
    }

    public List<LichSuGiaoDich> findByMaSanPham(String maSanPham) {
        List<LichSuGiaoDich> ketQua = new ArrayList<>();
        for (LichSuGiaoDich ls : danhSach) {
            if (ls.getMaSanPham().equals(maSanPham)) {
                ketQua.add(ls);
            }
        }
        return ketQua;
    }

    public List<LichSuGiaoDich> findByMaKho(String maKho) {
        List<LichSuGiaoDich> ketQua = new ArrayList<>();
        for (LichSuGiaoDich ls : danhSach) {
            if (ls.getMaKho().equals(maKho)) {
                ketQua.add(ls);
            }
        }
        return ketQua;
    }

    public void clear() {
        danhSach.clear();
    }

    public List<String> toDong() {
        List<String> dong = new ArrayList<>();
        for (LichSuGiaoDich ls : danhSach) {
            dong.add(ls.toDongFile());
        }
        return dong;
    }

    // Đọc xong phải đặt lại bộ đếm mã LSxxx = mã lớn nhất đã có, tránh trùng mã
    public void napTuDong(List<String> cacDong) {
        clear();
        int max = 0;
        for (String d : cacDong) {
            try {
                LichSuGiaoDich ls = LichSuGiaoDich.tuDongFile(d);
                if (ls != null) {
                    danhSach.add(ls);
                    max = Math.max(max, LichSuGiaoDich.laySoTuMa(ls.getMaGiaoDich()));
                }
            } catch (RuntimeException e) {
                System.out.println("Bỏ qua dòng lịch sử lỗi: " + d);
            }
        }
        LichSuGiaoDich.datLaiBoDem(max);
    }
}
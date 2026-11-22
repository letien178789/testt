package function;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.ChiTietPhieu;
import model.PhieuNhap;

// Phu trach: PhuongThao
// Nghiệp vụ nhập kho.
public class NhapKhoService {
    private final Map<String, PhieuNhap> danhSachPhieuNhap;

    public NhapKhoService() {
        danhSachPhieuNhap = new LinkedHashMap<>();
    }

    public void themPhieuNhap(PhieuNhap phieuNhap) {
        kiemTraPhieuNhap(phieuNhap);
        if (danhSachPhieuNhap.containsKey(phieuNhap.getMaPhieu())) {
            throw new IllegalArgumentException("Mã phiếu nhập đã tồn tại");
        }
        danhSachPhieuNhap.put(phieuNhap.getMaPhieu(), phieuNhap);
    }

    public void capNhatPhieuNhap(PhieuNhap phieuNhap) {
        kiemTraPhieuNhap(phieuNhap);
        if (!danhSachPhieuNhap.containsKey(phieuNhap.getMaPhieu())) {
            throw new IllegalArgumentException("Không tìm thấy phiếu nhập");
        }
        danhSachPhieuNhap.put(phieuNhap.getMaPhieu(), phieuNhap);
    }

    public void xoaPhieuNhap(String maPhieu) {
        if (danhSachPhieuNhap.remove(maPhieu) == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu nhập");
        }
    }

    public PhieuNhap timTheoMa(String maPhieu) {
        return danhSachPhieuNhap.get(maPhieu);
    }

    public List<PhieuNhap> layTatCa() {
        return new ArrayList<>(danhSachPhieuNhap.values());
    }

    public double tinhTongTien(String maPhieu) {
        PhieuNhap phieuNhap = timTheoMa(maPhieu);
        if (phieuNhap == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu nhập");
        }
        return phieuNhap.tongTien();
    }

    private void kiemTraPhieuNhap(PhieuNhap phieuNhap) {
        if (phieuNhap == null || phieuNhap.getMaPhieu() == null
                || phieuNhap.getMaPhieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Phiếu nhập không hợp lệ");
        }
        if (phieuNhap.getNhaCungCap() == null) {
            throw new IllegalArgumentException("Phiếu nhập phải có nhà cung cấp");
        }
        if (phieuNhap.getChiTiet().isEmpty()) {
            throw new IllegalArgumentException("Phiếu nhập phải có ít nhất một sản phẩm");
        }
        for (ChiTietPhieu chiTiet : phieuNhap.getChiTiet()) {
            if (chiTiet == null || chiTiet.getMaSanPham() == null
                    || chiTiet.getMaSanPham().trim().isEmpty()
                    || chiTiet.getSoLuong() <= 0 || chiTiet.getDonGia() < 0) {
                throw new IllegalArgumentException("Chi tiết phiếu nhập không hợp lệ");
            }
        }
    }
}

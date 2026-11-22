package model;

import function.TonKhoService;
import java.time.LocalDate;
import java.util.List;

// Phu trach: PhuongThao
// Phiếu nhập kế thừa PhieuKho: dùng chung mã phiếu, mã kho, ngày, trạng thái, danh sách chi tiết của lớp cha.
// Thêm riêng: nhà cung cấp và tổng tiền. Cài đặt (override) 2 hàm trừu tượng của PhieuKho.
public class PhieuNhap extends PhieuKho {
    private NhaCungCap nhaCungCap;

    public PhieuNhap() {
        super();
        setLyDo("Nhập hàng");
    }

    public PhieuNhap(String maPhieu, LocalDate ngayLap, String maKho,
                     NhaCungCap nhaCungCap, List<ChiTietPhieu> chiTiet) {
        super(maPhieu, maKho, "Nhập hàng");
        if (ngayLap != null) {
            setNgay(ngayLap);
        }
        this.nhaCungCap = nhaCungCap;
        setChiTiet(chiTiet);
    }

    // ----- Getter / Setter -----
    public LocalDate getNgayLap() {
        return getNgay();
    }

    public void setNgayLap(LocalDate ngayLap) {
        setNgay(ngayLap);
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    // Danh sách chi tiết chính là danh sách của lớp cha
    public List<ChiTietPhieu> getChiTiet() {
        return getDanhSachChiTiet();
    }

    public void setChiTiet(List<ChiTietPhieu> chiTiet) {
        getDanhSachChiTiet().clear();
        if (chiTiet != null) {
            getDanhSachChiTiet().addAll(chiTiet);
        }
    }

    // Thêm 1 dòng chi tiết có đơn giá (themChiTiet(maSP, soLuong) của lớp cha không có đơn giá)
    public void themChiTiet(String maSanPham, int soLuong, double donGia) {
        if (soLuong <= 0) {
            System.out.println("Số lượng phải lớn hơn 0 (SP " + maSanPham + ").");
            return;
        }
        for (ChiTietPhieu ct : getDanhSachChiTiet()) {
            if (ct.getMaSanPham().equals(maSanPham)) {
                ct.setSoLuong(ct.getSoLuong() + soLuong);
                ct.setDonGia(donGia);
                return;
            }
        }
        getDanhSachChiTiet().add(new ChiTietPhieu(maSanPham, soLuong, donGia));
    }

    public double tongTien() {
        double tongTien = 0;
        for (ChiTietPhieu ct : getDanhSachChiTiet()) {
            tongTien += ct.thanhTien();
        }
        return tongTien;
    }

    // ----- Override hàm trừu tượng của PhieuKho (đa hình) -----
    @Override
    public String loaiPhieu() {
        return "PHIẾU NHẬP KHO";
    }

    // Cộng tồn cho từng dòng chi tiết (TonKhoService tự ghi lịch sử NHAP)
    @Override
    public boolean capNhatTon(TonKhoService tonKhoService) {
        if (!CHO_XU_LY.equals(getTrangThai()) || getDanhSachChiTiet().isEmpty()) {
            return false;
        }
        for (ChiTietPhieu ct : getDanhSachChiTiet()) {
            tonKhoService.tangTon(getMaKho(), ct.getMaSanPham(), ct.getSoLuong(),
                    getMaPhieu(), "Nhập kho từ nhà cung cấp");
        }
        setTrangThai(DA_XU_LY);
        return true;
    }

    @Override
    public void inPhieu() {
        super.inPhieu();
        String ncc = (nhaCungCap == null) ? "(chưa có)" : nhaCungCap.getTenNhaCungCap();
        System.out.println("    Nhà cung cấp: " + ncc + " | Tổng tiền: " + tongTien());
    }
}
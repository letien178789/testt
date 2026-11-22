package model;

// Phu trach: PhuongThao
// Chi tiết sản phẩm thuộc phiếu: mã SP, số lượng và đơn giá (đơn giá dùng cho phiếu nhập; phiếu xuất để 0).
public class ChiTietPhieu {
    private String maSanPham;
    private int soLuong;
    private double donGia;

    public ChiTietPhieu() {
    }

    public ChiTietPhieu(String maSanPham, int soLuong) {
        this(maSanPham, soLuong, 0);
    }

    public ChiTietPhieu(String maSanPham, int soLuong, double donGia) {
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    // Thành tiền = số lượng x đơn giá
    public double thanhTien() {
        return soLuong * donGia;
    }
}
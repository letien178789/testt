package model;

import function.TonKhoService;
import java.time.LocalDate;
import java.util.ArrayList;

// Phu trach: MinhNgoc
// Lớp abstract dùng chung cho phiếu nhập/xuất.

public abstract class PhieuKho {
    public static final String CHO_XU_LY = "CHO_XU_LY";
    public static final String DA_XU_LY = "DA_XU_LY";
    public static final String TU_CHOI = "TU_CHOI";

    private String maPhieu;
    private String maKho;
    private LocalDate ngay;
    private String lyDo;
    private String trangThai;
    private ArrayList<ChiTietPhieu> danhSachChiTiet;

    public PhieuKho() {
        this.ngay = LocalDate.now();
        this.trangThai = CHO_XU_LY;
        this.danhSachChiTiet = new ArrayList<>();
    }

    public PhieuKho(String maPhieu, String maKho, String lyDo) {
        this();
        this.maPhieu = maPhieu;
        this.maKho = maKho;
        this.lyDo = lyDo;
    }

    // ----- Hàm trừu tượng -----
    public abstract String loaiPhieu();

    public abstract boolean capNhatTon(TonKhoService tonKhoService);

    // ----- Hàm chung -----
    public void themChiTiet(String maSanPham, int soLuong) {
        if (soLuong <= 0) {
            System.out.println("Số lượng phải lớn hơn 0 (SP " + maSanPham + ").");
            return;
        }
        for (ChiTietPhieu ct : danhSachChiTiet) {
            if (ct.getMaSanPham().equals(maSanPham)) {
                ct.setSoLuong(ct.getSoLuong() + soLuong);
                return;
            }
        }
        danhSachChiTiet.add(new ChiTietPhieu(maSanPham, soLuong));
    }

    public int tinhTongSoLuong() {
        int tong = 0;
        for (ChiTietPhieu ct : danhSachChiTiet) {
            tong += ct.getSoLuong();
        }
        return tong;
    }

    public void inPhieu() {
        System.out.println(loaiPhieu() + " " + maPhieu + " | Kho " + maKho + " | " + ngay
                + " | Lý do: " + lyDo + " | Trạng thái: " + trangThai);
        for (ChiTietPhieu ct : danhSachChiTiet) {
            System.out.println("    - SP " + ct.getMaSanPham() + " x " + ct.getSoLuong());
        }
        System.out.println("    Tổng số lượng: " + tinhTongSoLuong());
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public String getMaKho() {
        return maKho;
    }

    public void setMaKho(String maKho) {
        this.maKho = maKho;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public ArrayList<ChiTietPhieu> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }
}

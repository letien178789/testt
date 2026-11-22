package model;

import function.TonKhoService;
import java.time.LocalDate;

// Phu trach: MinhNgoc
// Phiếu xuất kho: kế thừa PhieuKho, cài đặt (override) 2 hàm trừu tượng loaiPhieu() và capNhatTon().
public class PhieuXuat extends PhieuKho {
    // ----- Thành phần static -----
    private static int tongdatao = 0; // đếm số phiếu xuất đã tạo

    public static int getTongdatao() {
        return tongdatao;
    }

    // Tạo từ 1 dòng file: maPhieu;maKho;ngay;lyDo;trangThai;SP001:20,SP002:25[;nguoiNhan]
    // (cột nguoiNhan là tùy chọn, nên file cũ chưa có cột này vẫn đọc được)
    public static PhieuXuat tuDongFile(String dong) {
        String[] p = dong.split(";", 7);
        if (p.length < 6) {
            return null;
        }
        PhieuXuat px = new PhieuXuat(p[0], p[1], p[3]);
        px.setNgay(LocalDate.parse(p[2]));
        px.setTrangThai(p[4]);
        if (!p[5].trim().isEmpty()) {
            String[] cacDong = p[5].split(",");
            for (String s : cacDong) {
                String[] kv = s.split(":");
                px.themChiTiet(kv[0], Integer.parseInt(kv[1]));
            }
        }
        if (p.length == 7) {
            px.setNguoiNhan(p[6]);
        }
        return px;
    }

    // ----- Thuộc tính riêng của phiếu xuất -----
    private String nguoiNhan;   // người / đơn vị nhận hàng

    // ----- Hàm thiết lập (constructor) -----
    public PhieuXuat() {
        super();
        this.nguoiNhan = "";
        tongdatao++;
    }

    public PhieuXuat(String maPhieu, String maKho, String lyDo) {
        super(maPhieu, maKho, lyDo);
        this.nguoiNhan = "";
        tongdatao++;
    }

    public PhieuXuat(String maPhieu, String maKho, String lyDo, String nguoiNhan) {
        this(maPhieu, maKho, lyDo);
        setNguoiNhan(nguoiNhan);
    }

    // ----- Getter / Setter -----
    public String getNguoiNhan() {
        return nguoiNhan;
    }

    public void setNguoiNhan(String nguoiNhan) {
        this.nguoiNhan = (nguoiNhan == null) ? "" : nguoiNhan.trim();
    }

    // ----- Ghi file -----
    public String toDongFile() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getDanhSachChiTiet().size(); i++) {
            ChiTietPhieu ct = getDanhSachChiTiet().get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append(ct.getMaSanPham()).append(":").append(ct.getSoLuong());
        }
        String lyDoAnToan = (getLyDo() == null) ? "" : getLyDo().replace(";", ",");
        return getMaPhieu() + ";" + getMaKho() + ";" + getNgay() + ";" + lyDoAnToan
                + ";" + getTrangThai() + ";" + sb + ";" + nguoiNhan.replace(";", ",");
    }

    // Kiểm tra tồn kho có đủ cho cả phiếu không (không in gì)
    public boolean duHang(TonKhoService tonKhoService) {
        for (ChiTietPhieu ct : getDanhSachChiTiet()) {
            if (tonKhoService.laySoLuongTon(getMaKho(), ct.getMaSanPham()) < ct.getSoLuong()) {
                return false;
            }
        }
        return true;
    }

    // ----- Override hàm trừu tượng của PhieuKho (đa hình) -----
    @Override
    public String loaiPhieu() {
        return "PHIẾU XUẤT KHO";
    }

    // Trừ tồn cho từng dòng chi tiết. TonKhoService.giamTon sẽ tự ghi lịch sử và in cảnh báo.
    @Override
    public boolean capNhatTon(TonKhoService tonKhoService) {
        if (!CHO_XU_LY.equals(getTrangThai())) {
            return false;
        }
        if (!duHang(tonKhoService)) {
            return false;
        }
        String ghiChu = "Xuất kho: " + getLyDo();
        if (!nguoiNhan.isEmpty()) {
            ghiChu += " (người nhận: " + nguoiNhan + ")";
        }
        for (ChiTietPhieu ct : getDanhSachChiTiet()) {
            tonKhoService.giamTon(getMaKho(), ct.getMaSanPham(),
                    ct.getSoLuong(), getMaPhieu(), ghiChu);
        }
        setTrangThai(DA_XU_LY);
        return true;
    }

    // Override inPhieu: in phần chung của PhieuKho rồi in thêm người nhận
    @Override
    public void inPhieu() {
        super.inPhieu();
        if (!nguoiNhan.isEmpty()) {
            System.out.println("    Người nhận: " + nguoiNhan);
        }
    }
}
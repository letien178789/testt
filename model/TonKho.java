package model;

// Phu trach: MinhNgoc
// Số lượng tồn của 1 sản phẩm tại 1 kho. Khóa của dòng tồn là cặp (maKho, maSanPham).
public class TonKho {
    // ----- Thành phần static -----
    public static final int MUC_TOI_THIEU_MAC_DINH = 10;
    private static int tongdatao = 0;   // đếm số đối tượng TonKho đã tạo

    // Hàm static: kiểm tra số lượng hợp lệ (không được âm)
    public static boolean hopLeSoLuong(int soLuong) {
        return soLuong >= 0;
    }

    public static int getTongdatao() {
        return tongdatao;
    }

    // Hàm static: tạo đối tượng từ 1 dòng file "maKho;maSP;soLuong;mucToiThieu"
    // Dòng sai định dạng -> trả về null (không làm chương trình bị lỗi)
    public static TonKho tuDongFile(String dong) {
        String[] p = dong.split(";");
        if (p.length < 4) {
            return null;
        }
        try {
            return new TonKho(p[0], p[1], Integer.parseInt(p[2].trim()), Integer.parseInt(p[3].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ----- Thuộc tính -----
    private String maKho;
    private String maSanPham;
    private int soLuong;
    private int mucToiThieu;   // dưới mức này thì cảnh báo "sắp hết"

    // ----- Hàm thiết lập (constructor) -----
    public TonKho() {
        this.mucToiThieu = MUC_TOI_THIEU_MAC_DINH;
        tongdatao++;
    }

    public TonKho(String maKho, String maSanPham, int soLuong, int mucToiThieu) {
        this.maKho = maKho;
        this.maSanPham = maSanPham;
        setSoLuong(soLuong);
        setMucToiThieu(mucToiThieu);
        tongdatao++;
    }

    // ----- Getter / Setter -----
    public String getMaKho() {
        return maKho;
    }

    public void setMaKho(String maKho) {
        this.maKho = maKho;
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
        if (hopLeSoLuong(soLuong)) {
            this.soLuong = soLuong;
        } else {
            System.out.println("Số lượng tồn không được âm, đặt lại bằng 0.");
            this.soLuong = 0;
        }
    }

    public int getMucToiThieu() {
        return mucToiThieu;
    }

    public void setMucToiThieu(int mucToiThieu) {
        if (mucToiThieu < 0) {
            this.mucToiThieu = 0;
        } else {
            this.mucToiThieu = mucToiThieu;
        }
    }

    // ----- Nghiệp vụ nhỏ -----
    public boolean duHang(int soLuongCan) {
        return soLuong >= soLuongCan;
    }

    public boolean hetHang() {
        return soLuong == 0;
    }

    public boolean sapHet() {
        return soLuong > 0 && soLuong <= mucToiThieu;
    }

    // Cộng tồn. Trả về false nếu số lượng thêm <= 0
    public boolean tang(int soLuongThem) {
        if (soLuongThem <= 0) {
            return false;
        }
        this.soLuong += soLuongThem;
        return true;
    }

    // Trừ tồn. Trả về false nếu số lượng bớt <= 0 hoặc lớn hơn số đang có (không bao giờ để tồn âm)
    public boolean giam(int soLuongBot) {
        if (soLuongBot <= 0 || soLuongBot > soLuong) {
            return false;
        }
        this.soLuong -= soLuongBot;
        return true;
    }

    // ----- Cảnh báo tồn kho -----
    public boolean canCanhBao() {
        return hetHang() || sapHet();
    }

    public String tinhTrang() {
        if (hetHang()) {
            return "HẾT HÀNG";
        }
        if (sapHet()) {
            return "SẮP HẾT";
        }
        return "BÌNH THƯỜNG";
    }

    public String thongBaoCanhBao() {
        if (hetHang()) {
            return "[CẢNH BÁO] SP " + maSanPham + " tại kho " + maKho + " ĐÃ HẾT HÀNG!";
        }
        if (sapHet()) {
            return "[CẢNH BÁO] SP " + maSanPham + " tại kho " + maKho
                    + " SẮP HẾT: còn " + soLuong + " (mức tối thiểu " + mucToiThieu + ")";
        }
        return "";
    }

    // Chuyển thành 1 dòng để ghi file
    public String toDongFile() {
        return maKho + ";" + maSanPham + ";" + soLuong + ";" + mucToiThieu;
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-8s | Tồn: %-5d | Tối thiểu: %-4d | %s",
                maKho, maSanPham, soLuong, mucToiThieu, tinhTrang());
    }
}
package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Phu trach: MinhNgoc
// Lưu 1 lần biến động tồn kho (nhập / xuất / điều chỉnh): tồn trước, tồn sau, thuộc phiếu nào.
public class LichSuGiaoDich {
    // ----- Thành phần static -----
    public static final String LOAI_NHAP = "NHAP";
    public static final String LOAI_XUAT = "XUAT";
    public static final String LOAI_DIEU_CHINH = "DIEU_CHINH";

    private static int dem = 0; // bộ đếm dùng chung để sinh mã LS001, LS002...

    public static String taoMaGiaoDich() {
        dem++;
        return String.format("LS%03d", dem);
    }

    // Đặt lại bộ đếm (dùng khi đọc file: bộ đếm phải bằng mã lớn nhất đã có)
    public static void datLaiBoDem(int giaTri) {
        dem = giaTri;
    }

    public static int getDem() {
        return dem;
    }

    // Lấy phần số từ mã "LS012" -> 12
    public static int laySoTuMa(String ma) {
        try {
            return Integer.parseInt(ma.substring(2));
        } catch (Exception e) {
            return 0;
        }
    }

    // Tạo 1 bản ghi lịch sử mới, tự sinh mã giao dịch (TonKhoService gọi hàm này)
    public static LichSuGiaoDich tao(String maKho, String maSanPham, String loaiGiaoDich,
                                     int soLuong, int tonTruoc, int tonSau,
                                     String maPhieu, String ghiChu) {
        return new LichSuGiaoDich(taoMaGiaoDich(), maKho, maSanPham, loaiGiaoDich,
                soLuong, tonTruoc, tonSau, maPhieu, ghiChu);
    }

    // Tạo đối tượng từ 1 dòng file (dòng sai định dạng -> null)
    public static LichSuGiaoDich tuDongFile(String dong) {
        String[] p = dong.split(";", 10);
        if (p.length < 10) {
            return null;
        }
        LichSuGiaoDich ls = new LichSuGiaoDich(p[0], p[1], p[2], p[3],
                Integer.parseInt(p[4]), Integer.parseInt(p[5]), Integer.parseInt(p[6]),
                p[7], p[9]);
        ls.setThoiGian(LocalDateTime.parse(p[8]));
        return ls;
    }

    // ----- Thuộc tính -----
    private String maGiaoDich;
    private String maKho;
    private String maSanPham;
    private String loaiGiaoDich;
    private int soLuong;
    private int tonTruoc;
    private int tonSau;
    private String maPhieu;
    private LocalDateTime thoiGian;
    private String ghiChu;

    // ----- Hàm thiết lập (constructor) -----
    public LichSuGiaoDich() {
        this.thoiGian = LocalDateTime.now();
        this.ghiChu = "";
    }

    public LichSuGiaoDich(String maGiaoDich, String maKho, String maSanPham,
                          String loaiGiaoDich, int soLuong, int tonTruoc,
                          int tonSau, String maPhieu, String ghiChu) {
        this.maGiaoDich = maGiaoDich;
        this.maKho = maKho;
        this.maSanPham = maSanPham;
        this.loaiGiaoDich = loaiGiaoDich;
        this.soLuong = soLuong;
        this.tonTruoc = tonTruoc;
        this.tonSau = tonSau;
        this.maPhieu = maPhieu;
        this.ghiChu = ghiChu;
        this.thoiGian = LocalDateTime.now();
    }

    // ----- Getter / Setter -----
    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

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

    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getTonTruoc() {
        return tonTruoc;
    }

    public void setTonTruoc(int tonTruoc) {
        this.tonTruoc = tonTruoc;
    }

    public int getTonSau() {
        return tonSau;
    }

    public void setTonSau(int tonSau) {
        this.tonSau = tonSau;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // ----- Nghiệp vụ nhỏ -----
    public boolean laNhap() {
        return LOAI_NHAP.equals(loaiGiaoDich);
    }

    public boolean laXuat() {
        return LOAI_XUAT.equals(loaiGiaoDich);
    }

    // Dòng ghi file: ký tự ';' và xuống dòng trong ghi chú được đổi để không vỡ định dạng
    public String toDongFile() {
        String gc = (ghiChu == null) ? "" : ghiChu.replace(";", ",").replace("\r", " ").replace("\n", " ");
        return maGiaoDich + ";" + maKho + ";" + maSanPham + ";" + loaiGiaoDich + ";"
                + soLuong + ";" + tonTruoc + ";" + tonSau + ";" + maPhieu + ";"
                + thoiGian.toString() + ";" + gc;
    }

    @Override
    public String toString() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return maGiaoDich + " | " + thoiGian.format(f) + " | " + loaiGiaoDich
                + " | Kho " + maKho + " | SP " + maSanPham + " | SL " + soLuong
                + " | Tồn: " + tonTruoc + " -> " + tonSau + " | Phiếu " + maPhieu;
    }
}
package model;

// Phu trach: MinhNgoc
// Thông tin kho hàng: mỗi kho có mã, tên và địa chỉ.
public class Kho {
    // ----- Thành phần static -----
    private static int tongdatao = 0;   // đếm số đối tượng Kho đã tạo

    public static int getTongdatao() {
        return tongdatao;
    }

    // Mã kho hợp lệ: không rỗng và không chứa dấu ';' (vì ';' dùng để ngăn cách cột trong file)
    public static boolean hopLeMa(String ma) {
        return ma != null && !ma.trim().isEmpty() && !ma.contains(";");
    }

    // Tạo Kho từ 1 dòng file "maKho;tenKho;diaChi" (dòng sai định dạng -> trả về null)
    public static Kho tuDongFile(String dong) {
        String[] p = dong.split(";", 3);
        if (p.length < 3 || !hopLeMa(p[0])) {
            return null;
        }
        return new Kho(p[0], p[1], p[2]);
    }

    // ----- Thuộc tính -----
    private String maKho;
    private String tenKho;
    private String diaChi;

    // ----- Hàm thiết lập (constructor) -----
    public Kho() {
        this.maKho = "";
        this.tenKho = "";
        this.diaChi = "";
        tongdatao++;
    }

    public Kho(String maKho, String tenKho, String diaChi) {
        setMaKho(maKho);
        setTenKho(tenKho);
        setDiaChi(diaChi);
        tongdatao++;
    }

    // Constructor sao chép: tạo kho mới có dữ liệu giống kho k
    public Kho(Kho k) {
        this(k.maKho, k.tenKho, k.diaChi);
    }

    // ----- Getter / Setter -----
    public String getMaKho() {
        return maKho;
    }

    public void setMaKho(String maKho) {
        this.maKho = (maKho == null) ? "" : maKho.trim();
    }

    public String getTenKho() {
        return tenKho;
    }

    public void setTenKho(String tenKho) {
        this.tenKho = (tenKho == null) ? "" : tenKho.trim();
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = (diaChi == null) ? "" : diaChi.trim();
    }

    // Dòng ghi file: ký tự ';' trong dữ liệu được đổi thành ',' để không vỡ định dạng
    public String toDongFile() {
        return maKho.replace(";", ",") + ";" + tenKho.replace(";", ",") + ";" + diaChi.replace(";", ",");
    }

    @Override
    public String toString() {
        return "Kho [" + maKho + "] " + tenKho + " - " + diaChi;
    }
}
//Note: DanhMuc.java
//Bao gồm: Lớp DanhMuc với các thuộc tính: maDanhMuc, tenDanhMuc, moTa và các phương thức getter/setter tương ứng

package model;


public class DanhMuc {
    private String maDanhMuc;
    private String tenDanhMuc;
    private String moTa;

    public DanhMuc() {
        maDanhMuc = "";
        tenDanhMuc = "";
        moTa = "";
    }

    // Giữ lại để tương thích với chỗ đang tạo danh mục chỉ có mã và tên
    public DanhMuc(String maDanhMuc, String tenDanhMuc) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.moTa = "";
    }

    public DanhMuc(String maDanhMuc, String tenDanhMuc, String moTa) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.moTa = moTa;
    }

    public DanhMuc(DanhMuc danhMuc) {
        this.maDanhMuc = danhMuc.maDanhMuc;
        this.tenDanhMuc = danhMuc.tenDanhMuc;
        this.moTa = danhMuc.moTa;
    }

    public String getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(String maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
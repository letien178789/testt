//Note: SanPham.java
//Bao gồm: Lớp SanPham với các thuộc tính: maSanPham, maDanhMuc, tenSanPham, donViTinh, giaNhap, giaBan và các phương thức getter/setter tương ứng


package model;

import java.math.BigDecimal;

public class SanPham {
    private String maSanPham;
    private String maDanhMuc;
    private String tenSanPham;
    private String donViTinh;
    private BigDecimal giaNhap;
    private BigDecimal giaBan;

    public SanPham() {
        maSanPham = "";
        maDanhMuc = "";
        tenSanPham = "";
        donViTinh = "";
        giaNhap = BigDecimal.ZERO;
        giaBan = BigDecimal.ZERO;
    }

    public SanPham(String maSanPham, String maDanhMuc,
                   String tenSanPham, String donViTinh,
                   BigDecimal giaNhap, BigDecimal giaBan) {
        this.maSanPham = maSanPham;
        this.maDanhMuc = maDanhMuc;
        this.tenSanPham = tenSanPham;
        this.donViTinh = donViTinh;
        this.giaNhap = giaNhap;
        this.giaBan = giaBan;
    }

    public SanPham(SanPham sp) {
        this.maSanPham = sp.maSanPham;
        this.maDanhMuc = sp.maDanhMuc;
        this.tenSanPham = sp.tenSanPham;
        this.donViTinh = sp.donViTinh;
        this.giaNhap = sp.giaNhap;
        this.giaBan = sp.giaBan;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(String maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public BigDecimal getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(BigDecimal giaNhap) {
        this.giaNhap = giaNhap;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }
}
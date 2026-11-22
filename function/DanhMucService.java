//Note: DanhmucService.java
//Bao gồm: Các phương thức để quản lý danh mục : Thêm danh mục, tìm danh mục theo mã, cập nhật danh mục, xóa danh mục, lấy tất cả danh mục

package function;

import model.DanhMuc;

public class DanhMucService {
    private static final int SUC_CHUA_TOI_DA = 100;

    private final DanhMuc[] dsDanhMuc = new DanhMuc[SUC_CHUA_TOI_DA];
    private int soLuong = 0;

    // Thêm danh mục
    public boolean themDanhMuc(DanhMuc dm) {
        if (dm == null
                || dm.getMaDanhMuc() == null
                || dm.getMaDanhMuc().trim().isEmpty()
                || dm.getTenDanhMuc() == null
                || dm.getTenDanhMuc().trim().isEmpty()) {
            return false;
        }

        if (soLuong >= SUC_CHUA_TOI_DA) {
            System.out.println("Danh sách danh mục đã đầy!");
            return false;
        }

        String ma = dm.getMaDanhMuc().trim();

        if (timTheoMa(ma) != null) {
            System.out.println("Mã danh mục đã tồn tại!");
            return false;
        }

        DanhMuc banSao = new DanhMuc(dm);
        banSao.setMaDanhMuc(ma);
        dsDanhMuc[soLuong] = banSao;
        soLuong++;

        return true;
    }

    // Tìm danh mục theo mã
    public DanhMuc timTheoMa(String maDanhMuc) {
        if (maDanhMuc == null || maDanhMuc.trim().isEmpty()) {
            return null;
        }
        String ma = maDanhMuc.trim();

        for (int i = 0; i < soLuong; i++) {
            if (dsDanhMuc[i].getMaDanhMuc().equals(ma)) {
                return dsDanhMuc[i];
            }
        }

        return null;
    }

    // Cập nhật danh mục
    public boolean capNhatDanhMuc(DanhMuc dm) {
        if (dm == null
                || dm.getMaDanhMuc() == null
                || dm.getMaDanhMuc().trim().isEmpty()
                || dm.getTenDanhMuc() == null
                || dm.getTenDanhMuc().trim().isEmpty()) {
            return false;
        }

        String ma = dm.getMaDanhMuc().trim();

        for (int i = 0; i < soLuong; i++) {
            if (dsDanhMuc[i].getMaDanhMuc().equals(ma)) {
                DanhMuc banSao = new DanhMuc(dm);
                banSao.setMaDanhMuc(ma);
                dsDanhMuc[i] = banSao;
                return true;
            }
        }

        return false;
    }

    // Xóa danh mục
    public boolean xoaDanhMuc(String maDanhMuc) {
        if (maDanhMuc == null || maDanhMuc.trim().isEmpty()) {
            return false;
        }

        String ma = maDanhMuc.trim();

        for (int i = 0; i < soLuong; i++) {
            if (dsDanhMuc[i].getMaDanhMuc().equals(ma)) {
                for (int j = i; j < soLuong - 1; j++) {
                    dsDanhMuc[j] = dsDanhMuc[j + 1];
                }

                soLuong--;
                dsDanhMuc[soLuong] = null;
                return true;
            }
        }

        return false;
    }

    // Lấy danh sách danh mục hiện có
    public DanhMuc[] layTatCa() {
        DanhMuc[] ketQua = new DanhMuc[soLuong];

        for (int i = 0; i < soLuong; i++) {
            ketQua[i] = new DanhMuc(dsDanhMuc[i]);
        }

        return ketQua;
    }

    public int getSoLuong() {
        return soLuong;
    }
}
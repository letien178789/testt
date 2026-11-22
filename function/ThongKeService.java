//Note: ThongKeService.java

//Bao gồm: Các phương thức để thống kê dữ liệu: đếm tổng số sản phẩm, đếm tổng số danh mục, đếm số sản phẩm theo danh mục, Đếm số sản phẩm thuộc tất cả danh mục
//Setting chuẩn cho hàm ThongKeDAO: demTongSoSanPham, demTongSoDanhMuc, demSanPhamTheoDanhMuc, thongKeSanPhamTheoDanhMuc
package function;

import model.DanhMuc;
import model.SanPham;

public class ThongKeService {

    // Đếm số phần tử sản phẩm đang sử dụng
    public int demSoSanPham(SanPham[] dsSanPham, int soLuongSP) {
        if (dsSanPham == null
                || soLuongSP < 0
                || soLuongSP > dsSanPham.length) {
            throw new IllegalArgumentException(
                "Danh sách hoặc số lượng sản phẩm không hợp lệ"
            );
        }

        int dem = 0;

        for (int i = 0; i < soLuongSP; i++) {
            if (dsSanPham[i] != null) {
                dem++;
            }
        }

        return dem;
    }

    // Đếm số phần tử danh mục đang sử dụng
    public int demSoDanhMuc(DanhMuc[] dsDanhMuc, int soLuongDM) {
        if (dsDanhMuc == null
                || soLuongDM < 0
                || soLuongDM > dsDanhMuc.length) {
            throw new IllegalArgumentException(
                "Danh sách hoặc số lượng danh mục không hợp lệ"
            );
        }

        int dem = 0;

        for (int i = 0; i < soLuongDM; i++) {
            if (dsDanhMuc[i] != null) {
                dem++;
            }
        }

        return dem;
    }

    // Đếm số sản phẩm thuộc một danh mục
    public int demSanPhamTheoDanhMuc(
            SanPham[] dsSanPham, int soLuongSP, String maDanhMuc) {

        if (dsSanPham == null
                || soLuongSP < 0
                || soLuongSP > dsSanPham.length
                || maDanhMuc == null
                || maDanhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Danh sách, số lượng hoặc mã danh mục không hợp lệ"
            );
        }

        int dem = 0;
        String maCanTim = maDanhMuc.trim();

        for (int i = 0; i < soLuongSP; i++) {
            if (dsSanPham[i] != null
                    && maCanTim.equals(dsSanPham[i].getMaDanhMuc())) {
                dem++;
            }
        }

        return dem;
    }

    // In số sản phẩm thuộc từng danh mục
    public void thongKeSanPhamTheoDanhMuc(
            SanPham[] dsSanPham, int soLuongSP,
            DanhMuc[] dsDanhMuc, int soLuongDM) {

        if (dsDanhMuc == null
                || soLuongDM < 0
                || soLuongDM > dsDanhMuc.length) {
            throw new IllegalArgumentException(
                "Danh sách hoặc số lượng danh mục không hợp lệ"
            );
        }

        for (int i = 0; i < soLuongDM; i++) {
            DanhMuc dm = dsDanhMuc[i];

            if (dm != null && dm.getMaDanhMuc() != null) {
                int soSanPham = demSanPhamTheoDanhMuc(
                    dsSanPham, soLuongSP, dm.getMaDanhMuc()
                );

                System.out.println(
                    dm.getTenDanhMuc() + ": " + soSanPham + " sản phẩm"
                );
            }
        }
    }
}
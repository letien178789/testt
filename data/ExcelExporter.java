package data;

import model.ChiTietPhieu;
import model.LichSuGiaoDich;
import model.PhieuKho;
import model.PhieuNhap;
import model.SanPham;
import model.TonKho;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Phu trach: TienLe
// Xuất báo cáo ra file CSV để mở bằng Excel (không cần thư viện ngoài).
// - Ghi UTF-8 có BOM để Excel hiển thị đúng tiếng Việt.
// - Dòng đầu "sep=;" để Excel tự tách cột bằng dấu ';' (đúng cả máy Windows tiếng Việt).
public class ExcelExporter {
    private static final String SEP = ";";

    // Ghi bảng bất kỳ: mỗi phần tử của cacDong là 1 dòng, mỗi String là 1 ô
    public static boolean ghiCSV(String duongDan, List<String[]> cacDong) {
        try {
            Path p = Paths.get(duongDan);
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
                w.write('\uFEFF');
                w.write("sep=" + SEP + "\r\n");
                for (String[] dong : cacDong) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < dong.length; i++) {
                        if (i > 0) {
                            sb.append(SEP);
                        }
                        sb.append(thoat(dong[i]));
                    }
                    w.write(sb.toString());
                    w.write("\r\n");
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi xuất CSV: " + e.getMessage());
            return false;
        }
    }

    // Ô chứa ';' hoặc '"' hoặc xuống dòng phải bọc trong dấu nháy kép
    private static String thoat(String s) {
        if (s == null) {
            return "";
        }
        if (s.contains(SEP) || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    public static boolean xuatSanPham(List<SanPham> ds, String duongDan) {
        List<String[]> b = new ArrayList<>();
        b.add(new String[]{"Mã SP", "Mã danh mục", "Tên sản phẩm", "Đơn vị tính", "Giá nhập", "Giá bán"});
        for (SanPham sp : ds) {
            b.add(new String[]{sp.getMaSanPham(), sp.getMaDanhMuc(), sp.getTenSanPham(),
                    sp.getDonViTinh(),
                    sp.getGiaNhap() == null ? "" : sp.getGiaNhap().toPlainString(),
                    sp.getGiaBan() == null ? "" : sp.getGiaBan().toPlainString()});
        }
        return ghiCSV(duongDan, b);
    }

    public static boolean xuatTonKho(List<TonKho> ds, String duongDan) {
        List<String[]> b = new ArrayList<>();
        b.add(new String[]{"Mã kho", "Mã SP", "Số lượng tồn", "Mức tối thiểu", "Trạng thái"});
        for (TonKho t : ds) {
            String trangThai = t.hetHang() ? "HẾT HÀNG" : (t.sapHet() ? "SẮP HẾT" : "Bình thường");
            b.add(new String[]{t.getMaKho(), t.getMaSanPham(), String.valueOf(t.getSoLuong()),
                    String.valueOf(t.getMucToiThieu()), trangThai});
        }
        return ghiCSV(duongDan, b);
    }

    public static boolean xuatLichSu(List<LichSuGiaoDich> ds, String duongDan) {
        List<String[]> b = new ArrayList<>();
        b.add(new String[]{"Mã GD", "Thời gian", "Mã kho", "Mã SP", "Loại", "Số lượng",
                "Tồn trước", "Tồn sau", "Mã phiếu", "Ghi chú"});
        for (LichSuGiaoDich ls : ds) {
            b.add(new String[]{ls.getMaGiaoDich(), String.valueOf(ls.getThoiGian()),
                    ls.getMaKho(), ls.getMaSanPham(), ls.getLoaiGiaoDich(),
                    String.valueOf(ls.getSoLuong()), String.valueOf(ls.getTonTruoc()),
                    String.valueOf(ls.getTonSau()), ls.getMaPhieu(), ls.getGhiChu()});
        }
        return ghiCSV(duongDan, b);
    }

    // Phiếu nhập có thêm đơn giá + thành tiền; phiếu xuất chỉ có số lượng (đa hình qua instanceof)
    public static boolean xuatPhieu(PhieuKho phieu, String duongDan) {
        List<String[]> b = new ArrayList<>();
        b.add(new String[]{"Loại phiếu", phieu.loaiPhieu()});
        b.add(new String[]{"Mã phiếu", phieu.getMaPhieu()});
        b.add(new String[]{"Mã kho", phieu.getMaKho()});
        b.add(new String[]{"Ngày", String.valueOf(phieu.getNgay())});
        b.add(new String[]{"Lý do", phieu.getLyDo()});
        b.add(new String[]{"Trạng thái", phieu.getTrangThai()});
        b.add(new String[]{});
        if (phieu instanceof PhieuNhap) {
            PhieuNhap pn = (PhieuNhap) phieu;
            b.add(new String[]{"STT", "Mã SP", "Số lượng", "Đơn giá", "Thành tiền"});
            int stt = 1;
            for (ChiTietPhieu ct : pn.getChiTiet()) {
                b.add(new String[]{String.valueOf(stt++), ct.getMaSanPham(),
                        String.valueOf(ct.getSoLuong()), String.valueOf(ct.getDonGia()),
                        String.valueOf(ct.thanhTien())});
            }
            b.add(new String[]{"", "", "", "Tổng tiền", String.valueOf(pn.tongTien())});
        } else {
            b.add(new String[]{"STT", "Mã SP", "Số lượng"});
            int stt = 1;
            for (ChiTietPhieu ct : phieu.getDanhSachChiTiet()) {
                b.add(new String[]{String.valueOf(stt++), ct.getMaSanPham(),
                        String.valueOf(ct.getSoLuong())});
            }
            b.add(new String[]{"", "Tổng số lượng", String.valueOf(phieu.tinhTongSoLuong())});
        }
        return ghiCSV(duongDan, b);
    }
}
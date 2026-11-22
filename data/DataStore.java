package data;
import function.DanhSachSanPham;
import function.TonKhoService;
import model.SanPham;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
// Phu trach: TienLe
// "Kho dữ liệu trung tâm": giữ tất cả DAO + lưu/nạp toàn bộ ra thư mục file.
// Main chỉ cần: DataStore ds = new DataStore("data"); ds.loadAll(); ... ds.saveAll();
public class DataStore {
    public static final String FILE_DANH_MUC = "danhmuc.txt";
    public static final String FILE_SAN_PHAM = "sanpham.txt";
    public static final String FILE_NHA_CUNG_CAP = "nhacungcap.txt";
    public static final String FILE_KHO = "kho.txt";
    public static final String FILE_TON_KHO = "tonkho.txt";
    public static final String FILE_PHIEU_KHO = "phieukho.txt";
    public static final String FILE_LICH_SU = "lichsu.txt";
    private final FileStorage fileStorage;
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();
    private final SanPhamDAO sanPhamDAO = new DanhSachSanPham(); // MinhAnh/YenNhi cài đặt
    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final KhoDAO khoDAO = new KhoDAO();
    private final TonKhoService tonKhoService;
    private final TonKhoDAO tonKhoDAO;
    private final PhieuKhoDAO phieuKhoDAO;
    private final LichSuGiaoDichDAO lichSuGiaoDichDAO = new LichSuGiaoDichDAO();
    public DataStore(String thuMuc) {
        this(thuMuc, 1000);
    }
    public DataStore(String thuMuc, int sucChuaTonKho) {
        this.fileStorage = new FileStorage(thuMuc);
        this.tonKhoService = new TonKhoService(sucChuaTonKho);
        this.tonKhoDAO = new TonKhoDAO(tonKhoService);
        this.phieuKhoDAO = new PhieuKhoDAO(nhaCungCapDAO);
    }
    // ----- Getter: các lớp khác lấy DAO/Service từ đây -----
    public FileStorage getFileStorage() { return fileStorage; }
    public DanhMucDAO getDanhMucDAO() { return danhMucDAO; }
    public SanPhamDAO getSanPhamDAO() { return sanPhamDAO; }
    public NhaCungCapDAO getNhaCungCapDAO() { return nhaCungCapDAO; }
    public KhoDAO getKhoDAO() { return khoDAO; }
    public TonKhoService getTonKhoService() { return tonKhoService; }
    public TonKhoDAO getTonKhoDAO() { return tonKhoDAO; }
    public PhieuKhoDAO getPhieuKhoDAO() { return phieuKhoDAO; }
    public LichSuGiaoDichDAO getLichSuGiaoDichDAO() { return lichSuGiaoDichDAO; }
    // ================= LƯU =================
    public boolean saveAll() {
        boolean ok = true;
        ok &= fileStorage.ghiDong(FILE_DANH_MUC, danhMucDAO.toDong());
        ok &= fileStorage.ghiDong(FILE_SAN_PHAM, sanPhamToDong());
        ok &= fileStorage.ghiDong(FILE_NHA_CUNG_CAP, nhaCungCapDAO.toDong());
        ok &= fileStorage.ghiDong(FILE_KHO, khoDAO.toDong());
        ok &= tonKhoDAO.ghiFile(fileStorage.duongDan(FILE_TON_KHO).toString());
        ok &= fileStorage.ghiDong(FILE_PHIEU_KHO, phieuKhoDAO.toDong());
        ok &= fileStorage.ghiDong(FILE_LICH_SU, lichSuGiaoDichDAO.toDong());
        return ok;
    }
    // ================= NẠP =================
    // Thứ tự quan trọng: nhà cung cấp phải nạp trước phiếu kho (phiếu nhập cần nối NCC).
    public void loadAll() {
        danhMucDAO.napTuDong(fileStorage.docDong(FILE_DANH_MUC));
        napSanPham(fileStorage.docDong(FILE_SAN_PHAM));
        nhaCungCapDAO.napTuDong(fileStorage.docDong(FILE_NHA_CUNG_CAP));
        khoDAO.napTuDong(fileStorage.docDong(FILE_KHO));
        if (fileStorage.tonTai(FILE_TON_KHO)) {
            tonKhoDAO.docFile(fileStorage.duongDan(FILE_TON_KHO).toString());
        }
        phieuKhoDAO.napTuDong(fileStorage.docDong(FILE_PHIEU_KHO));
        lichSuGiaoDichDAO.napTuDong(fileStorage.docDong(FILE_LICH_SU));
    }
    // ----- Sản phẩm: maSP;maDanhMuc;tenSP;donViTinh;giaNhap;giaBan -----
    private List<String> sanPhamToDong() {
        List<String> dong = new ArrayList<>();
        for (SanPham sp : sanPhamDAO.findAll()) {
            dong.add(FileStorage.lamSach(sp.getMaSanPham()) + ";"
                    + FileStorage.lamSach(sp.getMaDanhMuc()) + ";"
                    + FileStorage.lamSach(sp.getTenSanPham()) + ";"
                    + FileStorage.lamSach(sp.getDonViTinh()) + ";"
                    + chuoiGia(sp.getGiaNhap()) + ";"
                    + chuoiGia(sp.getGiaBan()));
        }
        return dong;
    }
    private String chuoiGia(BigDecimal gia) {
        return gia == null ? "0" : gia.toPlainString();
    }
    private void napSanPham(List<String> cacDong) {
        for (SanPham sp : new ArrayList<>(sanPhamDAO.findAll())) {
            sanPhamDAO.delete(sp.getMaSanPham()); // xóa dữ liệu cũ trước khi nạp
        }
        for (String d : cacDong) {
            String[] p = d.split(";", -1);
            if (p.length < 6) {
                continue;
            }
            try {
                sanPhamDAO.insert(new SanPham(p[0], p[1], p[2], p[3],
                        new BigDecimal(p[4]), new BigDecimal(p[5])));
            } catch (RuntimeException e) {
                System.out.println("Bỏ qua sản phẩm lỗi (" + p[0] + "): " + e.getMessage());
            }
        }
    }
}
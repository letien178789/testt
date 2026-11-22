package function;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.NhaCungCap;
public class NhaCungCapService {
    private final Map<String, NhaCungCap> danhSachNhaCungCap;
    public NhaCungCapService() {
        danhSachNhaCungCap = new LinkedHashMap<>();
    }
    public void them(NhaCungCap nhaCungCap) {
        if (nhaCungCap == null || nhaCungCap.getMaNhaCungCap() == null) {
            throw new IllegalArgumentException("Nhà cung cấp không hợp lệ");
        }
        if (danhSachNhaCungCap.containsKey(nhaCungCap.getMaNhaCungCap())) {
            throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại");
        }
        danhSachNhaCungCap.put(nhaCungCap.getMaNhaCungCap(), nhaCungCap);
    }
    public void capNhat(NhaCungCap nhaCungCap) {
        if (nhaCungCap == null || nhaCungCap.getMaNhaCungCap() == null) {
            throw new IllegalArgumentException("Nhà cung cấp không hợp lệ");
        }
        if (!danhSachNhaCungCap.containsKey(nhaCungCap.getMaNhaCungCap())) {
            throw new IllegalArgumentException("Không tìm thấy nhà cung cấp");
        }
        danhSachNhaCungCap.put(nhaCungCap.getMaNhaCungCap(), nhaCungCap);
    }
    public void xoa(String maNhaCungCap) {
        if (danhSachNhaCungCap.remove(maNhaCungCap) == null) {
            throw new IllegalArgumentException("Không tìm thấy nhà cung cấp");
        }
    }
    public NhaCungCap timTheoMa(String maNhaCungCap) {
        return danhSachNhaCungCap.get(maNhaCungCap);
    }
    public List<NhaCungCap> layTatCa() {
        return new ArrayList<>(danhSachNhaCungCap.values());
    }
}
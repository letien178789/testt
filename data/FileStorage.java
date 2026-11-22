package data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Phu trach: TienLe
// Đọc/ghi dữ liệu bằng file text (UTF-8), không dùng SQL.
// Mỗi "bảng" là 1 file, mỗi bản ghi là 1 dòng, các cột cách nhau bằng dấu ';'.
public class FileStorage {
    private final String thuMuc;

    public FileStorage(String thuMuc) {
        this.thuMuc = thuMuc;
    }

    public String getThuMuc() {
        return thuMuc;
    }

    public Path duongDan(String tenFile) {
        return Paths.get(thuMuc, tenFile);
    }

    public boolean tonTai(String tenFile) {
        return Files.exists(duongDan(tenFile));
    }

    // Bỏ ký tự làm vỡ định dạng dòng (';' và xuống dòng) trước khi ghi
    public static String lamSach(String s) {
        if (s == null) {
            return "";
        }
        return s.replace(";", ",").replace("\r", " ").replace("\n", " ");
    }

    public boolean ghiDong(String tenFile, List<String> cacDong) {
        try {
            Path p = duongDan(tenFile);
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            Files.write(p, cacDong, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi ghi file " + tenFile + ": " + e.getMessage());
            return false;
        }
    }

    // File chưa có -> trả về danh sách rỗng (lần chạy đầu tiên)
    public List<String> docDong(String tenFile) {
        List<String> ketQua = new ArrayList<>();
        if (!tonTai(tenFile)) {
            return ketQua;
        }
        try {
            boolean dauTien = true;
            for (String dong : Files.readAllLines(duongDan(tenFile), StandardCharsets.UTF_8)) {
                if (dauTien && dong.startsWith("\uFEFF")) {
                    dong = dong.substring(1); // bỏ BOM nếu file được sửa bằng Notepad
                }
                dauTien = false;
                if (!dong.trim().isEmpty()) {
                    ketQua.add(dong);
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file " + tenFile + ": " + e.getMessage());
        }
        return ketQua;
    }
}
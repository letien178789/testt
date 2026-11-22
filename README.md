# Khung do an OOP Java - Quan ly kho

Bo khung file `.java` da tao san, KHONG co nghiep vu da duoc cai dat. Khong SQL, khong Maven/pom.xml.

## Cau truc 4 thu muc

- **model/**: du lieu, class co so `PhieuKho` + 2 lop ke thua `PhieuNhap`, `PhieuXuat`.
- **function/**: class danh sach trung tam, interface, Service nghiep vu.
- **data/**: DAO dung Collection, doc/ghi file va xuat Excel.
- **present/**: chuong trinh Console (`Main.java`).

## Phan cong (theo mang da chot)

| Thanh vien | Mang phu trach | File |
|---|---|---|
| TienLe | Luu tru, DAO, Excel, Main | `data/dataStore.java`, `data/FileStorage.java`, `data/ExcelExporter.java`, `data/SanPhamDAO.java`, `data/DanhMucDAO.java`, `data/NhaCungCapDAO.java`, `data/KhoDAO.java`, `data/TonKhoDAO.java`, `data/PhieuKhoDAO.java`, `data/LichSuGiaoDichDAO.java`, `present/Main.java` |
| MinhNgoc | Xuat kho, ton kho, lich su | `model/Kho.java`, `model/TonKho.java`, `model/PhieuKho.java`, `model/PhieuXuat.java`, `model/LichSuGiaoDich.java`, `function/XuatKhoService.java`, `function/TonKhoService.java`, `function/DanhSachPhieuKho.java` |
| YenNhi | San pham, danh muc, thong ke | `model/SanPham.java`, `model/DanhMuc.java`, `function/SanPhamService.java`, `function/DanhMucService.java`, `function/ThongKeService.java`, `function/DanhSachSanPham.java`, `function/IQuanLy.java` |
| PhuongThao | Nhap kho, nha cung cap | `model/NhaCungCap.java`, `model/PhieuNhap.java`, `model/ChiTietPhieu.java`, `function/NhaCungCapService.java`, `function/NhapKhoService.java` |

## Chay Java thuan trong PowerShell (tu thu muc goc)

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$files = (Get-ChildItem -Path model,function,data,present -Recurse -Filter *.java).FullName
javac -encoding UTF-8 -d out $files
java -cp out present.Main
```

**Luu y:** cac file chi la khung, khong co chuc nang nhap/xuat ton kho. Dung JDK 8 tro len cho khung nay. Khi them logic, co the can them import/cap nhat phuong thuc.

- `DAO` o day la lop doc/ghi du lieu Collection, KHONG dung SQL.
- `ExcelExporter` chua co ma tao Excel. Co the xuat CSV bang Java thuan de mo tren Excel hoac tu cai dat XLSX ve sau.
- Muc `IQuanLy` va `DanhSachSanPham` duoc them de dap ung yeu cau giang vien ve interface va danh sach trung tam.
- Moi file co comment ghi ro nguoi phu trach, ten class/package dung voi 4 thu muc.

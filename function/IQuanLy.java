//Note: IQuanLy.java
//Bao gồm: Các phương thức để quản lý dữ liệu: Lấy tất cả dữ liệu, tìm dữ liệu theo mã, kiểm tra tồn tại theo mã, thêm, cập nhật, xóa dữ liệu
//Setting chuẩn cho hàm IQuanLy: findAll, findById, existsById, insert, update, delete
package function;
import java.util.List;

public interface IQuanLy<T, K> {
    List<T> findAll();

    T findById(K ma);

    boolean existsById(K ma);

    void insert(T doiTuong);

    void update(T doiTuong);

    void delete(K ma);
}


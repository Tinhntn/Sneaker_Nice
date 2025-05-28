package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.DAO.NhanVienCustom;
import poly.edu.sneaker.Model.NhanVien;

import java.util.List;
import java.util.UUID;

public interface NhanVienService {

    Page<NhanVienCustom> getAll(Pageable pageable);

    void saveNhanVien(NhanVien nhanVien);

    void updateNhanVien(NhanVien nhanVien, int id);

    NhanVien findNhanVienById(int id);

    Page<NhanVienCustom> search(String keyword,Boolean trangThai, Pageable pageable);

    NhanVien getNhanVienByEmailandMatKhau(String email, String matKhau);

    NhanVien getNhanVienByEmail(String email);

    boolean layLaiMatKhauNhanVien(NhanVien nhanVien);
    List<NhanVien> findAllNhanVien();
    String taoMa();
}

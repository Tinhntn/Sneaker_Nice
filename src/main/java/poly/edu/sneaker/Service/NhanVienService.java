package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.NhanVien;

import java.util.List;

public interface NhanVienService {
    List<NhanVien> getAllNhanVien(int page, int size);
    Page<NhanVien> findAllNhanVien(Pageable pageable);
    NhanVien findNhanVienById(int id);
    void saveNhanVien(NhanVien nhanVien);
    void updateNhanVien(NhanVien nhanVien);
    void deleteNhanVien(int id);
}

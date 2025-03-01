package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.KhachHang;

public interface KhachHangService {
    Page<KhachHang> findByTenKhachHangContainingOrEmailContainingOrSdtContaining(String tenKhachHang, String email, String sdt, Pageable pageable);
    Page<KhachHang> findAll(Pageable pageable);
    KhachHang findById(Integer id);
    void save(KhachHang khachHang);
    void update(KhachHang khachHang);
    void deleteById(Integer id);
}
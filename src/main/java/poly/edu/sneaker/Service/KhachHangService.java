package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.KhachHang;

public interface KhachHangService {
    Page<KhachHang> findAll(Pageable pageable);
    KhachHang findById(int id);
    KhachHang save(KhachHang khachHang);
    void deleteById(int id);
    void update(KhachHang khachHang);
}

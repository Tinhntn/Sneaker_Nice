package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.SanPham;

public interface SanPhamService {
    Page<SanPham> findAll(Pageable pageable);
    void save(SanPham sanPham);
    void delete(SanPham sanPham);
    SanPham findById(int id);
    void update(SanPham sanPham);

    Page<SanPham> findByMaSanPhamOrTenSanPham(String maSanPham,String tenSanPham, Pageable pageable);
}

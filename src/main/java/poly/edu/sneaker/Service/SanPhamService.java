package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.SanPham;

import java.util.List;

public interface SanPhamService {
    Page<SanPham> findAll(Pageable pageable);
    void save(SanPham sanPham);
    void delete(int id);
    SanPham findById(int id);
    void update(SanPham sanPham);
    String taoMaSanPham();
    List<SanPham> getAllSanPhams();
    Page<SanPham> findByMaSanPhamOrTenSanPham(String maSanPham,String tenSanPham, Pageable pageable);

}

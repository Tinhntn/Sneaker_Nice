package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.Interface.SanPhamInterface;
import poly.edu.sneaker.Model.SanPham;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface SanPhamService {
    Page<SanPham> findAll(Pageable pageable);
    void save(SanPham sanPham);
    void delete(int id);
    SanPham findById(int id);
    void update(SanPham sanPham);
    String taoMaSanPham();
    List<SanPham> getAllSanPhams();
    Page<SanPham> findByMaSanPhamOrTenSanPham(String maSanPham, String tenSanPham, Pageable pageable);

    ArrayList<ChiTietSanPham> createProductValidations(int idSanPham);
    Page<SanPham> searchSanPham(String keyword, Date startDate, Date endDate, Pageable pageable);


}
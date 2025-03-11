package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChiTietSanPham;

import java.util.List;

public interface ChiTietSanPhamService {

    // 1) Các hàm đã có
    Page<ChiTietSanPham> findAll(Pageable pageable);
    void saveChiTietSanPham(ChiTietSanPham chiTietSanPham);
    ChiTietSanPham findById(int id);
    void deleteChiTietSanPham(int id);
    void update(ChiTietSanPham chiTietSanPham);
    Page<ChiTietSanPham> findChiTietSanPhamByIDSanPham(int idSanPham, Pageable pageable);
    Page<ChiTietSanPham> findChiTietSanPhamJustOne(Pageable pageable);
    List<ChiTietSanPham> searchByMultipleFields(String keyword);

    // 2) Thêm mới - Tìm theo hãng (nếu chỉ cần lọc hãng)
    Page<ChiTietSanPham> findByHang(String hang, Pageable pageable);

    // 3) Thêm mới - Lọc theo hãng + khoảng giá
    Page<ChiTietSanPham> filterByHangAndPrice(String hang, String priceRange, Pageable pageable);
}

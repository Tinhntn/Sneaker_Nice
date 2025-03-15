package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChiTietSanPham;
import java.util.ArrayList;
import java.util.List;

public interface ChiTietSanPhamService {
    Page<ChiTietSanPham> findAll(Pageable pageable);
    void saveChiTietSanPham(ChiTietSanPham chiTietSanPham);
    ChiTietSanPham findById(int id);
    void deleteChiTietSanPham(int id);
    void update(ChiTietSanPham chiTietSanPham);
    Page<ChiTietSanPham> findChiTietSanPhamByIDSanPham(int idSanPham, Pageable pageable);
    Page<ChiTietSanPham> findChiTietSanPhamJustOne(Pageable pageable);
    ChiTietSanPham findCTSPByIDMauSac(int idCTSP, int idMauSac);
    ArrayList<ChiTietSanPham> findByIdSanPham(int idSanPham);
    ChiTietSanPham findCTSPByIdSPAndIdMauSacAndIdSize(int idSanPham, int idMauSac, int idSize);
    List<ChiTietSanPham> searchByMultipleFields(String keyword);

    // Lọc theo hãng nếu chỉ cần hãng
//    Page<ChiTietSanPham> findByHang(String hang, Pageable pageable);

    // Lọc theo hãng + khoảng giá (các tham số có thể là null hoặc rỗng)
    Page<ChiTietSanPham> filterByHangAndPrice(String hang, String priceRange, Pageable pageable);
}

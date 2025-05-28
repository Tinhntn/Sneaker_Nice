package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChiTietSanPham;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public interface ChiTietSanPhamService {
    Page<ChiTietSanPham> findAll(Pageable pageable);

    void saveChiTietSanPham(ChiTietSanPham chiTietSanPham);

    ChiTietSanPham findById(int id);

    void deleteChiTietSanPham(int id);

    void update(ChiTietSanPham chiTietSanPham);

    Page<ChiTietSanPham> findChiTietSanPhamByIDSanPham(int idSanPham, Pageable pageable);

    Page<ChiTietSanPham> findChiTietSanPhamJustOne(String keyword,
                                                   Integer idHang,
                                                   Integer idDanhMuc,
                                                   Integer idChatLieu,
                                                   Integer idMauSac,
                                                   Integer idSize,
                                                   Pageable pageable);

    List<ChiTietSanPham> searchByMultipleFields(String keyword);

    // Lọc theo hãng, chất liệu và khoảng giá
    Page<ChiTietSanPham> filterByHangAndPrice(String hang, String chatLieu, String priceRange, Pageable pageable);

    // Các phương thức hỗ trợ cascading filter
    List<String> findDistinctChatLieuByHang(String hang);

    List<String> findDistinctHangByChatLieu(String chatLieu);

    // code hung
    List<ChiTietSanPham> getALl();

    List<Map<String, Object>> getTop10NewestProducts();
    //code hung end

    void capNhatSoLuongKhiHuyHoaDon(int idCTSP, int soLuong);

    List<ChiTietSanPham> findIDSPByIDMauSac(int idSP, int idMauSac);

    ArrayList<ChiTietSanPham> findByIdSanPham(int idSanPham);

    ChiTietSanPham findCTSPByIdSPAndIdMauSacAndIdSize(int idSanPham, int idSize, int idMauSac);

    Page<ChiTietSanPham> locChiTietSanPham(int idSanPhan, Integer idSize, Integer idMauSac, Boolean trangThai, Pageable pageable);
}

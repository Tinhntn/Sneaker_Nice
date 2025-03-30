package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.SanPhamBanChayResponse;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonChiTietOnlRepository extends JpaRepository<HoaDonChiTiet, Integer> {


    @Query("SELECT h FROM HoaDonChiTiet h WHERE h.idHoaDon.id = :idHoaDon AND h.idChiTietSanPham.id = :idChiTietSanPham")
    HoaDonChiTiet findByIdHoaDonAndIdChiTietSanPham(@Param("idHoaDon") int idHoaDon, @Param("idChiTietSanPham") int idChiTietSanPham);




    List<HoaDonChiTietOnlCustom> findByIdHoaDon(HoaDon idHoaDon);

    @Query("SELECT h FROM HoaDonChiTiet h WHERE h.idHoaDon.id = :idHoaDon AND h.idChiTietSanPham.id = :idChiTietSanPham")
    Optional<HoaDonChiTiet> findByHoaDonAndSanPham(
            @Param("idHoaDon") int idHoaDon,
            @Param("idChiTietSanPham") int idChiTietSanPham
    );

    @Modifying
    @Query("DELETE FROM HoaDonChiTiet h WHERE h.idHoaDon.id = :idHoaDon AND h.idChiTietSanPham.id = :idChiTietSanPham")
    void xoaChiTietSanPhamTrongHoaDon(@Param("idHoaDon") int idHoaDon, @Param("idChiTietSanPham") int idChiTietSanPham);


    @Query(value = "SELECT * FROM HoaDonChiTiet hct " +
            "WHERE hct.id_hoa_don = :idHoaDon",
            nativeQuery = true)
    List<HoaDonChiTiet> findHoaDonChiTietByHoaDonId(@Param("idHoaDon") Integer idHoaDon);

    @Query(value = "SELECT TOP 5 sp.ten_san_pham AS tenSanPham, " +
            "cts.hinh_anh AS hinhAnh, " +
            "cts.gia_ban AS giaBan, " +
            "CAST(SUM(ct.so_luong) AS BIGINT) AS soLuongBan " +
            "FROM hoadonchitiet ct " +
            "JOIN chitietsanpham cts ON ct.id_chi_tiet_san_pham = cts.id " +
            "JOIN sanpham sp ON cts.id_san_pham = sp.id " +
            "GROUP BY sp.ten_san_pham, cts.hinh_anh, cts.gia_ban " +
            "ORDER BY soLuongBan DESC", nativeQuery = true)
    List<SanPhamBanChayResponse> findTop5SanPhamBanChay();


    @Query(value = "SELECT COALESCE(SUM(ct.so_luong), 0) FROM hoadonchitiet ct " +
            "JOIN hoadon h ON ct.id_hoa_don = h.id " +
            "WHERE h.ngay_tao BETWEEN :startDate AND :endDate", nativeQuery = true)
    Long getTotalProductSoldBetweenDates(@Param("startDate") Date startDate,
                                         @Param("endDate") Date endDate);




    @Query("SELECT h.idChiTietSanPham, SUM(h.soLuong) AS totalSold " +
            "FROM HoaDonChiTiet h " +
            "JOIN h.idHoaDon hd " +
            "WHERE hd.trangThai = 1 " +  // Chỉ lấy hóa đơn đã hoàn thành
            "GROUP BY h.idChiTietSanPham " +
            "ORDER BY totalSold DESC")
    List<Object[]> findTopBestSellingProducts(Pageable pageable);

//    HoaDonChiTiet findHoaDonChiTietByIdHoaDon(int idHoaDon);

}

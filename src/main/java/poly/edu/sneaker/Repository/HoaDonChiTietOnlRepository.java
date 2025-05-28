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
    List<HoaDonChiTiet> findByHoaDonAndSanPham(
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
// thay đổi câu truy vấn cũ
    @Query("SELECT new poly.edu.sneaker.DAO.SanPhamBanChayResponse(sp.tenSanPham, ctsp.hinhAnh, ctsp.giaBan, SUM(hdct.soLuong)) " +
            "FROM HoaDonChiTiet hdct " +
            "JOIN hdct.idHoaDon hd " +
            "JOIN hdct.idChiTietSanPham ctsp " +
            "JOIN ctsp.idSanPham sp " +
            "WHERE hd.trangThai = 1 " +
            "AND hd.ngayTao BETWEEN :startDate AND :endDate " +
            "AND (:loaiSanPham IS NULL OR sp.idDanhMuc.id = :loaiSanPham) " +
            "GROUP BY sp.tenSanPham, ctsp.hinhAnh, ctsp.giaBan " +
            "ORDER BY SUM(hdct.soLuong) DESC")
    List<SanPhamBanChayResponse> getTop5SanPhamBanChay(@Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate,
                                                       @Param("loaiSanPham") Long loaiSanPham);
    //an

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

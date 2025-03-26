package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonChiTietOnlRepository extends JpaRepository<HoaDonChiTiet, Integer> {

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

//    HoaDonChiTiet findHoaDonChiTietByIdHoaDon(int idHoaDon);

}

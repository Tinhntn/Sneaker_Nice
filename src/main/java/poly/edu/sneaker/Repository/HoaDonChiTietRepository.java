package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.sneaker.Model.HoaDonChiTiet;

import java.util.List;

public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet,Integer> {
    //code cua quan
    @Query(value = "SELECT * FROM HoaDonChiTiet hct " +
            "WHERE hct.id_hoa_don = :idHoaDon " +
            "AND hct.trang_thai = 0;",
            nativeQuery = true)
    List<HoaDonChiTiet> findHoaDonChiTietByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);

    List<HoaDonChiTiet> findHoaDonChiTietByIdHoaDon_IdAndIdHoaDon_LoaiHoaDon(int idHoaDon,boolean loaiHoaDon);
    @Query(value = "SELECT * FROM HoaDonChiTiet " +
            "WHERE HoaDonChiTiet.id_chi_tiet_san_pham = :idChiTietSanPham " +
            "AND HoaDonChiTiet.id_hoa_don = :idHoaDon", nativeQuery = true)
    List<HoaDonChiTiet> ktraAddctsplencthd(@Param("idChiTietSanPham") Integer idChiTietSanPham,
                                           @Param("idHoaDon") Integer idHoaDon);

    @Query(value = "SELECT * FROM HoaDonChiTiet hct " +
            "WHERE hct.id = :id " +
            "AND hct.trang_thai = 0;",
            nativeQuery = true)
    List<HoaDonChiTiet> findHoaDonChiTietById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM HoaDonChiTiet hct WHERE hct.id = :id AND hct.trang_thai = 0",
            nativeQuery = true)
    HoaDonChiTiet HoaDonChiTietById(@Param("id") Integer id);

    @Query(value = "SELECT COALESCE(SUM(so_luong * don_gia), 0) FROM HoaDonChiTiet WHERE id_hoa_don = :id", nativeQuery = true)
    Double tongTienCTHD(@Param("id") Integer id);

    //code cua quan
}

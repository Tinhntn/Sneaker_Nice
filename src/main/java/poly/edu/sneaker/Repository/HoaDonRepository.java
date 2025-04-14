package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.HoaDon;

import java.util.List;

@Repository
public interface HoaDonRepository  extends JpaRepository<HoaDon, Integer> {
    @Query(value = "select * from hoadon where trang_thai = 0 ", nativeQuery = true)
    List<HoaDon> getAllHoaDon();

    @Query(value = "SELECT * FROM hoadon WHERE id = :id", nativeQuery = true)
    HoaDon findByHDId(@Param("id") Integer id);

    @Query(value = "SELECT * FROM hoadon WHERE  trang_thai = 1 AND loai_hoa_don = 0 ", nativeQuery = true)
    Page<HoaDon> getAllHoaDon(Pageable pageable);

    @Query(value = "SELECT hd.* FROM hoadon hd " +
            "WHERE hd.ma_hoa_don LIKE %:keyword% ", nativeQuery = true)
    Page<HoaDon> searchByMaHoaDonTenKhachHangOrSdt(@Param("keyword") String keyword,Pageable pageable);

    @Query(value = "SELECT * FROM hoadon " +
            "WHERE ngay_tao BETWEEN :startDate AND :endDate",
            countQuery = "SELECT COUNT(*) FROM hoadon " +
                    "WHERE ngay_tao BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    Page<HoaDon> findByNgayTaoBetween(@Param("startDate") String startDate,
                                      @Param("endDate") String endDate,
                                      Pageable pageable);



}

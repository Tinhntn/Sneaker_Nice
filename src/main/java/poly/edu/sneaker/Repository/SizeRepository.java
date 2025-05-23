package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.Size;

import java.util.Date;
import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {

    @Query("SELECT z FROM Size z")
    Page<Size> getAll(Pageable pageable);

    Size findByMaSize(String maSize);

    @Query(value = "SELECT * FROM Size s " +
            "WHERE s.trang_thai = 1;",
            nativeQuery = true)
    List<Size> getAllSizeTimKiem();
    @Query("SELECT s FROM Size s " +
            "WHERE (:keyword IS NULL OR s.maSize LIKE %:keyword% OR s.tenSize LIKE %:keyword%) " +
            "AND ( " +
            "(:startDate IS NULL AND :endDate IS NULL) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NULL AND s.ngayTao >= :startDate) " +
            "OR (:startDate IS NULL AND :endDate IS NOT NULL AND s.ngayTao <= :endDate) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND s.ngayTao BETWEEN :startDate AND :endDate)) " +
            "AND (:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<Size> getSizeByKeyword(@Param("keyword") String keyword,
                                      @Param("startDate") Date startDate,
                                      @Param("endDate") Date endDate,
                                      @Param("trangThai") Boolean trangThai,
                                      Pageable pageable);
}

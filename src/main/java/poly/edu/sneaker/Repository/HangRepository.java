package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.Hang;

import java.util.Date;
import java.util.List;

@Repository
public interface HangRepository extends JpaRepository<Hang, Integer> {

    @Query("SELECT h.id, h.maHang, h.tenHang, h.ngayTao, h.ngaySua, h.trangThai FROM Hang h")
    Page<Hang> getAll(Pageable pageable);

    Hang findByMaHang(String maHang);

    // code cua quan start
    @Query(value = "SELECT * FROM Hang h " +
            "WHERE h.trang_thai = 1;",
            nativeQuery = true)
    List<Hang> getAllHangTimKiem();

    @Query("SELECT h FROM Hang h " +
            "WHERE (:keyword IS NULL OR h.maHang LIKE %:keyword% OR h.tenHang LIKE %:keyword%) " +
            "AND ( " +
            "(:startDate IS NULL AND :endDate IS NULL) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NULL AND h.ngayTao >= :startDate) " +
            "OR (:startDate IS NULL AND :endDate IS NOT NULL AND h.ngayTao <= :endDate) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND h.ngayTao BETWEEN :startDate AND :endDate)) " +
            "AND (:trangThai IS NULL OR h.trangThai = :trangThai)")
    Page<Hang> getHangByKeyword(@Param("keyword") String keyword,
                                @Param("startDate") Date startDate,
                                @Param("endDate") Date endDate,
                                @Param("trangThai") Boolean trangThai,
                                Pageable pageable);

}

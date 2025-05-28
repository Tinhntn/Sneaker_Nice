package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Model.Size;

import java.util.Date;
import java.util.List;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {
    Page<MauSac> findByMaMauSacContainingOrTenMauSacContaining(String maMauSac, String tenMauSac, Pageable pageable);

    MauSac findByMaMauSac(String maMauSac);
    MauSac findById(int id);
    @Query(value = "SELECT * FROM MauSac ms " +
            "WHERE ms.trang_thai = 1;",
            nativeQuery = true)
    List<MauSac> getAllMauSacTimKiem();

    @Query("SELECT s FROM MauSac s " +
            "WHERE (:keyword IS NULL OR s.maMauSac LIKE %:keyword% OR s.tenMauSac LIKE %:keyword%) " +
            "AND ( " +
            "(:startDate IS NULL AND :endDate IS NULL) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NULL AND s.ngayTao >= :startDate) " +
            "OR (:startDate IS NULL AND :endDate IS NOT NULL AND s.ngayTao <= :endDate) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND s.ngayTao BETWEEN :startDate AND :endDate)) " +
            "AND (:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<MauSac> getMauSacByKeyword(@Param("keyword") String keyword,
                                @Param("startDate") Date startDate,
                                @Param("endDate") Date endDate,
                                @Param("trangThai") Boolean trangThai,
                                Pageable pageable);

}
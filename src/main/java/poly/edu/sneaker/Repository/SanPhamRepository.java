package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.SanPham;

import java.util.Date;
import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    Page<SanPham> findByMaSanPhamContainingOrTenSanPhamContaining(String maSanPham, String tenSanPham, Pageable pageable);
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE (:keyword IS NULL OR sp.maSanPham LIKE %:keyword% OR sp.tenSanPham LIKE %:keyword%) " +
            "AND (:startDate IS NULL OR :endDate IS NULL OR sp.ngayTao BETWEEN :startDate AND :endDate)")
    Page<SanPham> searchSanPham(@Param("keyword") String keyword,
                                @Param("startDate") Date startDate,
                                @Param("endDate") Date endDate,
                                Pageable pageable);



}

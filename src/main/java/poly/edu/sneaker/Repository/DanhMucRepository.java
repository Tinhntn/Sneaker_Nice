package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Model.Hang;

import java.util.Date;
import java.util.List;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    Page<DanhMuc> findByMaDanhMucContainingOrTenDanhMucContaining(String maDanhMuc, String tenDanhMuc, Pageable pageable);

    DanhMuc findByMaDanhMuc(String maDanhMuc);

    // Code Cua Quan Start
    @Query(value = "SELECT * FROM DanhMuc dm " +
            "WHERE dm.trang_thai = 1;",
            nativeQuery = true)
    List<DanhMuc> getAllDanhMucTimKiem();
    // code cua quan end

    boolean existsByTenDanhMuc(String tenDanhMuc);
    @Query("SELECT dm FROM DanhMuc dm " +
            "WHERE (:keyword IS NULL OR dm.maDanhMuc LIKE %:keyword% OR dm.tenDanhMuc LIKE %:keyword%) " +
            "AND ( " +
            "(:startDate IS NULL AND :endDate IS NULL) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NULL AND dm.ngayTao >= :startDate) " +
            "OR (:startDate IS NULL AND :endDate IS NOT NULL AND dm.ngayTao <= :endDate) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND dm.ngayTao BETWEEN :startDate AND :endDate)) " +
            "AND (:trangThai IS NULL OR dm.trangThai = :trangThai)")
    Page<DanhMuc> getDanhMucByKeyword(@Param("keyword") String keyword,
                                @Param("startDate") Date startDate,
                                @Param("endDate") Date endDate,
                                @Param("trangThai") Boolean trangThai,
                                Pageable pageable);
}
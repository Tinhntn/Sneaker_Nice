package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.DanhMuc;

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
}
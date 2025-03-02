package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.DanhMuc;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    Page<DanhMuc> findByMaDanhMucContainingOrTenDanhMucContaining(String maDanhMuc, String tenDanhMuc, Pageable pageable);

    DanhMuc findByMaDanhMuc(String maDanhMuc);
}
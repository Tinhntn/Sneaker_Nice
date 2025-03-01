package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.DanhMuc;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    Page<DanhMuc> findAllByDeletedAt(boolean deletedAt, Pageable pageable);

    Optional<DanhMuc> findByIdAndDeletedAt(int id, boolean deletedAt);

    Page<DanhMuc> findByMaDanhMucContainingAndDeletedAt(String maDanhMuc, boolean deletedAt, Pageable pageable);

    Page<DanhMuc> findByTenDanhMucContainingAndDeletedAt(String tenDanhMuc, boolean deletedAt, Pageable pageable);

    DanhMuc findByMaDanhMucAndDeletedAt(String maDanhMuc, boolean deletedAt);

    Page<DanhMuc> findByIdOrMaDanhMucAndDeletedAt(Integer id, String maDanhMuc, boolean deletedAt, Pageable pageable);

    List<DanhMuc> findByTenDanhMucContaining(String tenDanhMuc);
}
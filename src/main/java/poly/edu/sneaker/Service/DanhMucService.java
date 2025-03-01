package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.DanhMuc;

import java.util.List;

public interface DanhMucService {
    Page<DanhMuc> findAllDanhMuc(Pageable pageable);

    DanhMuc findDanhMucById(int id);

    DanhMuc save(DanhMuc danhMuc);

    void updateDanhMuc(DanhMuc danhMuc);

    void delete(Integer id);

    Page<DanhMuc> findByMaDanhMucContainingAndDeletedAt(String maDanhMuc, boolean deletedAt, Pageable pageable);

    Page<DanhMuc> findByTenDanhMucContainingAndDeletedAt(String tenDanhMuc, boolean deletedAt, Pageable pageable);

    Page<DanhMuc> findAllByDeletedAt(boolean deletedAt, Pageable pageable);

    List<DanhMuc> findByTenDanhMucContaining(String tenDanhMuc);
}
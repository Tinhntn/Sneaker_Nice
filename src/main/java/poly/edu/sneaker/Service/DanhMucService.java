package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.DanhMuc;

public interface DanhMucService {
    Page<DanhMuc> getAll(Pageable pageable);

    DanhMuc findDanhMucById(int id);

    void saveDanhMuc(DanhMuc danhMuc);

    void updateDanhMuc(DanhMuc danhMuc, int id);

    void deleteById(int id);

    Page<DanhMuc> search(String keyword, Pageable pageable);

    DanhMuc findByMaDanhMuc(String maDanhMuc);
}
package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.DanhMuc;

public interface DanhMucService {
    Page<DanhMuc> findAllDanhMuc(Pageable pageable);
    DanhMuc findDanhMucById(int id);
    DanhMuc save(DanhMuc danhMuc);
    void delete(DanhMuc danhMuc);
    void updateDanhMuc(DanhMuc danhMuc);
}

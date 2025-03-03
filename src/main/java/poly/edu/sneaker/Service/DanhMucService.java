package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.DanhMuc;

import java.util.List;

public interface DanhMucService {
    Page<DanhMuc> getAll(Pageable pageable);

    DanhMuc findDanhMucById(int id);
    DanhMuc save(DanhMuc danhMuc);
    void delete(DanhMuc danhMuc);
    void updateDanhMuc(DanhMuc danhMuc);
    List<DanhMuc>  getAllDanhMucs();


    void saveDanhMuc(DanhMuc danhMuc);

    void updateDanhMuc(DanhMuc danhMuc, int id);

    void deleteById(int id);

    Page<DanhMuc> search(String keyword, Pageable pageable);

    DanhMuc findByMaDanhMuc(String maDanhMuc);
}

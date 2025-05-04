package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.DanhMuc;

import java.util.ArrayList;
import java.util.List;

public interface DanhMucService {
    Page<DanhMuc> getAll(Pageable pageable);

    DanhMuc findDanhMucById(int id);

    void save(DanhMuc danhMuc);

    void update(DanhMuc danhMuc, int id);

    void deleteById(int id);

    Page<DanhMuc> search(String keyword, Pageable pageable);

    DanhMuc findByMaDanhMuc(String maDanhMuc);

    String taoMaDanhMuc();

    ArrayList<DanhMuc> getAllDanhMucs();
    boolean existsByTenDanhMuc(String tenDanhMuc);
}
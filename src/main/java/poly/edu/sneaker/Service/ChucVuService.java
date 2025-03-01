package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChucVu;

import java.util.List;

public interface ChucVuService {
    Page<ChucVu> findAll(Pageable pageable);

    ChucVu findById(int id);

    ChucVu save(ChucVu chucVu);

    void deleteById(int id);

    void update(ChucVu chucVu);

    Page<ChucVu> listPage(Pageable pageable);

    void delete(Integer id);

    Page<ChucVu> findByTenChucVuOrMaChucVuAndDeletedAt(String tenChucVu, String maChucVu, boolean deletedAt, Pageable pageable);
}
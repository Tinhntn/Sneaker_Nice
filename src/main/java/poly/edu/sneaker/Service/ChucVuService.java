package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChucVu;

public interface ChucVuService {
    Page<ChucVu> getAll(Pageable pageable);

    ChucVu findChucVuById(int id);

    void save(ChucVu chucVu);

    void update(ChucVu chucVu, int id);

    void deleteById(int id);

    Page<ChucVu> search(String keyword, Pageable pageable);

    ChucVu findByMaChucVu(String maChucVu);
}
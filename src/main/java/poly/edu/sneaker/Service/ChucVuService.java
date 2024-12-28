package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChucVu;

public interface ChucVuService {
    Page<ChucVu> findAll(Pageable pageable);
    ChucVu findById(int id);

    ChucVu save(ChucVu chucVu);
    void delete(ChucVu chucVu);

}

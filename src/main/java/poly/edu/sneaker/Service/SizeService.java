package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.Size;

public interface SizeService {
    Page<Size> findAll(Pageable pageable);
    Size save(Size size);
    Size findById(int id);
    void delete(Size size);
    void deleteById(int id);
    void update(Size size);
}

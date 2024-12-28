package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.HinhAnh;

public interface HinhAnhService {
    Page<HinhAnh> findAll(Pageable pageable);
    HinhAnh findById(int id);
    HinhAnh save(HinhAnh hinhAnh);
    void delete(HinhAnh hinhAnh);
    void deleteById(int id);
    void update(HinhAnh hinhAnh);
}

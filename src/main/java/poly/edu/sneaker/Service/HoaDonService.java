package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.HoaDon;

public interface HoaDonService {
    Page<HoaDon> findAll(Pageable pageable);
    HoaDon findById(int id);
    HoaDon save(HoaDon hoaDon);
    void update(HoaDon hoaDon);
    void deleteById(int id);
}

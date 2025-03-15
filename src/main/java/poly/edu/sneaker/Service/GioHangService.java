package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.GioHang;

public interface GioHangService {
    Page<GioHang> findAll(Pageable pageable);
    GioHang findById(int id);
    GioHang save(GioHang gioHang);
    void delete(GioHang gioHang);
    void deleteById(int id);
    void update(GioHang gioHang);
    String taoMaGioHang();

    GioHang findGioHangByIDKH(int id);
}

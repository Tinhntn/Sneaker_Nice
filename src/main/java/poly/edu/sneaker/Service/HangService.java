package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.Hang;

import java.util.List;

public interface HangService {
    Page<Hang> getAllHangs(Pageable pageable);
    Hang getHangById(int id);
    Hang saveHang(Hang hang);
    void deleteHang(int id);
    void updateHang(Hang hang);
    List<Hang> getAllHangs();
}

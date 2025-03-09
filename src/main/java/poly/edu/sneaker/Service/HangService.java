package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.Hang;

import java.util.ArrayList;
import java.util.List;

public interface HangService {
    Page<Hang> getAll(Pageable pageable);

    Hang getHangById(int id);

    void saveHang(Hang hang);

    void deleteHang(int id);

    void updateHang(Hang hang);

    ArrayList<Hang> getAllHangs();

    void updateHang(Hang hang, int id);

    String taoMaHang();
}

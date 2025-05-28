package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface SizeService {
    Page<Size> findAll(Pageable pageable);
    Size save(Size size);
    Size findById(int id);
    void delete(Size size);
    void deleteById(int id);
    void update(Size size);
    ArrayList<Size> findAll();
    Page<Size> getAll(Pageable pageable);
    Size getSizeById(int id);
    void saveSize(Size size);
//    void deleteSize(int id);
    void updateSize(Size size, int id);
    String taoMaSize();
    Page<Size> locSize(String keyword, LocalDate startDate, LocalDate endDate, Boolean trangThai, Pageable pageable);

}

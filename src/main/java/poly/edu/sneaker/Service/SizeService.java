package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.Size;

public interface SizeService {
    Page<Size> getAll(Pageable pageable);
    Size getSizeById(int id);
    void saveSize(Size size);
//    void deleteSize(int id);
    void updateSize(Size size, int id);
}

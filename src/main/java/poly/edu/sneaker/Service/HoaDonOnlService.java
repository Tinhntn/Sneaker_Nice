package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;

public interface HoaDonOnlService {

    Page<HoaDonOnlCustom> getHoaDonOLChoxacnhan(Pageable pageable);

}

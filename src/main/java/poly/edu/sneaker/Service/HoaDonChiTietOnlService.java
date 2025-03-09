package poly.edu.sneaker.Service;

import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.Model.HoaDon;

import java.util.List;

public interface HoaDonChiTietOnlService {

    List<HoaDonChiTietOnlCustom> findByHoaDonId(HoaDon idhoadon);

}

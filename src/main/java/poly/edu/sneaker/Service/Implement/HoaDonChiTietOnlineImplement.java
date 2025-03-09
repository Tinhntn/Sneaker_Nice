package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Repository.HoaDonChiTietOnlRepository;
import poly.edu.sneaker.Service.HoaDonChiTietOnlService;

import java.util.List;

@Service
public class HoaDonChiTietOnlineImplement implements HoaDonChiTietOnlService {

    @Autowired
    HoaDonChiTietOnlRepository hoaDonChiTietOnlRepository;

    @Override
    public List<HoaDonChiTietOnlCustom> findByHoaDonId(HoaDon idhoadon) {
        return hoaDonChiTietOnlRepository.findByIdHoaDon(idhoadon);
    }
}

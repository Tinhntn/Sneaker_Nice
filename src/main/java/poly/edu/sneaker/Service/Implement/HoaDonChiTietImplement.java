package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.HoaDonChiTiet;
import poly.edu.sneaker.Repository.HoaDonChiTietRepository;
import poly.edu.sneaker.Service.HoaDonChiTietService;
import poly.edu.sneaker.Service.HoaDonService;

@Service
public class HoaDonChiTietImplement implements HoaDonChiTietService {
    @Autowired
    HoaDonChiTietRepository hoaDonChiTietRepository;
    @Override
    public void saveHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        hoaDonChiTietRepository.save(hoaDonChiTiet);

    }

    @Override
    public HoaDonChiTiet findHoaDonChiTietByID(int id) {
        return hoaDonChiTietRepository.findById(id).get();
    }

    @Override
    public boolean deleteHoaDonChiTiet(int id) {
        hoaDonChiTietRepository.deleteById(id);
        return true;
    }


}

package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Repository.HoaDonOnlRepository;
import poly.edu.sneaker.Service.HoaDonOnlService;

@Service
public class HoaDonOnlineImplement implements HoaDonOnlService {

    @Autowired
    HoaDonOnlRepository hoaDonOnlRepository;

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomDH(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomDH(pageable);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonOLChoxacnhan(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomCXN(pageable);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonOLCholayhang(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomCLH(pageable);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomDG(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomDG(pageable);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomHT(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomHT(pageable);
    }

    @Override
    public HoaDon detailHD(int id) {
        return hoaDonOnlRepository.getAllHoaDonByid(id);
    }

    @Override
    public void updateHoaDon(HoaDon hd, int id) {
        hoaDonOnlRepository.save(hd);
    }
}

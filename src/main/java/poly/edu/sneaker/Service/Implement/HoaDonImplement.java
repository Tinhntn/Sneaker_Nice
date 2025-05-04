package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;
import poly.edu.sneaker.Repository.HoaDonChiTietRepository;
import poly.edu.sneaker.Repository.HoaDonRepository;
import poly.edu.sneaker.Service.HoaDonService;

import java.util.List;
import java.util.Random;

@Service

public class HoaDonImplement implements HoaDonService {
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    HoaDonChiTietRepository hoaDonChiTietRepository;
    @Override
    public Page<HoaDon> findAll(Pageable pageable) {
        return hoaDonRepository.findAll(pageable);
    }

    @Override
    public HoaDon findById(int id) {
        return hoaDonRepository.findById(id).get();
    }

    @Override
    public HoaDon save(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public void update(HoaDon hoaDon) {
        hoaDonRepository.save(hoaDon);
    }

    @Override
    public void deleteById(int id) {
        hoaDonRepository.deleteById(id);
    }

    @Override
    public Page<HoaDon> getAllHoaDonTaiQuay(int page, int size) {
        return hoaDonRepository.getAllHoaDon(PageRequest.of(page,size));
    }

    @Override
    public List<HoaDonChiTiet> danhSachChiTietHoaDonByIDHD(Integer id) {
        return hoaDonChiTietRepository.findHoaDonChiTietByIdHoaDon(id);
    }

    @Override
    public String taoMaHoaDon() {
        Random random = new Random();
        String maHoaDon = "HD"+1000+random.nextInt(9000);
        for ( HoaDon hoaDon : hoaDonRepository.findAll()
             ) {
            if(hoaDon.getMaHoaDon().equals(maHoaDon)){
                maHoaDon = "HD"+1000+random.nextInt(9000);            }

        }
        return maHoaDon;
    }

    //code quan

}

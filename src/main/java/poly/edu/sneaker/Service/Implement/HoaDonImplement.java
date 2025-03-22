package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Repository.HoaDonRepository;
import poly.edu.sneaker.Service.HoaDonService;

import java.util.Random;

@Service

public class HoaDonImplement implements HoaDonService {
    @Autowired
    HoaDonRepository hoaDonRepository;
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
}

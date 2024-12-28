package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Repository.HoaDonRepository;
import poly.edu.sneaker.Service.HoaDonService;
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
}

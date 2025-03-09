package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.GioHang;
import poly.edu.sneaker.Repository.GioHangRepository;
import poly.edu.sneaker.Service.GioHangService;

import java.util.ArrayList;
import java.util.Random;

@Service

public class GioHangImplement implements GioHangService {
    @Autowired
    GioHangRepository gioHangRepository;


    @Override
    public Page<GioHang> findAll(Pageable pageable) {
        return gioHangRepository.findAll(pageable);
    }

    @Override
    public GioHang findById(int id) {
        return gioHangRepository.findById(id).get();
    }

    @Override
    public GioHang save(GioHang gioHang) {
        return gioHangRepository.save(gioHang);
    }

    @Override
    public void delete(GioHang gioHang) {
        gioHangRepository.delete(gioHang);
    }

    @Override
    public void deleteById(int id) {
        gioHangRepository.deleteById(id);
    }

    @Override
    public void update(GioHang gioHang) {
        gioHangRepository.save(gioHang);
    }

    @Override
    public String taoMaGioHang() {
        Random random = new Random();
        String maGioHang = "GioHang"+random.nextInt(9000);
        for (GioHang gioHang : gioHangRepository.findAll()) {
            if(maGioHang.equals(gioHang.getMaGioHang())){
                maGioHang ="GioHang"+random.nextInt(9000);
            }
        }
        return maGioHang;
    }
}

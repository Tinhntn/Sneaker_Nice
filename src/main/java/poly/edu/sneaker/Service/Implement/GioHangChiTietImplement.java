package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.GioHangChiTiet;
import poly.edu.sneaker.Repository.GioHangChiTietRepository;
import poly.edu.sneaker.Service.GioHangChiTietService;
@Service

public class GioHangChiTietImplement implements GioHangChiTietService {
    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;
    @Override
    public Page<GioHangChiTiet> findAll(Pageable pageable) {
        return gioHangChiTietRepository.findAll(pageable);
    }

    @Override
    public GioHangChiTiet findById(int id) {
        return gioHangChiTietRepository.findById(id).get();
    }

    @Override
    public GioHangChiTiet saveGioHangChitiet(GioHangChiTiet gioHangChiTiet) {
        return gioHangChiTietRepository.save(gioHangChiTiet);
    }

    @Override
    public void deleteGioHangChitiet(int id) {
        gioHangChiTietRepository.deleteById(id);
    }

    @Override
    public void update(GioHangChiTiet gioHangChiTiet) {
        gioHangChiTietRepository.save(gioHangChiTiet);
    }
}

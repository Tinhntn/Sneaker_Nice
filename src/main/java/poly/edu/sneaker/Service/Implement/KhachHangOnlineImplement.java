package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Repository.HoaDonRepository;
import poly.edu.sneaker.Repository.KhachHangRepository;
import poly.edu.sneaker.Service.KhachHangOnlineService;

@Service
public class KhachHangOnlineImplement implements KhachHangOnlineService {
    @Autowired
    KhachHangRepository khachHangRepository;

    @Override
    public KhachHang layKhachHangQuaid(Integer id) {
        return khachHangRepository.findKhachHangByIdkh(id);
    }

    @Override
    public KhachHang saveKH(KhachHang kh) {
        return khachHangRepository.save(kh);
    }
}

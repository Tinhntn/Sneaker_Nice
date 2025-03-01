package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Repository.KhachHangRepository;
import poly.edu.sneaker.Service.KhachHangService;

@Service
public class KhachHangImplement implements KhachHangService {

    private final KhachHangRepository khachHangRepository;

    @Autowired
    public KhachHangImplement(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    public Page<KhachHang> findByTenKhachHangContainingOrEmailContainingOrSdtContaining(String tenKhachHang, String email, String sdt, Pageable pageable) {
        return khachHangRepository.findByTenKhachHangContainingOrEmailContainingOrSdtContaining(tenKhachHang, email, sdt, pageable);
    }

    @Override
    public Page<KhachHang> findAll(Pageable pageable) {
        return khachHangRepository.findAll(pageable);
    }

    @Override
    public KhachHang findById(Integer id) {
        return khachHangRepository.findById(id).orElse(null);
    }

    @Override
    public void save(KhachHang khachHang) {
        khachHangRepository.save(khachHang);
    }

    @Override
    public void update(KhachHang khachHang) {
        khachHangRepository.save(khachHang);
    }

    @Override
    public void deleteById(Integer id) {
        khachHangRepository.deleteById(id);
    }
}
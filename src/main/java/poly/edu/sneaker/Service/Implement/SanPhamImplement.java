package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.SanPham;
import poly.edu.sneaker.Repository.SanPhamRepository;
import poly.edu.sneaker.Service.SanPhamService;
@Service

public class SanPhamImplement implements SanPhamService {
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Override
    public Page<SanPham> findAll(Pageable pageable) {
        return sanPhamRepository.findAll(pageable);
    }

    @Override
    public void save(SanPham sanPham) {
        sanPhamRepository.save(sanPham);
    }

    @Override
    public void delete(SanPham sanPham) {
        sanPhamRepository.delete(sanPham);
    }

    @Override
    public SanPham findById(int id) {
        return sanPhamRepository.findById(id).get();
    }

    @Override
    public void update(SanPham sanPham) {
        sanPhamRepository.save(sanPham);
    }

    @Override
    public Page<SanPham> findByMaSanPhamOrTenSanPham(String maSanPham, String tenSanPham, Pageable pageable) {
        return sanPhamRepository.findByMaSanPhamContainingOrTenSanPhamContaining(maSanPham,tenSanPham,pageable);
    }

}

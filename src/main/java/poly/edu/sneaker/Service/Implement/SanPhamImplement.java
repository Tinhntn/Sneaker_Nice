package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.Interface.SanPhamInterface;
import poly.edu.sneaker.Model.SanPham;
import poly.edu.sneaker.Repository.SanPhamRepository;
import poly.edu.sneaker.Service.SanPhamService;

import java.util.List;
import java.util.Random;

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
    public void delete(int id) {
        sanPhamRepository.deleteById(id);
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
    public String taoMaSanPham() {
        Random random = new Random();
        int randomNumber = 1000+random.nextInt(9000);
        return "SP"+randomNumber;
    }

    @Override
    public List<SanPham> getAllSanPhams() {
        return sanPhamRepository.findAll();
    }

    @Override
    public Page<SanPham> findByMaSanPhamOrTenSanPham(String maSanPham, String tenSanPham, Pageable pageable) {
        return sanPhamRepository.findByMaSanPhamContainingOrTenSanPhamContaining(maSanPham,tenSanPham,pageable);
    }


}

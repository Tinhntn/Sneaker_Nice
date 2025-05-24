package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.Interface.SanPhamInterface;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Model.SanPham;
import poly.edu.sneaker.Model.Size;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Repository.MauSacRepository;
import poly.edu.sneaker.Repository.SanPhamRepository;
import poly.edu.sneaker.Repository.SizeRepository;
import poly.edu.sneaker.Service.SanPhamService;

import java.util.*;

@Service
public class SanPhamImplement implements SanPhamService {
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private MauSacRepository mauSacRepository;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;


    @Override
    public Page<SanPham> findAll(Pageable pageable) {
        return sanPhamRepository.findAll(pageable);
    }

    @Override
    public void save(SanPham sanPham) {
        sanPham.setMaSanPham(taoMaSanPham());
        System.out.println(sanPham);
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
        int randomNumber = 1000 + random.nextInt(9000);
        return "SP" + randomNumber;
    }

    @Override
    public List<SanPham> getAllSanPhams() {
        return sanPhamRepository.findAll();
    }

    @Override
    public Page<SanPham> searchSanPham(String keyword, Date startDate, Date endDate, Pageable pageable) {
        return sanPhamRepository.searchSanPham(keyword, startDate, endDate, pageable);
    }





}
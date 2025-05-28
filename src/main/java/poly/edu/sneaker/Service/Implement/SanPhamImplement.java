package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.time.LocalDate;
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

    public Page<SanPham> findByMaSanPhamOrTenSanPham(String maSanPham, String tenSanPham, Pageable pageable) {
        return sanPhamRepository.findByMaSanPhamContainingOrTenSanPhamContaining(maSanPham, tenSanPham, pageable);
    }

    @Override
    public Page<SanPham> filterSanPham(String keyword, LocalDate startDate, LocalDate endDate,
                                       Integer idDanhMuc, Integer idChatLieu, Integer idHang,
                                       int page, int size) {
        Date start = startDate != null ? java.sql.Date.valueOf(startDate) : null;
        Date end = endDate != null ? java.sql.Date.valueOf(endDate) : null;
        if (end != null) {
            // Gán endDate thành 23:59:59 để bao phủ hết ngày đó
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            end = cal.getTime();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return sanPhamRepository.filterSanPham(
                keyword, start, end,
                idDanhMuc, idChatLieu, idHang,
                pageable
        );

    }


}
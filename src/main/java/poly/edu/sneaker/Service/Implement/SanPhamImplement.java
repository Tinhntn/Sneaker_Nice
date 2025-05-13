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
    public Page<SanPham> searchSanPham(String keyword, Date startDate, Date endDate, Pageable pageable) {
        return sanPhamRepository.searchSanPham(keyword, startDate, endDate, pageable);
    }
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
    public ArrayList<ChiTietSanPham> createProductValidations(int idSanPham) {
        SanPham sanPham = sanPhamRepository.findById(idSanPham).orElse(null);
        if (sanPham == null) return new ArrayList<>();

        List<MauSac> mauSacs = mauSacRepository.findAll();
        List<Size> sizes = sizeRepository.findAll();
        List<ChiTietSanPham> existingVariants = chiTietSanPhamRepository.findChiTietSanPhamByIdSanPham_Id(idSanPham);

        // Tạo tập hợp để kiểm tra nhanh biến thể đã có
        Set<String> existingKeys = new HashSet<>();
        for (ChiTietSanPham ctsp : existingVariants) {
            String key = ctsp.getIdMauSac().getId() + "_" + ctsp.getIdSize().getId();
            existingKeys.add(key);
        }

        // Tạo những biến thể chưa tồn tại
        for (MauSac mau : mauSacs) {
            for (Size size : sizes) {
                String newKey = mau.getId() + "_" + size.getId();
                if (!existingKeys.contains(newKey)) {
                    ChiTietSanPham ctspNew = new ChiTietSanPham();
                    ctspNew.setIdSanPham(sanPham);
                    ctspNew.setIdSize(size);
                    ctspNew.setIdMauSac(mau);
                    ctspNew.setTrongLuong(1);
                    ctspNew.setGiaNhap(0);
                    ctspNew.setGiaBan(0);
                    ctspNew.setSoLuong(1);
                    ctspNew.setMoTa("Create products validations");
                    ctspNew.setNgayTao(new Date());
                    ctspNew.setTrangThai(true);
                    chiTietSanPhamRepository.save(ctspNew);
                    existingKeys.add(newKey); // Cập nhật vào Set để phòng trường hợp gọi nhiều lần liên tiếp
                }
            }
        }

        return (ArrayList<ChiTietSanPham>) chiTietSanPhamRepository.findChiTietSanPhamByIdSanPham_Id(idSanPham);
    }





}
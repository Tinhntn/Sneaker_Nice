package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Service.ChiTietSanPhamService;

import java.util.List;

@Service

public class ChiTietSanPhamImplement implements ChiTietSanPhamService {
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Override
    public Page<ChiTietSanPham> findAll(Pageable pageable) {

        return chiTietSanPhamRepository.findAll(pageable);
    }

    @Override
    public void saveChiTietSanPham(ChiTietSanPham chiTietSanPham) {
        chiTietSanPhamRepository.save(chiTietSanPham);
    }

    @Override
    public ChiTietSanPham findById(int id) {
        return chiTietSanPhamRepository.findById(id).get();
    }

    @Override
    public void deleteChiTietSanPham(int id) {
        chiTietSanPhamRepository.deleteById(id);
    }

    @Override
    public void update(ChiTietSanPham chiTietSanPham) {
        chiTietSanPhamRepository.save(chiTietSanPham);
    }

    @Override
    public Page<ChiTietSanPham> findChiTietSanPhamByIDSanPham(int idSanPham, Pageable pageable) {
        return chiTietSanPhamRepository.findChiTietSanPhamByIdSanPham_Id(idSanPham,pageable);
    }

    @Override
    public Page<ChiTietSanPham> findChiTietSanPhamJustOne(Pageable pageable) {
        return chiTietSanPhamRepository.findFirstRecordForEachProduct(pageable);
    }



    @Override
    public List<ChiTietSanPham> searchByMultipleFields(String keyword) {
        List<ChiTietSanPham> list = chiTietSanPhamRepository.searchByMultipleFields(keyword);
        if (list.size() > 5) {
            return list.subList(0, 5);
        }
        return list;

    }

    @Override
    public Page<ChiTietSanPham> findByHang(String hang, Pageable pageable) {
        if (hang == null || hang.trim().isEmpty()) {
            return chiTietSanPhamRepository.findAll(pageable);
        }
        return chiTietSanPhamRepository.findByHang(hang, pageable);
    }

    @Override
    public Page<ChiTietSanPham> filterByHangAndPrice(String hang, String priceRange, Pageable pageable) {
        // 1) Tính minPrice, maxPrice từ priceRange
        long minPrice = 0;
        long maxPrice = Long.MAX_VALUE;

        if (priceRange != null) {
            switch (priceRange) {
                case "duoi500":
                    maxPrice = 500000;
                    break;
                case "500-1000":
                    minPrice = 500000; maxPrice = 1000000; break;
                case "1000-2000":
                    minPrice = 1000000; maxPrice = 2000000; break;
                case "2000-3000":
                    minPrice = 2000000; maxPrice = 3000000; break;
                case "3000-5000":
                    minPrice = 3000000; maxPrice = 5000000; break;
                case "tren5000":
                    minPrice = 5000000; // max = Long.MAX_VALUE
                    break;
                default:
                    break;
            }
        }

        // 2) Nếu hang trống => set null để query bỏ qua
        if (hang != null && hang.trim().isEmpty()) {
            hang = null;
        }

        // 3) Gọi repository custom
        return chiTietSanPhamRepository.filterByHangAndPrice(hang, minPrice, maxPrice, pageable);
    }



}


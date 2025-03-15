package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChiTietSanPhamImplement implements ChiTietSanPhamService {

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;



    // Các phương thức còn lại được cài đặt tương tự...

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
        return chiTietSanPhamRepository.findChiTietSanPhamByIdSanPham_Id(idSanPham, pageable);
    }

    @Override
    public Page<ChiTietSanPham> findChiTietSanPhamJustOne(Pageable pageable) {
        return chiTietSanPhamRepository.findFirstRecordForEachProduct(pageable);
    }

    @Override
    public ChiTietSanPham findCTSPByIDMauSac(int idCTSP, int idMauSac) {
        return chiTietSanPhamRepository.findChiTietSanPhamByIdAndIdMauSacAndTrangThai(idCTSP, idMauSac, true);
    }

    @Override
    public ArrayList<ChiTietSanPham> findByIdSanPham(int idSanPham) {
        return chiTietSanPhamRepository.findByIdSanPham_IdAndTrangThai(idSanPham, true);
    }

    @Override
    public ChiTietSanPham findCTSPByIdSPAndIdMauSacAndIdSize(int idSanPham, int idMauSac, int idSize) {
        return chiTietSanPhamRepository.findChiTietSanPhamByIdSanPham_IdAndIdSize_IdAndIdMauSac_Id(idSanPham, idSize, idMauSac);
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
public Page<ChiTietSanPham> filterByHangAndPrice(String hang, String priceRange, Pageable pageable) {
    // Mặc định không có giới hạn giá
    long minPrice = 0;
    long maxPrice = Long.MAX_VALUE;

    // Nếu có chỉ định khoảng giá thì tính toán minPrice, maxPrice
    if (priceRange != null && !priceRange.trim().isEmpty()) {
        switch (priceRange) {
            case "duoi500":
                maxPrice = 500000;
                break;
            case "500-1000":
                minPrice = 500000;
                maxPrice = 1000000;
                break;
            case "1000-2000":
                minPrice = 1000000;
                maxPrice = 2000000;
                break;
            case "2000-3000":
                minPrice = 2000000;
                maxPrice = 3000000;
                break;
            case "3000-5000":
                minPrice = 3000000;
                maxPrice = 5000000;
                break;
            case "tren5000":
                minPrice = 5000000;
                break;
            default:
                // Nếu không khớp, giữ mặc định
                break;
        }
    }

    // Nếu biến hang rỗng thì chuyển về null để không áp dụng điều kiện hãng
    if (hang != null && hang.trim().isEmpty()) {
        hang = null;
    }

    // Gọi repository với điều kiện: nếu hang khác null thì chỉ lọc theo hãng và giá,
    // ngược lại chỉ lọc theo khoảng giá.
    return chiTietSanPhamRepository.filterByHangAndPrice(hang, minPrice, maxPrice, pageable);
}

}

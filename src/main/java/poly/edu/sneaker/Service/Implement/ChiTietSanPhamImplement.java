package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;
import java.util.Map;

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
        return chiTietSanPhamRepository.findById(id);
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

    public List<ChiTietSanPham> getALl() {
        return chiTietSanPhamRepository.getALl();
    }

    @Override
    public List<Map<String, Object>> getTop10NewestProducts() {
        List<ChiTietSanPham> results = chiTietSanPhamRepository.findTop10NewestProducts(PageRequest.of(0, 10));
        List<Map<String, Object>> newestProducts = new ArrayList<>();

        for (ChiTietSanPham chiTietSanPham : results) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("id",chiTietSanPham.getId());
            productData.put("tenSanPham", chiTietSanPham.getIdSanPham().getTenSanPham());
            productData.put("size", chiTietSanPham.getIdSize().getTenSize());
            productData.put("mauSac", chiTietSanPham.getIdMauSac().getTenMauSac());
            productData.put("giaBan", chiTietSanPham.getGiaBan());
            productData.put("hinhAnh", chiTietSanPham.getHinhAnh());
            productData.put("ngayTao", chiTietSanPham.getNgayTao());

            newestProducts.add(productData);
        }

        return newestProducts;
    }

    @Override
    public void capNhatSoLuongKhiHuyHoaDon(int idCTSP, int soLuong) {
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(idCTSP);
        chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() + soLuong);
    }
    @Override
    public List<ChiTietSanPham> findIDSPByIDMauSac(int idSP, int idMauSac) {
        return chiTietSanPhamRepository.findChiTietSanPhamByIdSPAndIdMauSac(idSP,idMauSac);
    }

    @Override
    public ArrayList<ChiTietSanPham> findByIdSanPham(int idSanPham) {
        return chiTietSanPhamRepository.findByIdSanPham_IdAndTrangThai(idSanPham, true);
    }

    @Override
    public ChiTietSanPham findCTSPByIdSPAndIdMauSacAndIdSize(int idSanPham, int idSize, int idMauSac) {
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
    public Page<ChiTietSanPham> filterByHangAndPrice(String hang, String chatLieu, String priceRange, Pageable pageable) {
        long minPrice = 0;
        long maxPrice = Long.MAX_VALUE;

        if (priceRange != null && !priceRange.trim().isEmpty()) {
            switch (priceRange) {
                case "duoi500":
                    maxPrice = 500000;
                    break;
                case "500000-1000000":
                    minPrice = 500000;
                    maxPrice = 1000000;
                    break;
                case "1000000-2000000":
                    minPrice = 1000000;
                    maxPrice = 2000000;
                    break;
                case "2000000-3000000":
                    minPrice = 2000000;
                    maxPrice = 3000000;
                    break;
                case "3000000-5000000":
                    minPrice = 3000000;
                    maxPrice = 5000000;
                    break;
                case "tren5000000":
                    minPrice = 5000000;
                    break;
                default:
                    break;
            }
        }

        if (hang != null && hang.trim().isEmpty()) {
            hang = null;
        }
        if (chatLieu != null && chatLieu.trim().isEmpty()) {
            chatLieu = null;
        }

        return chiTietSanPhamRepository.filterByHangAndPrice(hang, chatLieu, minPrice, maxPrice, pageable);
    }

    @Override
    public List<String> findDistinctChatLieuByHang(String hang) {
        return chiTietSanPhamRepository.findDistinctChatLieuByHang(hang);
    }

    @Override
    public List<String> findDistinctHangByChatLieu(String chatLieu) {
        return chiTietSanPhamRepository.findDistinctHangByChatLieu(chatLieu);
    }



}

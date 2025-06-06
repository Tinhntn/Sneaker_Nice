package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;
import poly.edu.sneaker.Model.KhuyenMai;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Repository.HoaDonChiTietOnlRepository;
import poly.edu.sneaker.Repository.HoaDonOnlRepository;
import poly.edu.sneaker.Service.HoaDonChiTietOnlService;
import poly.edu.sneaker.Service.HoaDonOnlService;
import poly.edu.sneaker.Service.KhuyenMaiService;

import java.util.*;

@Service
public class HoaDonChiTietOnlineImplement implements HoaDonChiTietOnlService {

    @Autowired
    HoaDonOnlService hoaDonOnlService;

    @Autowired
    HoaDonChiTietOnlRepository hoaDonChiTietOnlRepository;

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    HoaDonOnlRepository hoaDonOnlRepository;

    @Autowired
    KhuyenMaiService khuyenMaiService;

    @Override
    public List<HoaDonChiTietOnlCustom> findByHoaDonId(HoaDon idhoadon) {
        return hoaDonChiTietOnlRepository.findByIdHoaDon(idhoadon);
    }


    @Override
    public List<HoaDonChiTiet> findHoaDonChiTietByHoaDonId(Integer idHoaDon) {
        return hoaDonChiTietOnlRepository.findHoaDonChiTietByHoaDonId(idHoaDon);
    }

    @Override
    public void updateSoLuong(int idChiTietHoaDon, int idChiTietSanPham, int soLuongMoi) {
        HoaDonChiTiet chiTiet = hoaDonChiTietOnlRepository.findById(idChiTietHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết hóa đơn!"));

        ChiTietSanPham sanPham = chiTietSanPhamRepository.findById(idChiTietSanPham);

        // Kiểm tra số lượng sản phẩm có đủ không
        if (sanPham.getSoLuong() < chiTiet.getSoLuong()) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ!");
        }

        // Cập nhật số lượng trong chi tiết hóa đơn
        chiTiet.setSoLuong(soLuongMoi);
        hoaDonChiTietOnlRepository.save(chiTiet);


    }

    @Override
    public boolean themSPCTVaoHDCT(HoaDonChiTiet hoaDonChiTiet) {
        try {
            // Kiểm tra hóa đơn có tồn tại không
            Optional<HoaDon> hoaDonOpt = hoaDonOnlService.findHoaDonById(hoaDonChiTiet.getIdHoaDon().getId());
            if (!hoaDonOpt.isPresent()) return false;

            // Kiểm tra sản phẩm chi tiết có tồn tại không
            ChiTietSanPham chiTietOpt = chiTietSanPhamRepository.findById(hoaDonChiTiet.getIdChiTietSanPham().getId());
            if (chiTietOpt == null || !chiTietOpt.getTrangThai() || !chiTietOpt.getTrangThai()) {
                return false;
            }
            //mặc định nó là 1
            int soLuongMua = hoaDonChiTiet.getSoLuong();
            //kiểm tra xem hóa đơn có ở trạng thái đã xác nhận hay chưa
            boolean isHoaDonDaThanhToan = hoaDonChiTiet.getIdHoaDon().getTrangThai() == 3;

            // Kiểm tra nếu sản phẩm đã có trong hóa đơn chi tiết hay chưa
            List<HoaDonChiTiet> lstHoaDonCoSP = hoaDonChiTietOnlRepository.findByHoaDonAndSanPham(
                    hoaDonChiTiet.getIdHoaDon().getId(), chiTietOpt.getId()
            );
            //kiểm tra xem có ctsp trong hóa đơn hay chưa
            if (!lstHoaDonCoSP.isEmpty()) {
                // Tính tổng số lượng đang có trong hóa đơn
                int tongSoLuongTrongHoaDon = lstHoaDonCoSP.stream()
                        .mapToInt(HoaDonChiTiet::getSoLuong)
                        .sum();
                // Kiểm tra nếu vượt quá số lượng tồn kho
                if (tongSoLuongTrongHoaDon + soLuongMua > chiTietOpt.getSoLuong()) {
                    return false;
                }
                // Tìm HDCT có cùng giá
                Optional<HoaDonChiTiet> hdctCungGiaOpt = lstHoaDonCoSP.stream()
                        .filter(hdct -> hdct.getDonGia() == chiTietOpt.getGiaBan())
                        .findFirst();
                if (hdctCungGiaOpt.isPresent()) {
                    // Cập nhật HDCT có cùng giá
                    HoaDonChiTiet hdct = hdctCungGiaOpt.get();
                    int soLuongCu = hdct.getSoLuong();
                    hdct.setSoLuong(soLuongCu + soLuongMua);
                    hdct.setGhiChu("Đã cộng thêm " + soLuongMua + " sản phẩm");
                    hdct.setTongTrongLuong(chiTietOpt.getTrongLuong() * hdct.getSoLuong());
                    hdct.setNgaySua(new Date());
                    hoaDonChiTietOnlRepository.save(hdct);
                    if (isHoaDonDaThanhToan) {
                        chiTietOpt.setSoLuong(chiTietOpt.getSoLuong() - soLuongMua);
                        chiTietSanPhamRepository.save(chiTietOpt);
                    }
                    return true;
                } else {
                    // Thêm mới HDCT nếu không có cùng giá
                    HoaDonChiTiet hoaDonChiTietMoi = new HoaDonChiTiet();
                    hoaDonChiTietMoi.setIdHoaDon(hoaDonChiTiet.getIdHoaDon());
                    hoaDonChiTietMoi.setIdChiTietSanPham(chiTietOpt);
                    hoaDonChiTietMoi.setDonGia(chiTietOpt.getGiaBan());
                    hoaDonChiTietMoi.setSoLuong(soLuongMua);
                    hoaDonChiTietMoi.setTongTrongLuong(chiTietOpt.getTrongLuong() * soLuongMua);
                    hoaDonChiTietMoi.setNgayTao(new Date());
                    hoaDonChiTietMoi.setGhiChu("Thêm mới sản phẩm với giá khác");

                    hoaDonChiTietOnlRepository.save(hoaDonChiTietMoi);

                    if (isHoaDonDaThanhToan) {
                        chiTietOpt.setSoLuong(chiTietOpt.getSoLuong() - soLuongMua);
                        chiTietSanPhamRepository.save(chiTietOpt);
                    }
                    return true;
                }
            } else {
                // Thêm mới nếu sản phẩm chưa có trong hóa đơn
                if (chiTietOpt.getSoLuong() < soLuongMua) {
                    return false;
                }
                hoaDonChiTiet.setGhiChu("Thêm mới sản phẩm " + chiTietOpt.getIdSanPham().getTenSanPham());
                hoaDonChiTiet.setDonGia(chiTietOpt.getGiaBan());
                hoaDonChiTiet.setTongTrongLuong(chiTietOpt.getTrongLuong() * soLuongMua);
                hoaDonChiTietOnlRepository.save(hoaDonChiTiet);

                if (isHoaDonDaThanhToan) {
                    chiTietOpt.setSoLuong(chiTietOpt.getSoLuong() - soLuongMua);
                    chiTietOpt.setNgaySua(new Date());
                    chiTietSanPhamRepository.save(chiTietOpt);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public void xoaSPCTVaoHDCT(int idHoaDon, int idChiTietSanPham) {
        hoaDonChiTietOnlRepository.xoaChiTietSanPhamTrongHoaDon(idHoaDon, idChiTietSanPham);
    }

    @Override
    public List<Map<String, Object>> getTop10BestSellingProducts() {
        List<Object[]> results = hoaDonChiTietOnlRepository.findTopBestSellingProducts(PageRequest.of(0, 10));
        List<Map<String, Object>> bestSellingProducts = new ArrayList<>();

        for (Object[] row : results) {
            ChiTietSanPham chiTietSanPham = (ChiTietSanPham) row[0];
            Long totalSold = (Long) row[1]; // Lấy số lượng đã bán

            Map<String, Object> productData = new HashMap<>();
            productData.put("id", chiTietSanPham.getId());
            productData.put("tenSanPham", chiTietSanPham.getIdSanPham().getTenSanPham());
            productData.put("size", chiTietSanPham.getIdSize().getTenSize());
            productData.put("mauSac", chiTietSanPham.getIdMauSac().getTenMauSac());
            productData.put("giaBan", chiTietSanPham.getGiaBan());
            productData.put("soLuongDaBan", totalSold); // Hiển thị số lượng đã bán
            productData.put("hinhAnh", chiTietSanPham.getHinhAnh()); // Lấy hình ảnh
            bestSellingProducts.add(productData);
        }
        return bestSellingProducts;
    }

    @Override
    public HoaDonChiTiet findByIdHoaDonAndIdChiTietSanPham(int idHoaDon, int idChiTietSanPham) {
        return hoaDonChiTietOnlRepository.findByIdHoaDonAndIdChiTietSanPham(idHoaDon, idChiTietSanPham);
    }

    @Override
    @Transactional
    public void xacNhanHoaDon(int hoaDonId) {
//        HoaDon hoaDon = hoaDonOnlRepository.findById(hoaDonId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
//
//        if (hoaDon.getTrangThai() == 6) {
//            throw new RuntimeException("Hóa đơn này đã được xác nhận trước đó.");
//        }
//        if(hoaDon.getIdKhuyenMai()!=null){
//            KhuyenMai khuyenMai = hoaDon.getIdKhuyenMai();
//            if(khuyenMai!=null){
//                khuyenMai.setDaSuDung(khuyenMai.getDaSuDung()+1);
//                khuyenMaiService.updateKhuyenMai(khuyenMai,khuyenMai.getId());
//                return;
//            }
//        }
//        if(hoaDon.getTrangThai()==4){
//            // Lấy danh sách chi tiết hóa đơn
//            List<HoaDonChiTiet> chiTietList = hoaDonChiTietOnlRepository.findHoaDonChiTietByHoaDonId(hoaDonId);
//
//            for (HoaDonChiTiet chiTiet : chiTietList) {
//                ChiTietSanPham sanPham = chiTietSanPhamRepository.findById(chiTiet.getIdChiTietSanPham().getId());
//                if (sanPham == null) {
//                    throw new RuntimeException("Không tìm thấy sản phẩm");
//                }
//
//                if (sanPham.getSoLuong() < chiTiet.getSoLuong()) {
//                    throw new RuntimeException("Không đủ số lượng trong kho cho sản phẩm: " + sanPham.getIdSanPham().getTenSanPham());
//                }
//                // Trừ số lượng sản phẩm
//                sanPham.setSoLuong(sanPham.getSoLuong() - chiTiet.getSoLuong());
//                chiTietSanPhamRepository.save(sanPham);
//            }
//            hoaDonOnlRepository.save(hoaDon);
//
//        }

    }

    @Override
    public List<HoaDonChiTiet> findHoaDonChiTietByHoaDonIdAndIdChiTietSanPham(int idHoaDon, int idChiTietSanPham) {
        return hoaDonChiTietOnlRepository.findByHoaDonAndSanPham(idHoaDon, idChiTietSanPham);
    }


}

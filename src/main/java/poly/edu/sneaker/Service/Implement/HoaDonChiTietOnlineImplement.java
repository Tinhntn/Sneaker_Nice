package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Repository.HoaDonChiTietOnlRepository;
import poly.edu.sneaker.Service.HoaDonChiTietOnlService;
import poly.edu.sneaker.Service.HoaDonOnlService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HoaDonChiTietOnlineImplement implements HoaDonChiTietOnlService {

    @Autowired
    HoaDonOnlService hoaDonOnlService;

    @Autowired
    HoaDonChiTietOnlRepository hoaDonChiTietOnlRepository;

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

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

        ChiTietSanPham sanPham = chiTietSanPhamRepository.findById(idChiTietSanPham)
              ;

        int soLuongTruoc = chiTiet.getSoLuong();
        int chenhLech = soLuongMoi - soLuongTruoc;

        // Kiểm tra số lượng sản phẩm có đủ không
        if (sanPham.getSoLuong() < chenhLech) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ!");
        }

        // Cập nhật số lượng trong chi tiết hóa đơn
        chiTiet.setSoLuong(soLuongMoi);
        hoaDonChiTietOnlRepository.save(chiTiet);

        // Cập nhật số lượng sản phẩm chi tiết
        sanPham.setSoLuong(sanPham.getSoLuong() - chenhLech);
        chiTietSanPhamRepository.save(sanPham);
    }

    @Override
    public boolean themSPCTVaoHDCT(HoaDonChiTiet hoaDonChiTiet) {
        try {
            // Kiểm tra hóa đơn có tồn tại không
            Optional<HoaDon> hoaDonOpt = hoaDonOnlService.findHoaDonById(hoaDonChiTiet.getIdHoaDon().getId());
            if (!hoaDonOpt.isPresent()) return false;

            // Kiểm tra sản phẩm chi tiết có tồn tại không
            ChiTietSanPham chiTietOpt = chiTietSanPhamRepository.findById(hoaDonChiTiet.getIdChiTietSanPham().getId());

            ChiTietSanPham chiTietSanPham = chiTietOpt;
            int soLuongMua = hoaDonChiTiet.getSoLuong();

            // Kiểm tra nếu sản phẩm đã có trong hóa đơn chi tiết
            Optional<HoaDonChiTiet> existingDetailOpt = hoaDonChiTietOnlRepository.findByHoaDonAndSanPham(
                    hoaDonChiTiet.getIdHoaDon().getId(), hoaDonChiTiet.getIdChiTietSanPham().getId()
            );

            if (existingDetailOpt.isPresent()) {
                // Nếu sản phẩm đã có, cộng dồn số lượng
                HoaDonChiTiet existingDetail = existingDetailOpt.get();
                int tongSoLuongMoi = existingDetail.getSoLuong() + soLuongMua;

                // Kiểm tra số lượng kho có đủ không
                if (chiTietSanPham.getSoLuong() < soLuongMua) {
                    return false; // Không đủ hàng
                }

                existingDetail.setSoLuong(tongSoLuongMoi);
                existingDetail.setNgaySua(new Date());
                hoaDonChiTietOnlRepository.save(existingDetail);

            } else {
                // Nếu chưa có, thêm mới sản phẩm vào hóa đơn chi tiết
                if (chiTietSanPham.getSoLuong() < soLuongMua) {
                    return false; // Không đủ hàng
                }

                hoaDonChiTiet.setIdHoaDon(hoaDonOpt.get());
                hoaDonChiTiet.setIdChiTietSanPham(chiTietSanPham);
                hoaDonChiTiet.setNgayTao(new Date());
                hoaDonChiTiet.setNgaySua(new Date());
                hoaDonChiTietOnlRepository.save(hoaDonChiTiet);
            }

            // Trừ số lượng sản phẩm trong kho
            chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() - soLuongMua);
            chiTietSanPhamRepository.save(chiTietSanPham);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void xoaSPCTVaoHDCT(int idHoaDon, int idChiTietSanPham) {
        hoaDonChiTietOnlRepository.xoaChiTietSanPhamTrongHoaDon(idHoaDon, idChiTietSanPham);
    }

//    @Override
//    public HoaDonChiTiet findHoaDonChiTietByIdHoaDon(int idHoaDon) {
//        return hoaDonChiTietOnlRepository.findHoaDonChiTietByIdHoaDon(idHoaDon);
//    }

}

package poly.edu.sneaker.Service;

import org.springframework.data.repository.query.Param;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;

import java.util.List;
import java.util.Map;

public interface HoaDonChiTietOnlService {

    List<HoaDonChiTietOnlCustom> findByHoaDonId(HoaDon idhoadon);

    List<HoaDonChiTiet> findHoaDonChiTietByHoaDonId(@Param("idHoaDon") Integer idHoaDon);

    void updateSoLuong(int idChiTietHoaDon, int idChiTietSanPham, int soLuongMoi);

    boolean themSPCTVaoHDCT(HoaDonChiTiet hoaDonChiTiet);

    void xoaSPCTVaoHDCT(int idHoaDon, int idChiTietSanPham);

    List<Map<String, Object>> getTop10BestSellingProducts();

    HoaDonChiTiet findByIdHoaDonAndIdChiTietSanPham(@Param("idHoaDon") int idHoaDon, @Param("idChiTietSanPham") int idChiTietSanPham);

    void xacNhanHoaDon(int hoaDonId);
//    HoaDonChiTiet findHoaDonChiTietByIdHoaDon(int idHoaDon);
    List<HoaDonChiTiet> findHoaDonChiTietByHoaDonIdAndIdChiTietSanPham(int idHoaDon,int idChiTietSanPham);

}

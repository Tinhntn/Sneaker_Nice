package poly.edu.sneaker.Service;

import org.springframework.data.repository.query.Param;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;

import java.util.List;

public interface HoaDonChiTietOnlService {

    List<HoaDonChiTietOnlCustom> findByHoaDonId(HoaDon idhoadon);

    List<HoaDonChiTiet> findHoaDonChiTietByHoaDonId(@Param("idHoaDon") Integer idHoaDon);

    void updateSoLuong(int idChiTietHoaDon, int idChiTietSanPham, int soLuongMoi);

    boolean themSPCTVaoHDCT(HoaDonChiTiet hoaDonChiTiet);

    void xoaSPCTVaoHDCT(int idHoaDon, int idChiTietSanPham);

//    HoaDonChiTiet findHoaDonChiTietByIdHoaDon(int idHoaDon);


}

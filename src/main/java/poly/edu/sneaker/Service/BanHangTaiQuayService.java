package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import poly.edu.sneaker.Model.*;

import java.util.List;

public interface BanHangTaiQuayService {
    List<HoaDon> getAllHoaDon();
    HoaDon saveHoaDon(HoaDon hoaDon);
    HoaDon taoHoaDonCho(HoaDon hoaDon);
    Page<ChiTietSanPham> DanhSachSanPhamPhanTrang(int page, int size);
    List<HoaDonChiTiet> danhSachChiTietHoaDonByIDHD(Integer id);
    List<HoaDonChiTiet> danhSachChiTietHoaDonByID(Integer id);
    HoaDonChiTiet ChiTietHoaDonByID(Integer id);
    HoaDonChiTiet saveHDCT(HoaDonChiTiet hoaDonChiTiet);
    HoaDon getHoaDonByID(Integer id);
    Double tongTienCTHD(Integer id);
    ChiTietSanPham danhSachChiTietSPID(Integer id);
    ChiTietSanPham saveCTSP(ChiTietSanPham chiTietSanPham);
    HoaDonChiTiet addHoaDonCT(HoaDonChiTiet hoaDonChiTiet);
    List<HoaDonChiTiet> ktratrunghdct(Integer idChiTietSanPham,Integer idHoaDon);
    Void xoaHD(Integer id);
    Void xoaHDCT(Integer id);
    KhachHang timIDQuaSDTKH(String sdt);
    KhuyenMai timKhuyenMaiQuaMa(String maKM);
    List<ChatLieu> getAllChatLieuTimKiem();
    List<DanhMuc> getAllDanhMucTimKiem();
    List<Hang> getAllHangTimKiem();
    List<MauSac> getAllMauSacTimKiem();
    List<Size> getAllSizeTimKiem();
    Page<ChiTietSanPham> timKiemSanPham(int page, int size, String tenSanPham,
                                        Integer idSize, Integer idMauSac, Integer idDanhMuc, Integer idHang, Integer idChatLieu);
}

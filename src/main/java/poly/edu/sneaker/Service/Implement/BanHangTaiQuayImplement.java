package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Repository.*;
import poly.edu.sneaker.Service.BanHangTaiQuayService;
import poly.edu.sneaker.Service.HoaDonService;

import java.util.List;
@Service
public class BanHangTaiQuayImplement implements BanHangTaiQuayService {
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;
    @Autowired
    private ChatLieuRepository chatLieuRepository;
    @Autowired
    private DanhMucRepository danhMucRepository;
    @Autowired
    private HangRepository hangRepository;
    @Autowired
    private MauSacRepository mauSacRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Override
    public List<HoaDon> getAllHoaDon() {
        return hoaDonRepository.getAllHoaDon();
    }

    @Override
    public HoaDon saveHoaDon(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public HoaDon taoHoaDonCho(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public Page<ChiTietSanPham> DanhSachSanPhamPhanTrang(int page, int size) {
        return  chiTietSanPhamRepository.getAllChiTieSanPhamDAO(PageRequest.of(page,size));
    }

    @Override
    public List<HoaDonChiTiet> danhSachChiTietHoaDonByIDHD(Integer id) {
        return hoaDonChiTietRepository.findHoaDonChiTietByIdHoaDon(id);
    }

    @Override
    public List<HoaDonChiTiet> danhSachChiTietHoaDonByID(Integer id) {
        return hoaDonChiTietRepository.findHoaDonChiTietById(id);
    }

    @Override
    public HoaDonChiTiet ChiTietHoaDonByID(Integer id) {
        return hoaDonChiTietRepository.HoaDonChiTietById(id);
    }

    @Override
    public HoaDonChiTiet saveHDCT(HoaDonChiTiet hoaDonChiTiet) {
        return hoaDonChiTietRepository.save(hoaDonChiTiet);
    }

    @Override
    public HoaDon getHoaDonByID(Integer id) {
        return hoaDonRepository.findByHDId(id);
    }

    @Override
    public Double tongTienCTHD(Integer id) {
        return hoaDonChiTietRepository.tongTienCTHD(id);
    }

    @Override
    public ChiTietSanPham danhSachChiTietSPID(Integer id) {

        return chiTietSanPhamRepository.getChiTietSanPhamById(id);
    }

    @Override
    public ChiTietSanPham saveCTSP(ChiTietSanPham chiTietSanPham) {
        return chiTietSanPhamRepository.save(chiTietSanPham);
    }

    @Override
    public HoaDonChiTiet addHoaDonCT(HoaDonChiTiet hoaDonChiTiet) {
        return hoaDonChiTietRepository.save(hoaDonChiTiet);
    }

    @Override
    public List<HoaDonChiTiet> ktratrunghdct(Integer idChiTietSanPham, Integer idHoaDon) {
        return hoaDonChiTietRepository.ktraAddctsplencthd(idChiTietSanPham,idHoaDon);
    }

    @Override
    public Void xoaHD(Integer id) {
        hoaDonRepository.deleteById(id);
        return null;
    }

    @Override
    public Void xoaHDCT(Integer id) {
        hoaDonChiTietRepository.deleteById(id);
        return null;
    }

    @Override
    public KhachHang timIDQuaSDTKH(String sdt) {
        return khachHangRepository.TimKhachHangQuaSDT(sdt);
    }

    @Override
    public KhuyenMai timKhuyenMaiQuaMa(String maKM) {
        return khuyenMaiRepository.TimKhuyenMaiQuaMa(maKM);
    }

    @Override
    public List<ChatLieu> getAllChatLieuTimKiem() {
        return chatLieuRepository.getAllChatLieuTimKiem();
    }
    @Override
    public List<DanhMuc> getAllDanhMucTimKiem() {
        return danhMucRepository.getAllDanhMucTimKiem();
    }
    @Override
    public List<Hang> getAllHangTimKiem() {
        return hangRepository.getAllHangTimKiem();
    }
    @Override
    public List<MauSac> getAllMauSacTimKiem() {
        return mauSacRepository.getAllMauSacTimKiem();
    }
    @Override
    public List<Size> getAllSizeTimKiem() {
        return sizeRepository.getAllSizeTimKiem();
    }

    @Override
    public Page<ChiTietSanPham> timKiemSanPham(int page, int size, String tenSanPham, Integer idSize, Integer idMauSac, Integer idDanhMuc, Integer idHang, Integer idChatLieu) {
        Pageable pageable = PageRequest.of(page, size);
        return chiTietSanPhamRepository.timKiemSanPhamQuaCTSP(
                tenSanPham != null ? tenSanPham : "",
                idSize != null ? idSize : 0,
                idMauSac != null ? idMauSac : 0,
                idDanhMuc != null ? idDanhMuc : 0,
                idHang != null ? idHang : 0,
                idChatLieu != null ? idChatLieu : 0,
                pageable
        );
    }
}

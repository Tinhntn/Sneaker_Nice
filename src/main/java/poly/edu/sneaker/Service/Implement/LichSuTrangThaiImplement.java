package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Repository.HoaDonChiTietRepository;
import poly.edu.sneaker.Repository.KhuyenMaiRepository;
import poly.edu.sneaker.Repository.LichSuTrangThaiRepository;
import poly.edu.sneaker.Service.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service

public class LichSuTrangThaiImplement implements LichSuTrnngThaiService {
    @Autowired
    private LichSuTrangThaiRepository lichSuTrangThaiRepository;
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @Override
    public Page<TrangThaiDonHang> findAllLichSuTrangThai(Pageable pageable) {
        return lichSuTrangThaiRepository.findAll(pageable);
    }

    @Override
    public void saveLichSuTrangThai(TrangThaiDonHang lt) {
        lichSuTrangThaiRepository.save(lt);
    }

    @Override
    public void deleteLichSuTrangThai(TrangThaiDonHang lt) {
        lichSuTrangThaiRepository.delete(lt);

    }

    @Override
    public TrangThaiDonHang findLichSuTrangThaiById(Integer id) {
        return lichSuTrangThaiRepository.findById(id).get();
    }

    @Override
    public void updateLichSuTrangThai(TrangThaiDonHang lt) {
        lichSuTrangThaiRepository.save(lt);
    }

    @Override
    public List<TrangThaiDonHang> getAllByIdHoaDon(int idHD) {
        return lichSuTrangThaiRepository.findAllByIdHoaDon_Id(idHD);
    }

    @Override
    public boolean doiTrangThaiDonHang(int idHD, String ghiChu, int trangThai) {

        HoaDon hoaDon = hoaDonService.findById(idHD);
        if (trangThai == 4) {
            List<HoaDonChiTiet> lstHoaDonChiTiet = hoaDonChiTietRepository.findHoaDonChiTietByIdHoaDon_IdAndIdHoaDon_LoaiHoaDon(idHD,true);
            for (HoaDonChiTiet hoaDonChiTiet : lstHoaDonChiTiet) {
                ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(hoaDonChiTiet.getIdChiTietSanPham().getId());
                chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong()-hoaDonChiTiet.getSoLuong());
                chiTietSanPhamRepository.save(chiTietSanPham);
            }
        }
        if (trangThai == 6 || trangThai == 11) {
            if (hoaDon.getIdKhuyenMai() != null) {
                KhuyenMai khuyenMai = hoaDon.getIdKhuyenMai();
                if (khuyenMai != null) {
                    khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() + 1);
                    khuyenMai.setNgaySua(new Date());
                    khuyenMaiRepository.save(khuyenMai);

                }
            }
        }
        if(trangThai==11){
            List<HoaDonChiTiet> lstHoaDonChiTiet = hoaDonChiTietRepository.findHoaDonChiTietByIdHoaDon_IdAndIdHoaDon_LoaiHoaDon(idHD,true);
            for (HoaDonChiTiet hoaDonChiTiet : lstHoaDonChiTiet) {
                ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(hoaDonChiTiet.getIdChiTietSanPham().getId());
                chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong()+hoaDonChiTiet.getSoLuong());
                chiTietSanPhamRepository.save(chiTietSanPham);
            }
        }
        TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
        lichSuTrangThai.setIdHoaDon(hoaDon);
        lichSuTrangThai.setTrangThai(trangThai);
        lichSuTrangThai.setNgayCapNhat(new Date());
        lichSuTrangThai.setGhiChu(ghiChu);
        lichSuTrangThaiRepository.save(lichSuTrangThai);
        hoaDon.setTrangThai(trangThai);
        hoaDonService.save(hoaDon);

        return true;
    }
}

package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.*;
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
    HoaDonChiTietOnlService hoaDonChiTietOnlService;
    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    KhuyenMaiService khuyenMaiService;
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
        hoaDon.setTrangThai(trangThai);
        hoaDonService.save(hoaDon);

        TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
        lichSuTrangThai.setIdHoaDon(hoaDon);
        lichSuTrangThai.setTrangThai(trangThai);
        lichSuTrangThai.setNgayCapNhat(new Date());
        lichSuTrangThai.setGhiChu(ghiChu);
        lichSuTrangThaiRepository.save(lichSuTrangThai);
        return true;
    }
}

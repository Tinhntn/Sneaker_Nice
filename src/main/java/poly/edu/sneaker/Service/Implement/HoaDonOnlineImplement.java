package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Repository.HoaDonOnlRepository;
import poly.edu.sneaker.Service.HoaDonOnlService;

import java.util.Date;
import java.util.Optional;

@Service
public class HoaDonOnlineImplement implements HoaDonOnlService {

    @Autowired
    HoaDonOnlRepository hoaDonOnlRepository;

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomTatCa(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomTatCa(pageable);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomDH(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomDH(pageable);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonOLChoxacnhan(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomCXN(pageable);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonOLCholayhang(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomCLH(pageable);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomDG(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomDG(pageable);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomHT(Pageable pageable) {
        return hoaDonOnlRepository.getHoaDonCustomHT(pageable);
    }

    @Override
    public HoaDon detailHD(int id) {
        return hoaDonOnlRepository.getAllHoaDonByid(id);
    }

    @Override
    public void updateHoaDon(HoaDon hd, int id) {
        hoaDonOnlRepository.save(hd);
    }

    @Override
    public Optional<HoaDon> findHoaDonById(int id) {
        return Optional.ofNullable(hoaDonOnlRepository.getAllHoaDonByid(id));
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomDHKH(Pageable pageable, Integer idKhachHang) {
        return hoaDonOnlRepository.getHoaDonCustomDHKH(pageable, idKhachHang);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonOLChoxacnhanKH(Pageable pageable, Integer idKhachHang) {
        return hoaDonOnlRepository.getHoaDonCustomCXNKH(pageable, idKhachHang);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonOLCholayhangKH(Pageable pageable, Integer idKhachHang) {
        return hoaDonOnlRepository.getHoaDonCustomCLHKH(pageable, idKhachHang);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomDGKH(Pageable pageable, Integer idKhachHang) {
        return hoaDonOnlRepository.getHoaDonCustomDGKH(pageable, idKhachHang);
    }

    @Override
    public Page<HoaDonOnlCustom> getHoaDonCustomHTKH(Pageable pageable, Integer idKhachHang) {
        return hoaDonOnlRepository.getHoaDonCustomHTKH(pageable, idKhachHang);
    }

    @Override
    public Page<HoaDon> searchHoaDon(Integer idHoaDon, String tenKhachHang, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        return hoaDonOnlRepository.searchHoaDon(idHoaDon, tenKhachHang, pageable);
    }

    @Override
    public Page<HoaDon> filterHoaDonByDate(Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        return hoaDonOnlRepository.filterHoaDonByDate(startDate, endDate, pageable);
    }


}

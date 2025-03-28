package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.HoaDon;

import java.util.Date;
import java.util.Optional;


public interface HoaDonOnlService {

    Page<HoaDonOnlCustom> getHoaDonCustomTatCa(Pageable pageable);
    Page<HoaDonOnlCustom> getHoaDonCustomDH(Pageable pageable);
    Page<HoaDonOnlCustom> getHoaDonOLChoxacnhan(Pageable pageable);
    Page<HoaDonOnlCustom> getHoaDonOLCholayhang(Pageable pageable);
    Page<HoaDonOnlCustom> getHoaDonCustomDG(Pageable pageable);
    Page<HoaDonOnlCustom> getHoaDonCustomHT(Pageable pageable);

    HoaDon detailHD(int id);
    void updateHoaDon(HoaDon hd, int id);

    Optional<HoaDon> findHoaDonById(int id);


    //Hóa đơn khách hàng
    Page<HoaDonOnlCustom> getHoaDonCustomDHKH(Pageable pageable, Integer idKhachHang);
    Page<HoaDonOnlCustom> getHoaDonOLChoxacnhanKH(Pageable pageable, Integer idKhachHang);
    Page<HoaDonOnlCustom> getHoaDonOLCholayhangKH(Pageable pageable, Integer idKhachHang);
    Page<HoaDonOnlCustom> getHoaDonCustomDGKH(Pageable pageable, Integer idKhachHang);
    Page<HoaDonOnlCustom> getHoaDonCustomHTKH(Pageable pageable, Integer idKhachHang);
    //End hóa đơn khách hàng

    // tìm kiếm hóa đơn
    Page<HoaDon> searchHoaDon(Integer idHoaDon, String tenKhachHang, int page, int size);

    Page<HoaDon> filterHoaDonByDate(Date startDate, Date endDate, int page, int size);
    // end tìm kiếm hóa đơn


}

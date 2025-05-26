package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;

import java.util.List;

public interface HoaDonService {
    Page<HoaDon> findAll(Pageable pageable);
    HoaDon findById(int id);
    HoaDon save(HoaDon hoaDon);
    void update(HoaDon hoaDon);
    void deleteById(int id);
    Page<HoaDon> getAllHoaDonTaiQuay(int page, int size);
    List<HoaDonChiTiet> danhSachChiTietHoaDonByIDHD(Integer id);
    String taoMaHoaDon();

    Page<HoaDon> timkiemhoadon(String keyword, int page, int size);

    Page<HoaDon> searchHoaDonByDateRange(String startDate, String endDate, int page,int size);
    Page<HoaDon> timHoaDonTheoIdKhuyenMai(Integer idkm, int page,int size);

    void  tinhLaiKhuyenMai(HoaDon hoaDon);
}

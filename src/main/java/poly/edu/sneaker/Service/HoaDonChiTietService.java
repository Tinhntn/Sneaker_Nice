package poly.edu.sneaker.Service;

import poly.edu.sneaker.Model.HoaDonChiTiet;

import java.util.List;

public interface HoaDonChiTietService {
    void saveHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet);
    HoaDonChiTiet findHoaDonChiTietByID(int id);
    boolean deleteHoaDonChiTiet(int id);
}
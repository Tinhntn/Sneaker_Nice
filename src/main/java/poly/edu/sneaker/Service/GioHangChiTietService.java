package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.GioHangChiTiet;

import java.util.ArrayList;

public interface GioHangChiTietService {
    Page<GioHangChiTiet> findAll(Pageable pageable);
    GioHangChiTiet findById(int id);
    GioHangChiTiet saveGioHangChitiet(GioHangChiTiet gioHangChiTiet);
    void deleteGioHangChitiet(int id);
    void update(GioHangChiTiet gioHangChiTiet);
    ArrayList<GioHangChiTiet> findByIdGioHang(int idGioHang);
    GioHangChiTiet findByIdGioHangAndIDCTSP(int idGioHang, int idCTSP);

}

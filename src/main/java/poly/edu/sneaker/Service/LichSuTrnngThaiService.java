package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.TrangThaiDonHang;

import java.util.List;

public interface LichSuTrnngThaiService {
    Page<TrangThaiDonHang> findAllLichSuTrangThai(Pageable pageable);
    void saveLichSuTrangThai(TrangThaiDonHang lt);
    void deleteLichSuTrangThai(TrangThaiDonHang lt);
    TrangThaiDonHang findLichSuTrangThaiById(Integer id);
    void updateLichSuTrangThai(TrangThaiDonHang lt);
    List<TrangThaiDonHang> getAllByIdHoaDon(int idHD);
    boolean doiTrangThaiDonHang(int idHD, String ghiChu,int trangThai, boolean isUndo);
    TrangThaiDonHang layTrangThaiTruocDo(int idHoaDon,int trangThaiTruoc);

    List<TrangThaiDonHang> findByIdHoaDon_IdOrderByNgayCapNhatDesc(int idHoaDon);
}

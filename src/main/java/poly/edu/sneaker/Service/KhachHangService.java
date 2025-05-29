package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.KhachHang;

import java.util.List;

public interface KhachHangService {
    Page<KhachHang> getAll(Pageable pageable);

    KhachHang findKhachHangById(int id);
    List<KhachHang> findAll();
    void saveKhachHang(KhachHang khachHang);

    void updateKhachHang(KhachHang khachHang, int id);

    void updateKhachHangHung(KhachHang khachHang, int id);

    void updateKhachHangHoaDonOnl(KhachHang khachHang, int id);

    void deleteById(int id);

    Page<KhachHang> search(String keyword, Pageable pageable);

    KhachHang findByEmail(String email);
    KhachHang findByEmailAndMatKhau(String Email,String matKhau);

    boolean exitsKhachHangByEmail(String email);
    String taoMaKhachHang();
    boolean layLaiKhachHang(KhachHang khachHang);
    KhachHang findByMaKhachHang(String maKhachHang);
    Page<KhachHang> filterAndSort(Boolean trangThai, String sortBy, String sortDir, Pageable pageable);
    boolean guiMailDonHang(KhachHang khachHang, HoaDon hoaDon, String ghiChu);
    boolean ThongBao( HoaDon hoaDon,int trangThai, String ghiChu);

}
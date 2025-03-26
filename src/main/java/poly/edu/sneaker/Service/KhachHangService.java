package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.KhachHang;

public interface KhachHangService {
    Page<KhachHang> getAll(Pageable pageable);

    KhachHang findKhachHangById(int id);

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
}
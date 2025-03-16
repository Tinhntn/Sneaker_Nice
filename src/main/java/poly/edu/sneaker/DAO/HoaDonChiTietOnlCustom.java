package poly.edu.sneaker.DAO;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface HoaDonChiTietOnlCustom {

    int getId();
    int getIdHoaDon();

    // Sửa lỗi: Trả về ID thay vì đối tượng ChiTietSanPham
    @Value("#{target.idChiTietSanPham.id}")
    int getIdChiTietSanPham();

    int getSoLuong();
    float getTongTrongLuong();
    float getDonGia();
    String getGhiChu();
    Date getNgayTao();
    Date getNgaySua();
    Boolean getTrangThai();

}

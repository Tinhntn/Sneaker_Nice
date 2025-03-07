package poly.edu.sneaker.DAO;

import java.util.Date;

public interface HoaDonOnlCustom {

    int getId();
    int getIdNhanVien();
    int getIdKhachHang();
    int getIdKhuyenMai();
    String getMaHoaDon();
    float getThanhTien();
    String getGhiChu();
    Date getNgayTao();
    Date getNgaySua();
    float getTienKhachDua();
    float getTongTien();
    float getTienThua();
    float getTongTienGiam();
    Date getNgayGiaoHang();
    String getDonViGiaoHang();
    float getPhiShip();
    String getTenNguoiGiaoHang();
    String getEmailNguoiNhan();
    String getDiaChiChiTiet();
    String getTinhThanhPho();
    String getQuanHuyen();
    String getPhuongXa();
    Boolean getLoaiHoaDon();
    Boolean getLoaiThanhToan();
    String getTenNguoiGiao();
    String getSdtNguoiGiao();
    Boolean getTrangThai();
}



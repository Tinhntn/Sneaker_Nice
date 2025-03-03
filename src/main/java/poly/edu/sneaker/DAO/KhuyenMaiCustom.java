package poly.edu.sneaker.DAO;

import java.util.Date;

public interface KhuyenMaiCustom {

    int getId();
    String getMaKhuyenMai();
    String getTenKhuyenMai();
    String getMoTa();
    float getDieuKienApDung();
    Boolean getLoaiKhuyenMai();
    float getGiaTriGiam();
    float getMucGiamGiaToiDa();
    int getDaSuDung();
    int getSoLuong();
    Date getNgayBatDau();
    Date getNgayKetThuc();
    Date getNgayTao();
    Date getNgaySua();
    Boolean getTrangThai();

}

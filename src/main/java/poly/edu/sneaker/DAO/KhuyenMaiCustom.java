package poly.edu.sneaker.DAO;

import java.util.Date;

public interface KhuyenMaiCustom {

    Integer getId();
    String getMaKhuyenMai();
    String getTenKhuyenMai();
    String getMoTa();
    float getDieuKienApDung();
    Boolean getLoaiKhuyenMai();
    float getGiaTriGiam();
    float getMucGiamGiaToiDa();
    Integer getDaSuDung();
    Integer getSoLuong();
    Date getNgayBatDau();
    Date getNgayKetThuc();
    Date getNgayTao();
    Date getNgaySua();
    Boolean getTrangThai();

}

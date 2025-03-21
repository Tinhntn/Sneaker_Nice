package poly.edu.sneaker.DAO;

import java.util.Map;

public class ThongKeDTO {
    private Double doanhThu;
    private Integer soSanPham;
    private Integer soHoaDonThanhCong;
    private Integer soHoaDonHuy;
    private Map<String, Integer> hd; // Thêm thuộc tính chi tiết theo trạng thái

    public ThongKeDTO() {
    }

    public ThongKeDTO(Double doanhThu, Integer soSanPham, Integer soHoaDonThanhCong, Integer soHoaDonHuy) {
        this.doanhThu = doanhThu;
        this.soSanPham = soSanPham;
        this.soHoaDonThanhCong = soHoaDonThanhCong;
        this.soHoaDonHuy = soHoaDonHuy;
    }

    // Constructor mới có thêm hd
    public ThongKeDTO(Double doanhThu, Integer soSanPham, Integer soHoaDonThanhCong, Integer soHoaDonHuy, Map<String, Integer> hd) {
        this.doanhThu = doanhThu;
        this.soSanPham = soSanPham;
        this.soHoaDonThanhCong = soHoaDonThanhCong;
        this.soHoaDonHuy = soHoaDonHuy;
        this.hd = hd;
    }

    public Double getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(Double doanhThu) {
        this.doanhThu = doanhThu;
    }

    public Integer getSoSanPham() {
        return soSanPham;
    }

    public void setSoSanPham(Integer soSanPham) {
        this.soSanPham = soSanPham;
    }

    public Integer getSoHoaDonThanhCong() {
        return soHoaDonThanhCong;
    }

    public void setSoHoaDonThanhCong(Integer soHoaDonThanhCong) {
        this.soHoaDonThanhCong = soHoaDonThanhCong;
    }

    public Integer getSoHoaDonHuy() {
        return soHoaDonHuy;
    }

    public void setSoHoaDonHuy(Integer soHoaDonHuy) {
        this.soHoaDonHuy = soHoaDonHuy;
    }

    public Map<String, Integer> getHd() {
        return hd;
    }

    public void setHd(Map<String, Integer> hd) {
        this.hd = hd;
    }
}

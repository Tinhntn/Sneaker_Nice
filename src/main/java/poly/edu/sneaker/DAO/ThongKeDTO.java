package poly.edu.sneaker.DAO;

import java.util.Map;

public class ThongKeDTO {
    private Double doanhThu;             // Tổng doanh thu (có thể tính bằng doanhThuOnline + doanhThuOffline)
    private Double doanhThuOnline;       // Doanh thu từ đơn online
    private Double doanhThuOffline;      // Doanh thu từ đơn offline
    private Integer soSanPham;
    private Integer soHoaDonThanhCong;
    private Integer soHoaDonHuy;
    private Map<String, Integer> hd;      // Chi tiết trạng thái đơn hàng

    public ThongKeDTO() {
    }

    // Constructor cơ bản (chỉ tổng doanh thu)
    public ThongKeDTO(Double doanhThu, Integer soSanPham, Integer soHoaDonThanhCong, Integer soHoaDonHuy) {
        this.doanhThu = doanhThu;
        this.soSanPham = soSanPham;
        this.soHoaDonThanhCong = soHoaDonThanhCong;
        this.soHoaDonHuy = soHoaDonHuy;
    }

    // Constructor có thêm hd (chi tiết trạng thái)
    public ThongKeDTO(Double doanhThu, Integer soSanPham, Integer soHoaDonThanhCong, Integer soHoaDonHuy, Map<String, Integer> hd) {
        this.doanhThu = doanhThu;
        this.soSanPham = soSanPham;
        this.soHoaDonThanhCong = soHoaDonThanhCong;
        this.soHoaDonHuy = soHoaDonHuy;
        this.hd = hd;
    }

    // Constructor mở rộng cho doanh thu online và offline
    public ThongKeDTO(Double doanhThuOnline, Double doanhThuOffline, Double doanhThu,
                      Integer soSanPham, Integer soHoaDonThanhCong, Integer soHoaDonHuy,
                      Map<String, Integer> hd) {
        this.doanhThuOnline = doanhThuOnline;
        this.doanhThuOffline = doanhThuOffline;
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

    public Double getDoanhThuOnline() {
        return doanhThuOnline;
    }

    public void setDoanhThuOnline(Double doanhThuOnline) {
        this.doanhThuOnline = doanhThuOnline;
    }

    public Double getDoanhThuOffline() {
        return doanhThuOffline;
    }

    public void setDoanhThuOffline(Double doanhThuOffline) {
        this.doanhThuOffline = doanhThuOffline;
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

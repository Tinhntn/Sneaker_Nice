package poly.edu.sneaker.DAO;

public class ThongKeDTO {
    private Double doanhThu;
    private Integer soSanPham;
    private Integer soHoaDonThanhCong;
    private Integer soHoaDonHuy;

    public ThongKeDTO() {
    }


    public ThongKeDTO(Double doanhThu, Integer soSanPham, Integer soHoaDonThanhCong, Integer soHoaDonHuy) {
        this.doanhThu = doanhThu;
        this.soSanPham = soSanPham;
        this.soHoaDonThanhCong = soHoaDonThanhCong;
        this.soHoaDonHuy = soHoaDonHuy;
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
}

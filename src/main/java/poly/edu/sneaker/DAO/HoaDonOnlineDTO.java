package poly.edu.sneaker.DAO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HoaDonOnlineDTO {

    private int id;
    private String tenNhanVien;
    private String tenKhachHang;
    private String tenKhuyenMai;
    private String maHoaDon;
    private double tongTien;
    private double tongTienGiam;
    private double thanhTien;
    private double tienKhachDua;
    private double tienThua;
    private double phiShip;
    private Date ngayGiaoHang;
    private String donViGiaoHang;
    private String tenNguoiGiao;
    private String sdtNguoiGiao;
    private String emailNguoiNhan;
    private String diaChiChiTiet;
    private String tinhThanhPho;
    private String quanHuyen;
    private String phuongXa;
    private Boolean loaiThanhToan;
    private Integer trangThai;
    private Date ngayTao;
    private Date ngaySua;

    public HoaDonOnlineDTO(int id, String tenNhanVien, String tenKhachHang, String tenKhuyenMai, String maHoaDon,
                              double tongTien, double tongTienGiam, double thanhTien, double tienKhachDua,
                              double tienThua, double phiShip, Date ngayGiaoHang, String donViGiaoHang,
                              String tenNguoiGiao, String sdtNguoiGiao, String emailNguoiNhan, String diaChiChiTiet,
                              String tinhThanhPho, String quanHuyen, String phuongXa, Boolean loaiThanhToan,
                              Integer trangThai, Date ngayTao, Date ngaySua) {
        this.id = id;
        this.tenNhanVien = tenNhanVien;
        this.tenKhachHang = tenKhachHang;
        this.tenKhuyenMai = tenKhuyenMai;
        this.maHoaDon = maHoaDon;
        this.tongTien = tongTien;
        this.tongTienGiam = tongTienGiam;
        this.thanhTien = thanhTien;
        this.tienKhachDua = tienKhachDua;
        this.tienThua = tienThua;
        this.phiShip = phiShip;
        this.ngayGiaoHang = ngayGiaoHang;
        this.donViGiaoHang = donViGiaoHang;
        this.tenNguoiGiao = tenNguoiGiao;
        this.sdtNguoiGiao = sdtNguoiGiao;
        this.emailNguoiNhan = emailNguoiNhan;
        this.diaChiChiTiet = diaChiChiTiet;
        this.tinhThanhPho = tinhThanhPho;
        this.quanHuyen = quanHuyen;
        this.phuongXa = phuongXa;
        this.loaiThanhToan = loaiThanhToan;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.ngaySua = ngaySua;
    }

}

package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "hoadon")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "idNhanVien")
    private NhanVien idNhanVien;
    @ManyToOne
    @JoinColumn(name = "idKhachHang")
    private KhachHang idKhachHang;
    @ManyToOne
    @JoinColumn(name = "idKhuyenMai")
    private KhuyenMai idKhuyenMai;
    private String maHoaDon;
    private float thanhTien;
    private String ghiChu;
    private Date ngayTao;
    private Date ngaySua;
    private float tienKhachDua;
    private float tongTien;
    private float tienThua;
    private float tongTienGiam;
    private Date ngayGiaoHang;
    private String donViGiaoHang;
    private float phiShip;
    private String emailNguoiNhan;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiChiTiet;
    private String tinhThanhPho;
    private String quanHuyen;
    private String phuongXa;
    private Boolean loaiHoaDon;
    private Boolean loaiThanhToan;
    private String tenNguoiGiao;
    private String sdtNguoiGiao;
    private int trangThai;

}

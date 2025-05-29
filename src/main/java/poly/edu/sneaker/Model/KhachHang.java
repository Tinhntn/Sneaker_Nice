package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Table(name = "khachhang")
@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ma_khach_hang", unique = true, nullable = false)
    private String maKhachHang;

    @Column(name = "ten_khach_hang", nullable = false)
    private String tenKhachHang;

    @Column(name = "tinh_thanh_pho")
    private String tinhThanhPho;

    @Column(name = "quan_huyen")
    private String quanHuyen;

    @Column(name = "phuong_xa")
    private String phuongXa;

    @Column(name = "gioi_tinh", columnDefinition = "boolean default true")
    private Boolean gioiTinh;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "sdt")
    private String sdt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngaySua = new Date();

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;
}   
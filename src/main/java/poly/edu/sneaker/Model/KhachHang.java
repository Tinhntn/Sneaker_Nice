package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "khachhang")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ma_khach_hang", unique = true, nullable = false)
    @NotBlank(message = "Mã khách hàng không được để trống")
    private String maKhachHang;

    @Column(name = "ten_khach_hang", nullable = false)
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String tenKhachHang;

    @Column(name = "tinh_thanh_pho")
    private String tinhThanhPho;

    @Column(name = "quan_huyen")
    private String quanHuyen;

    @Column(name = "phuong_xa")
    private String phuongXa;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Email(message = "Email không hợp lệ")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số và không chứa chữ cái")
    @Column(name = "sdt")
    private String sdt;

    @Column(name = "ngay_sinh")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;

    @Column(name = "ngay_tao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao = new Date();

    @Column(name = "ngay_sua", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngaySua = new Date();

    @Column(name = "mat_khau", nullable = false)
    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(regexp = "^(?=.*[A-Z]).{6,}$", message = "Mật khẩu phải có ít nhất 6 ký tự và chứa ít nhất một chữ cái in hoa")
    private String matKhau;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;

    @Column(name = "deleted_at", nullable = false)
    private Boolean deletedAt = false;

//    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<DiaChi> diaChiList;
//
//    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<HoaDon> hoaDonList;
//
//    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<GioHang> gioHangList;
}
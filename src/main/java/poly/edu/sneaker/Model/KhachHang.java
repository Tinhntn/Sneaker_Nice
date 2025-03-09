package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số và không chứa chữ cái")
    @Column(name = "sdt")
    private String sdt;

    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_sinh")
    private Date ngaySinh;

    @Column(name = "ngay_tao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao = new Date();

    @Column(name = "ngay_sua", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngaySua = new Date();

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(regexp = "^(?=.*[A-Z]).{6,}$", message = "Mật khẩu phải có ít nhất 6 ký tự và chứa ít nhất một chữ cái in hoa")
    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;
}
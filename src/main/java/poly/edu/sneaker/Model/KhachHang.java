package poly.edu.sneaker.Model;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Mã KH không được để trống")
    @Size(min = 2, max = 50, message = "Mã KH phải từ 2-50 ký tự")
    private String maKhachHang;

    @Column(name = "ten_khach_hang", nullable = false)
    @NotBlank(message = "Tên KH không được để trống")
    @Size(min = 2, max = 100, message = "Tên KH phải từ 2-100 ký tự")
    private String tenKhachHang;

    @Column(name = "tinh_thanh_pho")
    private String tinhThanhPho;

    @Column(name = "quan_huyen")
    private String quanHuyen;

    @Column(name = "phuong_xa")
    private String phuongXa;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "email", unique = true)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Column(name = "sdt")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "\\d+", message = "SĐT chỉ chứa chữ số")
    @Size(min = 10, max = 15, message = "SĐT phải từ 10-15 ký tự số")
    private String sdt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Ngày sinh không được lớn hơn hiện tại")
    private Date ngaySinh;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngaySua = new Date();

    @Column(name = "mat_khau", nullable = false)
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
    private String matKhau;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;
}
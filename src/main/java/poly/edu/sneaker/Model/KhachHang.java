package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "khachhang")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String ma_khach_hang;
    private String ten_khach_hang;
    private String tinh_thanh_pho;
    private String quan_huyen;
    private String phuong_xa;
    private Boolean gioi_tinh;
    private String email;
    private String sdt;
    private Date ngay_sinh;
    private Date ngay_tao;
    private Date ngay_sua;
    private String mat_khau;
    private String hinh_anh;
    private Boolean trang_thai;

}

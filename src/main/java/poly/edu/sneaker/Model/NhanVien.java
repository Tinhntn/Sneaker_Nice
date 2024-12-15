package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "nhanvien")
@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  int id_chua_vu;
    private String ma_nhan_vien;
    private  String ho_va_ten;
    private Boolean gioi_tinh;
    private  Date ngay_sinh;
    private  String dia_chi;
    private  String sdt;
    private String email;
    private String mat_khau;
    private String hinh_anh;
    private  Date ngay_tao;
    private Date ngay_sua;
    private Boolean trang_thai;

}

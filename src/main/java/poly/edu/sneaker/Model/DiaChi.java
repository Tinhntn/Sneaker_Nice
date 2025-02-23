package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "diachi")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaChi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int id_khach_hang;
    private String tinh_thanh_pho;
    private String quan_huyen;
    private String phuong_xa;
    private String dia_chi_cu_the;
    private String sdt;
    private Date ngay_tao;
    private Date ngay_sua;
    private Boolean trang_thai;

}

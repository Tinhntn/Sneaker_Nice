package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table (name = "giohang")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GioHang {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    private int id_khach_hang;
    private int id_khuyen_mai;
    private String ma_gio_hang;
    private Date ngay_tao;
    private Date ngay_sua;
    private Boolean trang_thai;
    private String ghi_chu;
}

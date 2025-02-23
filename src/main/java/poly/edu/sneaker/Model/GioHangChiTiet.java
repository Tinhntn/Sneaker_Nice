package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "giohangchitiet")
@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GioHangChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int id_gio_hang;
    private int id_chi_tiet_san_pham;
    private int so_luong;
    private int tong_trong_luong;
    private float don_gia;
    private float tong_tien;
    private Date ngay_tao;
    private Date ngay_sua;
    private Boolean trang_thai;
}

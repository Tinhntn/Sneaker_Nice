package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "chitietsanpham")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPham {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    private int id_san_pham;
    private int id_size;
    private int id_mau_sac;
    private float trong_luong;
    private float gia_nhap;
    private float gia_ban;
    private String hinh_anh;
    private int so_luong;
    private String mo_ta;
    private Date ngay_tao;
    private Date ngay_sua;
    private Boolean trang_thai;
}

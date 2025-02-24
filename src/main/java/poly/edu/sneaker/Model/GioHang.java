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
    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang idKhachHang;
    @ManyToOne
    @JoinColumn(name = "id_khuyen_mai")
    private KhuyenMai idKhuyenMai;
    private String maGioHang;
    private Date ngayTao;
    private Date ngaySua;
    private Boolean trangThai;
    private String ghiChu;
}

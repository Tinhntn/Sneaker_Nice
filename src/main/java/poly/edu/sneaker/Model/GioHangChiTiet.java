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
    @ManyToOne
    @JoinColumn(name = "idGioHang")
    private GioHang idGioHang;
    @ManyToOne
    @JoinColumn(name = "idChiTietSanPham")
    private ChiTietSanPham idChiTietSanPham;
    private int soLuong;
    private int tongTrongLuong;
    private float donGia;
    private float tongTien;
    private Date ngayTao;
    private Date ngaySua;
    private Boolean trangThai;
}

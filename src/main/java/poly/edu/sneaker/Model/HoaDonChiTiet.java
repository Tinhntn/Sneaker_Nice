package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "hoadonchitiet")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "idHoaDon")
    private HoaDon idHoaDon;

    @ManyToOne
    @JoinColumn(name = "idChiTietSanPham")
    private ChiTietSanPham idChiTietSanPham;

    private int soLuong;
    private float tongTrongLuong;
    private float donGia;
    private String ghiChu;
    private Date ngayTao;
    private Date ngaySua;
    private Integer trangThai;
}

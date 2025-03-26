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
public class    ChiTietSanPham {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name="id_san_pham")
    private SanPham idSanPham;


    @ManyToOne
    @JoinColumn(name = "id_size")
    private Size idSize;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac")
    private MauSac idMauSac;
    private float trongLuong;
    private float giaNhap;
    private float giaBan;
    private String hinhAnh;
    private int soLuong;
    private String moTa;
    private Date ngayTao;
    private Date ngaySua;
    private Boolean trangThai;
}

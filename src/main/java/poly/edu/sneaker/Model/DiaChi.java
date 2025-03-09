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
    @ManyToOne
    @JoinColumn(name = "idKhachHang")
    private KhachHang idKhachHang;
    private String tinhThanhPho;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiCuThe;
    private String sdt;
    private Date ngayTao;
    private Date ngaySua;
    private Boolean trangThai;

}

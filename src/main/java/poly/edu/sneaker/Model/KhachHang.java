package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "khachhang")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String maKhachHang;
    private String tenKhachHang;
    private String tinhThanhPho;
    private String quanHuyen;
    private String phuongXa;
    private Boolean gioiTinh;
    private String email;
    private String sdt;
    private Date ngaySinh;
    private Date ngayTao;
    private Date ngaySua;
    private String matKhau;
    private String hinhAnh;
    private Boolean trangThai;

}

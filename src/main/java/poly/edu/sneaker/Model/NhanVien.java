package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "nhanvien")
@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "idChucVu")
    private  ChucVu idChucVu;
    private String maNhanVien;
    private  String hoVaTen;
    private Boolean gioiTinh;
    private  Date ngaySinh;
    private  String diaChi;
    private  String sdt;
    private String email;
    private String matKhau;
    private String hinhAnh;
    private  Date ngayTao;
    private Date ngaySua;
    private Boolean trangThai;

}

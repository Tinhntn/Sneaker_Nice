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
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_chuc_vu")
    private  ChucVu idChucVu;
    @Column(name = "ma_nhan_vien")
    private String maNhanVien;
    @Column(name = "ho_va_ten")
    private  String hoVaTen;
    @Column(name = "gioi_tinh ")
    private Boolean gioiTinh;
    @Column(name = "ngay_sinh")
    private  Date ngaySinh;
    @Column(name = "dia_chi")
    private  String diaChi;
    @Column(name = "sdt")
    private  String sdt;
    @Column(name = "email")
    private String email;

    @Column(name = "mat_khau")
    private String matKhau;
    @Column(name = "hinh_anh")
    private String hinhAnh;
    @Column(name = "ngay_tao")
    private  Date ngayTao;
    @Column(name = "ngay_sua")
    private Date ngaySua;
    @Column(name = "trang_thai")
    private Boolean trangThai;

}

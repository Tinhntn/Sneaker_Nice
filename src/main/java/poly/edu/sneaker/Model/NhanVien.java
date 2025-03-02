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

    @JoinColumn(name = "id_chuc_vu")
    private  ChucVu idChucVu;
    @JoinColumn(name = "ma_nhan_vien")
    private String maNhanVien;
    @JoinColumn(name = "ho_va_ten")
    private  String hoVaTen;
    @JoinColumn(name = "gioi_tinh ")
    private Boolean gioiTinh;
    @JoinColumn(name = "ngay_sinh")
    private  Date ngaySinh;
    @JoinColumn(name = "dia_chi")
    private  String diaChi;
    @JoinColumn(name = "sdt")
    private  String sdt;
    @JoinColumn(name = "email")
    private String email;

    @JoinColumn(name = "mat_khau")
    private String matKhau;
    @JoinColumn(name = "hinh_anh")
    private String hinhAnh;
    @JoinColumn(name = "ngay_tao")
    private  Date ngayTao;
    @JoinColumn(name = "ngay_sua")
    private Date ngaySua;
    @JoinColumn(name = "trang_thai")
    private Boolean trangThai;

}

package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "hoadon")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoaDon {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    private int id_nhan_vien;
    private int id_khach_hang;
    private int id_khuyen_mai;
    private String ma_hoa_don;
    private float thanh_tien;
    private String ghi_chu;
    private Date ngay_tao;
    private Date ngay_sua;
    private float tien_khach_dua;
    private float tong_tien;
    private float tien_thua;
    private float tong_tien_giam;
    private Date ngay_giao_hang;
    private String don_vi_giao_hang;
    private float phi_ship;
    private String ten_nguoi_giao_hang;
    private String email_nguoi_nhan;
    private String dia_chi_chi_tiet;
    private String tinh_thanh_pho;
    private String quan_huyen;
    private String phuong_xa;
    private Boolean loai_hoa_don;
    private Boolean loai_thanh_toan;
    private String ten_nguoi_giao;
    private String sdt_nguoi_giao;
    private Boolean trang_thai;


}

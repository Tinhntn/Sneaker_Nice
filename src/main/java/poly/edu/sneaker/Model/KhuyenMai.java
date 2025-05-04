package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Table(name = "khuyenmai")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ma_khuyen_mai")
    private String maKhuyenMai;
    @Column(name = "ten_khuyen_mai")
    private String tenKhuyenMai;
    @Column(name = "mo_ta")
    private String moTa;
    @Column(name = "dieu_kien_ap_dung")
    private float dieuKienApDung;
    @Column(name = "loai_khuyen_mai")
    private Boolean loaiKhuyenMai;
    @Column(name = "gia_tri_giam")
    private float giaTriGiam;
    @Column(name = "muc_giam_gia_toi_da")
    private float mucGiamGiaToiDa;
    @Column(name = "da_su_dung")
    private int daSuDung;
    @Column(name = "so_luong")
    private int soLuong;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_bat_dau")
    private Date ngayBatDau;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_ket_thuc")
    private Date ngayKetThuc;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_tao")
    private Date ngayTao;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_sua")
    private Date ngaySua;
    @Column(name = "trang_thai")
    private Boolean trangThai;
}

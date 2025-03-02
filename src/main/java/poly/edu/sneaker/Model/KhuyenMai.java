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

    @JoinColumn(name = "ma_khuyen_mai")
    private String maKhuyenMai;
    @JoinColumn(name = "ten_khuyen_mai")
    private String tenKhuyenMai;
    @JoinColumn(name = "mo_ta")
    private String moTa;
    @JoinColumn(name = "dieu_kien_ap_dung")
    private float dieuKienApDung;
    @JoinColumn(name = "loai_khuyen_mai")
    private Boolean loaiKhuyenMai;
    @JoinColumn(name = "gia_tri_giam")
    private float giaTriGiam;
    @JoinColumn(name = "muc_giam_toi_da")
    private float mucGiamToiDa;
    @JoinColumn(name = "da_su_dung")
    private int daSuDung;
    @JoinColumn(name = "so_luong")
    private int soLuong;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JoinColumn(name = "ngay_bat_dau")
    private Date ngayBatDau;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JoinColumn(name = "ngay_ket_thuc")
    private Date ngayKetThuc;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JoinColumn(name = "ngay_tao")
    private Date ngayTao;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JoinColumn(name = "ngay_sua")
    private Date ngaySua;
    @JoinColumn(name = "trang_thai")
    private Boolean trangThai;
}

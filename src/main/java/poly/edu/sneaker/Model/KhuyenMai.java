package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

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
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private String moTa;
    private float dieuKienApDung;
    private Boolean loaiKhuyenMai;
    private float giaTriGiam;
    private float mucGiamToiDa;
    private int daSuDung;
    private int soLuong;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private Date ngayTao;
    private Date ngaySua;
    private Boolean trangThai;
}

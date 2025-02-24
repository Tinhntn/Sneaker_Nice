package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "sanpham")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private String maSanPham;
    private String tenSanPham;
    @ManyToOne
    @JoinColumn(name = "idHang")
    private Hang idHang;

    @ManyToOne
    @JoinColumn(name = "idDanhMuc")
    private DanhMuc idDanhMuc;

    @ManyToOne
    @JoinColumn(name = "idChatLieu")
    private ChatLieu idChatLieu;

    private Date ngayTao;
    private  Date ngaySua;
    private Boolean trangThai;
}

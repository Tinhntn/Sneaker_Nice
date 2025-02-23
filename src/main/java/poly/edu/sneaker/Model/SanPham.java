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
    private String ma_san_pham;
    private String ten_san_pham;
    @ManyToOne
    @JoinColumn(name = "id_hang")
    private Hang id_hang;

    @ManyToOne
    @JoinColumn(name = "id_danh_muc")
    private DanhMuc id_danh_muc;

    @ManyToOne
    @JoinColumn(name = "id_chat_lieu")
    private ChatLieu id_chat_lieu;

    private Date ngay_tao;
    private  Date ngay_sua;
    private Boolean trang_thai;
}

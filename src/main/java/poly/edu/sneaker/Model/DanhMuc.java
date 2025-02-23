package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "danhmuc")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String ma_danh_muc;
    private String ten_danh_muc;
    private Date ngay_tao;
    private Date ngay_sua;
    private Boolean trang_thai;
}

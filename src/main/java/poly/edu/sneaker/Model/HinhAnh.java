package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "hinhanh")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HinhAnh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    private String ma_hinh_anh;
    private String url1;
    private String url2;
    private String url3;
    private String url4;
    private String url5;
    private Date ngay_tao;
    private Date ngay_sua;
    private Boolean trang_thai;

}

package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "size")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    private  String ma_size;
    private  String ten_size;
    private  Date ngay_tao;
    private   Date ngay_sua;
    private   Boolean trang_thai;
}

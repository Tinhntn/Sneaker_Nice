package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "chucvu")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChucVu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private String ma_chuc_vu;
    private String ten_chuc_vu;

}

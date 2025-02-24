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
    private  String maSize;
    private  String tenSize;
    private  Date ngayTao;
    private   Date ngaySua;
    private   Boolean trangThai;
}

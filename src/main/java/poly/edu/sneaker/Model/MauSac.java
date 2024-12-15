package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "mausac")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private   int id;
    private  String ma_mau_sac;
    private  String ten_mau_sac;
    private   Date ngay_tao;
    private   Date ngay_sua;
    private   Boolean trang_thai;
}

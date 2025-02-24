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
    private  String maMauSac;
    private  String tenMauSac;
    private   Date ngayTao;
    private   Date ngaySua;
    private   Boolean trangThai;
}

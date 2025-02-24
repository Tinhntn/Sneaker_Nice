package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "lichsutrangthai")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class TrangThaiDonHang {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "idHoaDon")
    private HoaDon idHoaDon;
    private Date thoiGian;
    private Byte trangThai;
    private String ghiChu;
}

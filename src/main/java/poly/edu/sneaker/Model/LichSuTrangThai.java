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

public class LichSuTrangThai {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    private int id_hoa_don;
    private Date thoi_gian;
    private Byte trang_thai;
    private String ghi_chu;
}

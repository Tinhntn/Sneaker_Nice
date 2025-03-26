package poly.edu.sneaker.Model;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "chucvu")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChucVu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "ma_chuc_vu", unique = true, nullable = false)
    private String maChucVu;

    @Column(name = "ten_chuc_vu", nullable = false)

    private String tenChucVu;
}
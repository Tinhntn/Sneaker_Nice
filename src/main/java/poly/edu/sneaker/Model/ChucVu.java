package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    private int id;
    @Column(name = "ma_chuc_vu", unique = true, nullable = false)
    @NotBlank(message = "Mã chức vụ không được để trống")
    private String maChucVu;

    @Column(name = "ten_chuc_vu", nullable = false)
    @NotBlank(message = "Tên chức vụ không được để trống")
    private String tenChucVu;
}
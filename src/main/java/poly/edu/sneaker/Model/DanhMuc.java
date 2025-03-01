package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Table(name = "danhmuc")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ma_danh_muc", unique = true, nullable = false)
    @NotBlank(message = "Mã danh mục không được để trống")
    private String maDanhMuc;

    @Column(name = "ten_danh_muc", nullable = false)
    @NotBlank(message = "Tên danh mục không được để trống")
    private String tenDanhMuc;

    @Column(name = "ngay_tao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao = new Date();

    @Column(name = "ngay_sua", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngaySua = new Date();

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;

    @Column(name = "deleted_at", nullable = false)
    private Boolean deletedAt = false;
}
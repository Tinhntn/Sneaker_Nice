package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "chatlieu")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ma_chat_lieu", unique = true, nullable = false)
    private String maChatLieu;

    @Column(name = "ten_chat_lieu", nullable = false)
    private String tenChatLieu;

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
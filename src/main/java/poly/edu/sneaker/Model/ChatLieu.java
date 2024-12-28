package poly.edu.sneaker.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table (name = "chatlieu")
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatLieu {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private  int id;
    private  String ma_chat_lieu;
    private  String ten_chat_lieu;
    private  Date ngay_tao;
    private  Date ngay_sua;
    private  Boolean trang_thai;
}

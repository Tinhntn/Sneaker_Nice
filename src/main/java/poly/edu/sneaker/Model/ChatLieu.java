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
    private  String maChatLieu;
    private  String tenChatLieu;
    private  Date ngayTao;
    private  Date ngaySua;
    private  Boolean trangThai;
}

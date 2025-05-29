package poly.edu.sneaker.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class GiaoHangTietKiemResponse {
    private boolean success;
    private String message;
    private int fee;
    private int estimatedDeliveryTime; // giờ
    private String errorCode;
}
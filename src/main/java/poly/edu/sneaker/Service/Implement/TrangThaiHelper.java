package poly.edu.sneaker.Service.Implement;

public class TrangThaiHelper {
    public static String getStateName(int state) {
        return switch (state) {
            case 0 -> "Chưa thanh toán";
            case 1 -> "Đã thanh toán";
            case 2 -> "Chờ xác nhận";
            case 3 -> "Chờ lấy hàng";
            case 4 -> "Đang giao";
            case 5 -> "Đã giao";
            case 6 -> "Đã hủy";
            case 10 -> "Đã thanh toán - chờ xác nhận";
            case 11 -> "Giao thất bại";
            default -> "Trạng thái " + state;
        };
    }
}

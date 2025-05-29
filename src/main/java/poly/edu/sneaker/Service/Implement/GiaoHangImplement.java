package poly.edu.sneaker.Service.Implement;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import poly.edu.sneaker.Response.GiaoHangTietKiemResponse;
import poly.edu.sneaker.Service.GiaoHangService;

import java.util.List;

@Service
public class GiaoHangImplement implements GiaoHangService {
    private static final String GHTK_TOKEN = "1UPNLDzc2NoNopuL9Fgfp5nX0ffqL2m3Y0PHMrY";
    private static final String API_URL = "https://services.giaohangtietkiem.vn/services/shipment/fee";

    @Override
    public GiaoHangTietKiemResponse getGiaoHangTietKiem(String pickProvince, String pickDistrict, String province, String district, int weight) {
        // Chuẩn hóa tên tỉnh/thành phố
        String normalizedFromProvince = normalizeProvince(pickProvince);
        String normalizedToProvince = normalizeProvince(province);

        // Xác định loại vận chuyển
        String shippingType = determineShippingType(normalizedFromProvince, normalizedToProvince);

        // Tính phí theo loại
        int fee = calculateFeeByType(shippingType, weight);
        int deliveryTime = estimateDeliveryTime(shippingType);

        return new GiaoHangTietKiemResponse(true, "Thành công", fee, deliveryTime, null);
    }


    private String normalizeProvince(String province) {
        // Chuẩn hóa tên tỉnh/thành phố
        return province.replaceAll("\\s+", "").toLowerCase();
    }

    private String determineShippingType(String fromProvince, String toProvince) {
        // Nội thành Hà Nội hoặc TP.HCM
        if (fromProvince.equals("hanoi") && toProvince.equals("hanoi")) {
            return "noi_thanh_ha_noi";
        }
        if (fromProvince.equals("tphcm") && toProvince.equals("tphcm")) {
            return "noi_thanh_tphcm";
        }

        // Nội miền
        if ((fromProvince.equals("hanoi") && isNorthernProvince(toProvince))) {
            return "noi_mien_bac";
        }
        if ((fromProvince.equals("tphcm") && isSouthernProvince(toProvince))) {
            return "noi_mien_nam";
        }

        // Liên miền
        return "lien_mien";
    }
    // Danh sách các tỉnh miền Bắc

    private boolean isNorthernProvince(String province) {
        List<String> northernProvinces = List.of(
                "hanoi",
                "haiduong",
                "haiphong",
                "hanam",
                "hungyen",
                "bacninh",
                "bacgiang",
                "backan",
                "caobang",
                "hagiang",
                "laocai",
                "tuyenquang",
                "yenbai",
                "phutho",
                "thainguyen",
                "langson",
                "quangninh",
                "dienbien",
                "laichau",
                "sonla",
                "hoabinh",
                "vinhphuc",
                "thaibinh",
                "namdinh",
                "ninhbinh"
        );
        return northernProvinces.contains(province);
    }
    //Danh sách các tỉnh miền trung
    private boolean centralProvinces(String province){
        List<String> centralProvinces = List.of(
                "thanhhoa",     // Thanh Hóa
                "nghean",       // Nghệ An
                "hatinh",       // Hà Tĩnh
                "quangbinh",    // Quảng Bình
                "quangtri",     // Quảng Trị
                "thuathienhue", // Thừa Thiên Huế
                "danang",       // Đà Nẵng
                "quangnam",     // Quảng Nam
                "quangngai",    // Quảng Ngãi
                "binhdinh",     // Bình Định
                "phuyen",       // Phú Yên
                "khanhhoa",     // Khánh Hòa
                "ninhthuan",    // Ninh Thuận
                "binhthuan",    // Bình Thuận
                "kontum",       // Kon Tum
                "gialai",       // Gia Lai
                "daklak",       // Đắk Lắk
                "daknong",      // Đắk Nông
                "lamdong"       // Lâm Đồng
        );
        return centralProvinces.contains(province);
    }
    // Danh sách các tỉnh miền Nam

    private boolean isSouthernProvince(String province) {
         List<String> southernProvinces = List.of(
                "tphcm",        // TP.Hồ Chí Minh
                "dongnai",      // Đồng Nai
                "binhduong",    // Bình Dương
                "baria-vungtau",// Bà Rịa - Vũng Tàu
                "tayninh",      // Tây Ninh
                "binhphuoc",    // Bình Phước
                "longan",       // Long An
                "tiengiang",    // Tiền Giang
                "bentre",       // Bến Tre
                "travinh",      // Trà Vinh
                "vinhlong",     // Vĩnh Long
                "dongthap",     // Đồng Tháp
                "angiang",      // An Giang
                "kiengiang",    // Kiên Giang
                "cantho",       // Cần Thơ
                "haugiang",     // Hậu Giang
                "socang",       // Sóc Trăng
                "baclieu",      // Bạc Liêu
                "camau",        // Cà Mau
                "phuquoc"       // Phú Quốc (Kiên Giang)
        );
        return southernProvinces.contains(province);
    }

    private int calculateFeeByType(String shippingType, int weight) {
        // Tính phí theo bảng giá
        int baseFee = 0;
        int additionalFee = 0;

        switch (shippingType) {
            case "noi_thanh_ha_noi":
            case "noi_thanh_tphcm":
                baseFee = 22000;
                additionalFee = (int) Math.ceil((weight - 3000) / 500.0) * 3000;
                break;
            case "noi_mien_bac":
            case "noi_mien_nam":
                baseFee = 30000;
                additionalFee = (int) Math.ceil((weight - 500) / 500.0) * 35000;
                break;

            case "lien_mien":
                baseFee = 40000;
                additionalFee = (int) Math.ceil((weight - 500) / 500.0) * 50000;
                break;

            default:
                baseFee = 40000;
                additionalFee = (int) Math.ceil((weight - 500) / 500.0) * 50000;
        }

        return baseFee + additionalFee;
    }

    private int estimateDeliveryTime(String shippingType) {
        // Ước lượng thời gian giao hàng
        switch (shippingType) {
            case "noi_thanh_ha_noi":
            case "noi_thanh_tphcm":
                return 12; // giờ

            case "noi_mien_bac":
            case "noi_mien_nam":
                return 24; // giờ

            case "lien_mien":
                return 72; // giờ

            default:
                return 48; // giờ
        }
    }
}

package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.edu.sneaker.DAO.SanPhamBanChayResponse;
import poly.edu.sneaker.Service.ThongKeService;

import java.util.List;
import java.util.Map;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Controller
@RequestMapping("/thongke")
public class ThongKeController {

    @Autowired
    private ThongKeService thongKeService;

    @GetMapping("/hienthi")
    public String viewThongKe(Model model) {
        // Lấy dữ liệu mặc định
        Pageable pageable = PageRequest.of(0, 5);
        Map<String, Object> defaultStats = thongKeService.getDefaultThongKe();
        model.addAllAttributes(defaultStats);

        // Lấy dữ liệu thống kê theo tháng
        Map<String, Object> monthlyStats = thongKeService.getThongKeTheoLoai(2); // 2 - Thống kê theo tháng
        model.addAttribute("monthlyStats", monthlyStats);

        // Lấy danh sách top 5 sản phẩm bán chạy trong ngày hôm nay
        Date todayStart = new Date(); // Lấy ngày hiện tại
        Date todayEnd = new Date();   // Ngày hiện tại
        List<SanPhamBanChayResponse> top5 = thongKeService.getTop5SanPhamBanChay(todayStart, todayEnd, null,pageable);
        model.addAttribute("lstSanPhamBanChay", top5);

        return "admin/thongke/viewThongKe";
    }
    // Lọc theo loại (0 - ngày, 1 - tuần, 2 - tháng, 3 - năm)
    @PostMapping("/loc")
    @ResponseBody
    public Map<String, Object> locTheoThoiGian(@RequestParam("bien") int loaiLoc) {
        if (loaiLoc < 0 || loaiLoc > 3) {
            return Map.of("error", "Giá trị loại lọc không hợp lệ!"); // Validate giá trị loaiLoc
        }
        return thongKeService.getThongKeTheoLoai(loaiLoc);
    }

    // Lọc theo khoảng ngày
    @PostMapping("/khoangngay")
    @ResponseBody
    public Map<String, Object> locTheoKhoangNgay(@RequestParam("startDate") String startDate,
                                                 @RequestParam("endDate") String endDate) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra nếu startDate hoặc endDate bị trống
        if (startDate == null || startDate.trim().isEmpty() || endDate == null || endDate.trim().isEmpty()) {
            response.put("error", "Ngày bắt đầu và ngày kết thúc không được để trống!");
            return response;
        }

        try {
            // Chuyển đổi startDate và endDate từ String sang Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            // Lấy ngày hiện tại
            Date today = new Date();

            // Kiểm tra nếu endDate là ngày trong tương lai
            if (end.after(today)) {
                response.put("error", "Ngày kết thúc không được là ngày trong tương lai!");
                return response;
            }

            // Kiểm tra nếu khoảng cách giữa startDate và endDate vượt quá 180 ngày
            long diffInMillies = Math.abs(end.getTime() - start.getTime());
            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24); // Chuyển đổi từ milliseconds sang ngày
            if (diffInDays > 180) {
                response.put("error", "Khoảng thời gian không được vượt quá 180 ngày!");
                return response;
            }

            // Nếu tất cả điều kiện hợp lệ, gọi service để lấy dữ liệu thống kê
            return thongKeService.getThongKeTheoKhoangNgay(startDate, endDate);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Lỗi phân tích ngày: " + e.getMessage());
            return response;
        }
    }
}

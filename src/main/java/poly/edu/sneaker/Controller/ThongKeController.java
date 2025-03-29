package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.edu.sneaker.DAO.SanPhamBanChayResponse;
import poly.edu.sneaker.Service.ThongKeService;




import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/thongke")
public class ThongKeController {

    @Autowired
    private ThongKeService thongKeService;
    @GetMapping("/hienthi")
    public String viewThongKe(Model model) {
        Map<String, Object> stats = thongKeService.getDefaultThongKe();
        model.addAllAttributes(stats);
        // Nếu muốn lấy luôn danh sách top sản phẩm:
        List<SanPhamBanChayResponse> top5 = thongKeService.getTop5SanPhamBanChay();

        model.addAttribute("lstSanPhamBanChay", top5);
        return "admin/thongke/viewThongKe";
    }



    // Lọc theo loại (0 - ngày, 1 - tuần, 2 - tháng, 3 - năm)
    @PostMapping("/loc")
    @ResponseBody
    public Map<String, Object> locTheoThoiGian(@RequestParam("bien") int loaiLoc) {
        return thongKeService.getThongKeTheoLoai(loaiLoc);
    }

    // Lọc theo khoảng ngày
    @PostMapping("/khoangngay")
    @ResponseBody
    public Map<String, Object> locTheoKhoangNgay(@RequestParam("startDate") String startDate,
                                                 @RequestParam("endDate") String endDate) {
        return thongKeService.getThongKeTheoKhoangNgay(startDate, endDate);
    }
}

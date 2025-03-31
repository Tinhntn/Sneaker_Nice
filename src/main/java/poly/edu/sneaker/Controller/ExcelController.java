package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.edu.sneaker.DAO.ThongKeDTO;
import poly.edu.sneaker.Service.ThongKeService;
import poly.edu.sneaker.Utils.ExcelExporter;

import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ExcelController {

    @Autowired
    private ThongKeService thongKeService;

    @GetMapping("/thongke/export")
    public void exportToExcel(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "loaiLoc", required = false) Integer loaiLoc,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String currentDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String headerValue = "attachment; filename=thongke_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        Map<String, Object> stats;

        // Nếu có startDate và endDate => lọc theo khoảng ngày
        if (startDate != null && !startDate.trim().isEmpty() &&
                endDate != null && !endDate.trim().isEmpty()) {
            stats = thongKeService.getThongKeTheoKhoangNgay(startDate, endDate);
        }
        // Nếu có loaiLoc (0: ngày, 1: tuần, 2: tháng, 3: năm)
        else if (loaiLoc != null) {
            stats = thongKeService.getThongKeTheoLoai(loaiLoc);
        }
        // Ngược lại, sử dụng dữ liệu mặc định (ví dụ: "Hôm nay")
        else {
            stats = thongKeService.getDefaultThongKe();
        }

        ThongKeDTO tk;
        if (stats.containsKey("doanhThuTrongNgay")) {
            // Khi dùng getDefaultThongKe() – dữ liệu được đặt tên theo "Hôm nay"
            Double doanhThu = (Double) stats.get("doanhThuTrongNgay");
            Integer soSanPham = ((Number) stats.get("soLuongSanPhamTrongNgay")).intValue();
            Integer soHoaDonThanhCong = ((Number) stats.get("soLuongHoaDonThanhCongTrongNgay")).intValue();
            Integer soHoaDonHuy = ((Number) stats.get("soluongHoaDonHuyTrongNgay")).intValue();
            tk = new ThongKeDTO(doanhThu, soSanPham, soHoaDonThanhCong, soHoaDonHuy);
        } else {
            // Khi dùng bộ lọc theo ngày/tuần/tháng/năm hoặc theo khoảng ngày
            Double doanhThu = (Double) stats.get("doanhThu");
            Integer soSanPham = ((Number) stats.get("soLuongSanPham")).intValue();
            Integer soHoaDonThanhCong = ((Number) stats.get("soLuongHoaDonThanhCong")).intValue();
            Integer soHoaDonHuy = ((Number) stats.get("soluongHoaDonHuy")).intValue();
            tk = new ThongKeDTO(doanhThu, soSanPham, soHoaDonThanhCong, soHoaDonHuy);
            // Nếu service có trả về map chi tiết trạng thái (hd) thì có thể set:
            tk.setHd((Map<String, Integer>) stats.get("hd"));
        }

        List<ThongKeDTO> listThongKe = new ArrayList<>();
        listThongKe.add(tk);

        ExcelExporter exporter = new ExcelExporter(listThongKe);
        exporter.export(response);
    }
}

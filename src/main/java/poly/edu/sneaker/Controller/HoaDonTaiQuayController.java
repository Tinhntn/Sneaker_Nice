package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.BanHangTaiQuayService;
import poly.edu.sneaker.Service.HoaDonService;

import java.util.List;

@Controller
@RequestMapping("/hoadontaiquay")
public class HoaDonTaiQuayController {
    @Autowired
    HoaDonService hoaDonService ;
    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page) {
        int size = 5;
        //list chitietsanpham phan trang
        Page<HoaDon> hd = hoaDonService.getAllHoaDonTaiQuay(page,size);
        model.addAttribute("hd", hd.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", hd.getTotalPages());
        return "admin/hoa-don/hoadontaiquay";
    }

    @GetMapping("/hienthichitiet/{id}")
    public String chitiet(@PathVariable("id") Integer id, Model model) {
      HoaDon hd =   hoaDonService.findById(id);
        model.addAttribute("hd", hd);

        List<HoaDonChiTiet> hdct = hoaDonService.danhSachChiTietHoaDonByIDHD(id);
        model.addAttribute("hdct",hdct);

        System.out.println(hd.getMaHoaDon());
        return "admin/hoa-don/chitiethoadontaiquay";
    }

    @GetMapping("/search")
    public String searchHoaDon(@RequestParam(value = "keyword", required = false, defaultValue = "null") String keyword
            ,@RequestParam(defaultValue = "0") int page, Model model) {
        int size = 5;
        Page<HoaDon> ketQua = hoaDonService.timkiemhoadon(keyword,page,size);
        for(HoaDon hd:ketQua){
            System.out.println(hd.getMaHoaDon());
        }
        model.addAttribute("hd", ketQua);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ketQua.getTotalPages());
        return "admin/hoa-don/hoadontaiquay";// trang html hiển thị danh sách kết quả

    }

    @GetMapping("/search-date")
    public String searchByDateRange(@RequestParam("startDate") String startDate,
                                    @RequestParam("endDate") String endDate,
                                    @RequestParam(defaultValue = "0") int page,
                                    Model model) {
        int size = 10;
        Page<HoaDon> pageHoaDon = hoaDonService.searchHoaDonByDateRange(startDate, endDate, page,size);
        model.addAttribute("hd", pageHoaDon);
        model.addAttribute("totalPages", pageHoaDon.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/hoa-don/hoadontaiquay"; // hoặc file Thymeleaf bạn đang dùng để hiển thị hóa đơn



    }



}

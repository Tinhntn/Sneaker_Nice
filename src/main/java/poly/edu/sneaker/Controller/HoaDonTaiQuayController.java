package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

}

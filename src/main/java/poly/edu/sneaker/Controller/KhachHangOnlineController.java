package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.edu.sneaker.DAO.KhuyenMaiCustom;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Model.KhuyenMai;
import poly.edu.sneaker.Service.HoaDonService;
import poly.edu.sneaker.Service.KhachHangOnlineService;

import java.util.ArrayList;

@Controller
@RequestMapping("/khachhangonline")
public class KhachHangOnlineController {
    @Autowired
    KhachHangOnlineService khachHangOnlineService;
    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page) {
        KhachHang khachHang = khachHangOnlineService.layKhachHangQuaid(1);
        model.addAttribute("khachhang",khachHang);
        return "user/khachhang/thongtinkhachhang";
    }
}

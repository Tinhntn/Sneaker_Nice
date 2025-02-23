package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Service.ChiTietSanPhamService;

@Controller
@RequestMapping("/Sneakers_Nice")
public class Home {
    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;

    @GetMapping("/hienthi")
    public String hienThi(Model model){
        Pageable pageable = PageRequest.of(0,10);
        Page<ChiTietSanPham> lstChiTietSanPham = chiTietSanPhamService.findAll(pageable);
        model.addAttribute("lstChiTietSanPham",lstChiTietSanPham);
        return "user/sanpham/trangchu";
    }
}

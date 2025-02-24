package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Service.ChiTietSanPhamService;

import java.util.List;

@Controller
@RequestMapping("/Sneakers_Nice")
public class Home {
    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;

    @GetMapping("/hienthi")
    public String hienthi(Model model){
        Page<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findAll(PageRequest.of(0,10));
        model.addAttribute("listSanPham", lstCTSP);
        return "user/sanpham/trangchu";
    }

    @GetMapping("/chitietsanpham/{id}")
    public String chiTietSanPham(@PathVariable("id") int id, Model model){
//        ChiTietSanPham chiTietSanPhams = chiTietSanPhamService.getCTSPByIdSP(PageRequest.of(0,10),id);
//        model.addAttribute("chiTietSanPham",chiTietSanPhams);
        System.out.println();
        return "user/sanpham/detailSanPham";
    }
}

package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.edu.sneaker.Model.SanPham;
import poly.edu.sneaker.Service.SanPhamService;

@Controller
@RequestMapping("/sanpham")
public class SanPhamController {
    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping("/hienthi")
    public String hienThi(Model model, @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false)String keyword){
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);
        Page<SanPham> lstSanPham;
        if (keyword != null && !keyword.isEmpty()) {
            lstSanPham = sanPhamService.findByMaSanPhamOrTenSanPham(keyword,keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            lstSanPham = sanPhamService.findAll(pageable);
        }
        model.addAttribute("lstSanPham", lstSanPham.getContent());
        model.addAttribute("currentPage", lstSanPham.getNumber());
        model.addAttribute("totalPages", lstSanPham.getTotalPages());
        return "admin/sanpham/listSanPham";
    }
}

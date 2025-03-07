package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.Interface.SanPhamInterface;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Model.Size;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import poly.edu.sneaker.Service.MauSacService;
import poly.edu.sneaker.Service.SanPhamService;
import poly.edu.sneaker.Service.SizeService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Sneakers_Nice")
public class Home {
    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private SizeService sizeService;
    @Autowired
    private MauSacService mauSacService;
    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0")int page){
        int size = 12;
        Page<ChiTietSanPham> lstCTSP  = chiTietSanPhamService.findChiTietSanPhamJustOne(PageRequest.of(page, size));
//        Page<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findAll(PageRequest.of(page,size));
        model.addAttribute("listSanPham", lstCTSP);
        model.addAttribute("currentPage",lstCTSP.getNumber());
        model.addAttribute("totalPages",lstCTSP.getTotalPages());
        return "user/sanpham/trangchu";
    }

    @GetMapping("/chitietsanpham/{id}")
    public String chiTietSanPham(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes){
        ChiTietSanPham chiTietSanPhams = chiTietSanPhamService.findById(id);
        if (chiTietSanPhams == null) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
            return "redirect:/Sneakers/hienthi"; // Điều hướng về trang chủ hoặc trang danh sách sản phẩm
        }
        ArrayList<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findByIdSanPham(chiTietSanPhams.getIdSanPham().getId());

        Set<MauSac> lstMauSacs = new HashSet<>();
        Set<Size> lstSize = new HashSet<>();

        for (ChiTietSanPham ctsp : lstCTSP) {
            lstMauSacs.add(ctsp.getIdMauSac());
            lstSize.add(ctsp.getIdSize());
        }

        model.addAttribute("chiTietSanPham",chiTietSanPhams);
        model.addAttribute("lstCTSP",lstCTSP);
        model.addAttribute("lstSize",lstSize);
        model.addAttribute("lstMauSac",lstMauSacs);
        return "user/sanpham/detailSanPham";
    }
    @GetMapping("/chitietsanpham")
    public ResponseEntity<ChiTietSanPham> getChiTietSanPhamByIdMauSac(@RequestParam int idCTSP, @RequestParam int idMauSac){
        ChiTietSanPham ct = chiTietSanPhamService.findCTSPByIDMauSac(idCTSP,idMauSac);
        if(ct!=null){
            ct.getHinhAnh();
            return ResponseEntity.ok(ct);
        }
        return ResponseEntity.notFound().build();
    }
}

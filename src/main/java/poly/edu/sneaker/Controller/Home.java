package poly.edu.sneaker.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Model.Size;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import poly.edu.sneaker.Service.MauSacService;
import poly.edu.sneaker.Service.SanPhamService;
import poly.edu.sneaker.Service.SizeService;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.*;

import java.util.*;

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
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private GioHangChiTietService gioHangChiTietService;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page) {
        KhachHang khachHangSession = (KhachHang) httpSession.getAttribute("khachHangSession");
        int size = 12;
        Page<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findChiTietSanPhamJustOne(PageRequest.of(page, size));
        int soLuongSanPhamTrongGioHang = 0;

        if (khachHangSession != null) {
            model.addAttribute("khachHang", khachHangSession);
            GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangSession.getId());
            ArrayList<GioHangChiTiet> lstGioHangChiTiet = gioHangChiTietService.findByIdGioHang(gioHang.getId());
            model.addAttribute("lstGioHangChiTiet", lstGioHangChiTiet);
            for (GioHangChiTiet ghct : lstGioHangChiTiet) {
                soLuongSanPhamTrongGioHang += ghct.getSoLuong();
            }
            model.addAttribute("soLuongSanPhamTrongGioHang", soLuongSanPhamTrongGioHang);
        }
        model.addAttribute("listSanPham", lstCTSP);
        model.addAttribute("currentPage", lstCTSP.getNumber());
        model.addAttribute("totalPages", lstCTSP.getTotalPages());
        return "user/sanpham/trangchu";
    }

    @GetMapping("/chitietsanpham/{id}")
    public String chiTietSanPham(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        ChiTietSanPham chiTietSanPhams = chiTietSanPhamService.findById(id);
        if (chiTietSanPhams == null) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
            return "redirect:/Sneakers/hienthi";
        }
        ArrayList<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findByIdSanPham(chiTietSanPhams.getIdSanPham().getId());

        Set<MauSac> lstMauSacs = new HashSet<>();
        Set<Size> lstSize = new HashSet<>();

        for (ChiTietSanPham ctsp : lstCTSP) {
            lstMauSacs.add(ctsp.getIdMauSac());
            lstSize.add(ctsp.getIdSize());
        }

        model.addAttribute("chiTietSanPham", chiTietSanPhams);
        model.addAttribute("lstCTSP", lstCTSP);
        model.addAttribute("lstSize", lstSize);
        model.addAttribute("lstMauSac", lstMauSacs);
        return "user/sanpham/detailSanPham";
    }

    @GetMapping("/chitietsanpham")
    public ResponseEntity<ChiTietSanPham> getChiTietSanPhamByIdMauSac(@RequestParam int idCTSP, @RequestParam int idMauSac) {
        ChiTietSanPham ct = chiTietSanPhamService.findCTSPByIDMauSac(idCTSP, idMauSac);
        if (ct != null) {
            ct.getHinhAnh();
            return ResponseEntity.ok(ct);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/ajax-search")
    public String ajaxSearch(@RequestParam("keyword") String keyword, Model model) {
        List<ChiTietSanPham> results = chiTietSanPhamService.searchByMultipleFields(keyword);
        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);
        return "layout/ajaxSearch :: ajaxSearchResultsFragment";
    }

    @GetMapping("/filter")
    public String filterProducts(
            Model model,
            @RequestParam(required = false) String hang,
            @RequestParam(required = false) String chatLieu,
            @RequestParam(required = false) String priceRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChiTietSanPham> result = chiTietSanPhamService.filterByHangAndPrice(hang, chatLieu, priceRange, pageable);

        model.addAttribute("filteredProducts", result.getContent());
        model.addAttribute("currentPage", result.getNumber());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("hang", hang);
        model.addAttribute("chatLieu", chatLieu);
        model.addAttribute("priceRange", priceRange);

        // Lấy các giá trị bộ lọc phụ theo cascading
        List<String> availableChatLieu = chiTietSanPhamService.findDistinctChatLieuByHang(hang);
        List<String> availableHang = chiTietSanPhamService.findDistinctHangByChatLieu(chatLieu);
        model.addAttribute("availableChatLieu", availableChatLieu);
        model.addAttribute("availableHang", availableHang);

        // Thêm danh sách sản phẩm mới
        Page<ChiTietSanPham> newProducts = chiTietSanPhamService.findChiTietSanPhamJustOne(PageRequest.of(0, 12));
        model.addAttribute("listSanPham", newProducts);

        return "user/sanpham/trangchu";
    }
}

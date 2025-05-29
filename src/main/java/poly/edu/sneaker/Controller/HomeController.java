package poly.edu.sneaker.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Model.Interface.SanPhamInterface;
import poly.edu.sneaker.Service.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Sneakers_Nice")
public class HomeController {
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
    HoaDonChiTietOnlService hoaDonChiTietOnlService;
    @Autowired
    KhachHangService khachHangService;
    @Autowired
    private HangService hangService;

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) Integer idHang,
                          @RequestParam(required = false) Integer idDanhMuc,
                          @RequestParam(required = false) Integer idChatLieu,
                          @RequestParam(required = false) Integer idMauSac,
                          @RequestParam(required = false) Integer idSize,
                          @RequestParam(required = false) String keyword) {
        int size = 12;
        Page<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findChiTietSanPhamJustOneTT(keyword,idHang,idDanhMuc,idChatLieu,idMauSac,idSize,PageRequest.of(page, size));
        List<Hang> hangs = hangService.getAllHangs();
        model.addAttribute("listHang", hangs);
        model.addAttribute("listSanPham", lstCTSP);
        model.addAttribute("currentPage", lstCTSP.getNumber());
        model.addAttribute("totalPages", lstCTSP.getTotalPages());
        //code hung
        List<Map<String, Object>> bestSellingProducts = hoaDonChiTietOnlService.getTop10BestSellingProducts();
        model.addAttribute("bestSellingProducts", bestSellingProducts);
        List<Map<String, Object>> newestProducts = chiTietSanPhamService.getTop10NewestProducts();
        model.addAttribute("newestProducts", newestProducts);
        //end code hung
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

    @GetMapping("/layAnh")
    public ResponseEntity<ChiTietSanPham> getChiTietSanPhamByIdMauSac(@RequestParam int idSP, @RequestParam int idMauSac) {
        List<ChiTietSanPham> lstCT = chiTietSanPhamService.findIDSPByIDMauSac(idSP, idMauSac);
        ChiTietSanPham ct = lstCT.get(0);
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
        return "user/sanpham/trangchu";
    }

    @GetMapping("/lay-combination")
    @ResponseBody
    public ResponseEntity<?> checkSoLuongSanPham(
            @RequestParam("idSanPham") int idSanPham,
            @RequestParam("idSize") int idSize,
            @RequestParam("idMauSac") int idMauSac) {

        try {
            // Kiểm tra các tham số đầu vào
            if (idSanPham <= 0 || idSize <= 0 || idMauSac <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thông tin sản phẩm không hợp lệ"
                ));
            }

            ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findCTSPByIdSPAndIdMauSacAndIdSize(idSanPham, idSize, idMauSac);

            if (chiTietSanPham == null) {
                return ResponseEntity.ok().body(Map.of(
                        "success", false,
                        "message", "Sản phẩm không tồn tại với màu sắc và kích cỡ đã chọn"
                ));
            }

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "chiTietSanPham", chiTietSanPham
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + e.getMessage()
            ));
        }
    }

}

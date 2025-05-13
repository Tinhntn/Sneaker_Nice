package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.KhuyenMaiCustom;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Model.KhuyenMai;
import poly.edu.sneaker.Service.HoaDonService;
import poly.edu.sneaker.Service.KhachHangOnlineService;
import poly.edu.sneaker.Service.KhachHangService;

import java.util.ArrayList;

@Controller
@RequestMapping("/khachhangonline")
public class KhachHangOnlineController {
    @Autowired
    KhachHangOnlineService khachHangOnlineService;
    @Autowired
    KhachHangService khachHangService;
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }
    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page) {

        KhachHang khachHang = khachHangService.findByEmail(getCurrentUserEmail());
        System.out.println(khachHang.getTenKhachHang());
        if(khachHang==null){
            return "/dang-nhap";
        }
        model.addAttribute("khachhang",khachHang);
        return "user/khachhang/thongtinkhachhang";
    }
    @PostMapping("/updatethongtin")
    public String capNhatThongTinKhachHang(
            @RequestParam("tenKhachHang") String tenKhachHang,
            @RequestParam("email") String email,
            @RequestParam("sdt") String sdt,
            @RequestParam("gioiTinh") Boolean gioiTinh,
            @RequestParam("tinhThanhPho") String tinhThanhPho,
            @RequestParam("quanHuyen") String quanHuyen,
            @RequestParam("phuongXa") String phuongXa,
            RedirectAttributes redirectAttributes) {
        KhachHang khachHang1 = khachHangService.findByEmail(getCurrentUserEmail());
        // ⚠️ Kiểm tra dữ liệu đầu vào
        if (tenKhachHang == null || tenKhachHang.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                sdt == null || sdt.trim().isEmpty() ||
                tinhThanhPho == null || tinhThanhPho.trim().isEmpty() ||
                quanHuyen == null || quanHuyen.trim().isEmpty() ||
                phuongXa == null || phuongXa.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin khách hàng!");
            return "redirect:/khachhangonline/hienthi";
        }

        // Các bước xử lý tiếp theo...
        KhachHang khachHang = khachHangOnlineService.layKhachHangQuaid(khachHang1.getId());
        if (khachHang != null) {
            khachHang.setTenKhachHang(tenKhachHang);
            khachHang.setEmail(email);
            khachHang.setSdt(sdt);
            khachHang.setGioiTinh(gioiTinh);
            khachHang.setTinhThanhPho(tinhThanhPho);
            khachHang.setQuanHuyen(quanHuyen);
            khachHang.setPhuongXa(phuongXa);

            khachHangOnlineService.saveKH(khachHang);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng!");
        }

        return "redirect:/khachhangonline/hienthi";
    }



}
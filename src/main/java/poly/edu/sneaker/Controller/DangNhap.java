package poly.edu.sneaker.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.KhachHangService;
import poly.edu.sneaker.Service.NhanVienService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class DangNhap {
    @Autowired
    NhanVienService nhanVienService;
    @Autowired
    KhachHangService khachHangService;

    @Controller
    public class AuthController {
        @GetMapping("/dang-nhap")
        public String dangNhap() {
            return "user/dangnhap"; // Trả về trang login Thymeleaf (hoặc HTML)
        }
    }

//    @GetMapping("/dang-nhap")
//    public String dangNhap(Model model, @ModelAttribute("loginError") String loginError) {
//        model.addAttribute("dangNhap", new DangNhapRequest());
//        model.addAttribute("dangKy", new DangKyRequest());
//        model.addAttribute("loginError", loginError);
//        return "user/dangnhap";
//    }

    @GetMapping("/quen_mat_khau")
    public String quanMatKhau() {
        return "user/quen_mat_khau";
    }

    @GetMapping("dang_ky_moi")
    public String dangKyMoi() {
        return "user/dang_ky_moi";
    }

    //    @PostMapping("/quen_mat_khau")
//    @ResponseBody
//    public ResponseEntity<?> QuenMatKhau(@RequestParam("email") String email) {
//        UUID idNhanVien = nhanVienService.getIdNhanVienByEmail(email);
//        UUID idKhachHang = khachHangService.getIdKhachHangByEmail(email);
//
//        // Kiểm tra nếu cả hai ID đều là null
//        if (idNhanVien == null && idKhachHang == null) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản không tồn tại"));
//        }
//
//        if (idKhachHang != null) {
//            KhachHang khachHang = khachHangService.getKhachHangByID(idKhachHang);
//            if (khachHang == null) {
//                return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản khách hàng không tồn tại"));
//            }
//
//            boolean kh = khachHangService.layLaiMatKhau(khachHang);
//            if (!kh) {
//                return ResponseEntity.badRequest().body(Map.of("message", "Email không còn hoạt động"));
//            }
//            return ResponseEntity.ok().body(Map.of("message", "Mật khẩu mới của bạn đã được gửi về địa chỉ email"));
//        }
//
//        if (idNhanVien != null) {
//            NhanVien nhanVien = nhanVienService.getNhanVienById(idNhanVien);
//            if (nhanVien == null) {
//                return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản nhân viên không tồn tại"));
//            }
//
//            boolean nv = nhanVienService.LayLaiMatKhau(nhanVien);
//            if (!nv) {
//                return ResponseEntity.badRequest().body(Map.of("message", "Email không còn hoạt động"));
//            }
//            return ResponseEntity.ok().body(Map.of("message", "Mật khẩu mới của bạn đã được gửi về địa chỉ email"));
//        }
//
//        return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản không tồn tại"));
//    }
//


    @PostMapping("/dang-nhap")
    @ResponseBody
    public ResponseEntity<?> dangNhap(@RequestBody Map<String, String> body, HttpSession httpSession) {
        // Kiểm tra dữ liệu đầu vào
        if (body == null || !body.containsKey("email") || !body.containsKey("matKhau")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu thông tin đăng nhập!"));
        }

        String email = body.get("email");
        String matKhau = body.get("matKhau");

        if (email == null || matKhau == null || email.isEmpty() || matKhau.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng nhập email và mật khẩu"));
        }

        NhanVien nhanVien = nhanVienService.getNhanVienByEmailandMatKhau(email, matKhau);
        KhachHang khachHang = khachHangService.findByEmailAndMatKhau(email, matKhau);

        Map<String, Object> response = new HashMap<>();

//        if (nhanVien != null) {
//            httpSession.setAttribute("nhanVien", nhanVien);
//            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ADMIN"));
//            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(nhanVien, null, authorities);
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//            response.put("message", "Đăng nhập thành công!");
//            response.put("redirectUrl", "/sanpham/hienthi");
//            return ResponseEntity.ok(response);
//        } else if (khachHang != null) {
//            httpSession.setAttribute("khachHang", khachHang);
//            response.put("message", "Đăng nhập thành công!");
//            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
//            UsernamePasswordAuthenticationToken authToken =
//                    new UsernamePasswordAuthenticationToken(khachHang, null, authorities);
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//            response.put("redirectUrl", "/Sneakers_Nice/hienthi");
//            return ResponseEntity.ok(response);
//        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Sai email hoặc mật khẩu!"));
    }
//
//    @GetMapping("/check-role")
//    public ResponseEntity<?> checkRole() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập!");
//        }
//
//        return ResponseEntity.ok("User: " + auth.getName() + ", Roles: " + auth.getAuthorities());
//    }

//    @PostMapping("/dang-ky")
//    @ResponseBody
//    public ResponseEntity<?> dangKy(@RequestBody DangKyRequest dangKyRequest) {
//        String emailDK = dangKyRequest.getEmailDK();
//        String hoTen = dangKyRequest.getHoTen();
//        String matKhauDK = dangKyRequest.getMatKhauDK();
//        if (emailDK.isEmpty() || emailDK == null || hoTen.isEmpty() || hoTen == null || matKhauDK.isEmpty() || matKhauDK == null) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng điền đầy đủ thông tin"));
//        }
//        if (!khachHangService.isPasswordValid(matKhauDK)) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu phải có ít nhất 6 ký tự và chứa ít nhất một chữ và một số"));
//        }
//        if (!khachHangService.emailValidate(emailDK)) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Email không đúng định dạng"));
//        }
//        if (khachHangService.existsByEmail(dangKyRequest.getEmailDK())) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản đã tồn tại"));
//        }
//        khachHangService.dangKy(hoTen, emailDK, matKhauDK);
//        KhachHangReponse kh = khachHangService.getKhachHangByEmailAndMatKhau(emailDK, matKhauDK);
//        gioHangService.addGioHangByKhachHang(kh.getId());
//        return ResponseEntity.ok().body(Map.of("message", "Đăng ký thành công"));
//    }
//
//    @GetMapping("/dang-xuat")
//    public String dangXuat() {
//        session.setAttribute("khachHang", null);
//        return "redirect:/";
//    }


}

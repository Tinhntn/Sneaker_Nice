package poly.edu.sneaker.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.edu.sneaker.Model.GioHang;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.GioHangService;
import poly.edu.sneaker.Service.KhachHangService;
import poly.edu.sneaker.Service.NhanVienService;

import java.util.*;
import java.util.regex.Pattern;

@Controller
public class DangNhap {
    @Autowired
    NhanVienService nhanVienService;
    @Autowired
    KhachHangService khachHangService;
    @Autowired
    HttpSession session;
    @Autowired
    GioHangService gioHangService;

    @Controller
    public class AuthController {
        @GetMapping("/dang-nhap")

        public String dangNhap() {

            return "user/dangnhap"; // Trả về trang login Thymeleaf (hoặc HTML)
        }
    }


    @GetMapping("/quen_mat_khau")
    public String quanMatKhau() {
        return "user/quen_mat_khau";
    }

    @GetMapping("/dang_ky_moi")
    public String dangKyMoi() {
        return "user/dang_ky_moi";
    }

    @PostMapping("/quen_mat_khau")
    @ResponseBody
    public ResponseEntity<?> QuenMatKhau(@RequestParam("email") String email) {

        NhanVien nhanVien = nhanVienService.getNhanVienByEmail(email);
        KhachHang khachHang = khachHangService.findByEmail(email);
        // Kiểm tra nếu cả hai ID đều là null
        if (nhanVien == null && khachHang == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản không tồn tại"));
        }

        if (khachHang != null) {

            boolean kh = khachHangService.layLaiKhachHang(khachHang);
            if (!kh) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email không còn hoạt động"));
            }
            return ResponseEntity.ok().body(Map.of("message", "Mật khẩu mới của bạn đã được gửi về địa chỉ email"));
        }

        if (nhanVien != null) {
            boolean nv = nhanVienService.layLaiMatKhauNhanVien(nhanVien);
            if (!nv) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email không còn hoạt động"));
            }
            return ResponseEntity.ok().body(Map.of("message", "Mật khẩu mới của bạn đã được gửi về địa chỉ email"));
        }

        return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản không tồn tại"));
    }


//    @PostMapping("/api/dang-nhap")
//    @ResponseBody
//    public ResponseEntity<?> dangNhap(@RequestBody Map<String, String> body, HttpSession httpSession) {
//        // Kiểm tra dữ liệu đầu vào
//        if (body == null || !body.containsKey("email") || !body.containsKey("password")) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu thông tin đăng nhập!"));
//        }
//        String email = body.get("email");
//        String password = body.get("password");
//        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng nhập email và mật khẩu"));
//        }
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(email, password)
//            );
//            // Nếu thành công, lưu authentication vào SecurityContext
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            //Lấy thông tin người dùng đã xác thực
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof UserDetails) {
//                UserDetails userDetails = (UserDetails) principal;
//                String role = userDetails.getAuthorities().iterator().next().getAuthority();
//                if ("ROLE_ADMIN".equals(role) || "ROLE_EMPLOYEE".equals(role)) {
//                    NhanVien nhanVien = nhanVienService.getNhanVienByEmail(email);
//                        session.setAttribute("nhanVienSession", nhanVien);
//                } else {
//                    KhachHang khachHang = khachHangService.findByEmail(email);
//                    session.setAttribute("khachHangSession", khachHang);
//                }
//                return ResponseEntity.ok().body(Map.of("message", "Đăng nhập thành công","role",role.replace("ROLE_","")));
//            }
//        } catch (Exception ex) {
//            return ResponseEntity.badRequest().body(Map.of("message","Sai thông tin đăng nhập"));
//        }
//        return ResponseEntity.badRequest().body(Map.of("message","Đăng nhập thất bại"));
//
//
//    }


    @PostMapping("/dang-ky")
    @ResponseBody
    public ResponseEntity<?> dangKy(@RequestBody Map<String, Object> thongTinDangKy) {
        String emailDK = (String) thongTinDangKy.get("emailDK");
        String hoTen = (String) thongTinDangKy.get("hoTen");
        String matKhauDK = (String) thongTinDangKy.get("matKhauDK");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String hashPasswork = passwordEncoder.encode(matKhauDK);
        if (emailDK.isEmpty() || emailDK == null || hoTen.isEmpty() || hoTen == null || matKhauDK.isEmpty() || matKhauDK == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng điền đầy đủ thông tin"));
        }
        if (!isValidPassword(matKhauDK)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu phải có ít nhất 8 ký tự và chứa ít nhất một chữ và một số"));
        }
        if (!emailValidate(emailDK)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không đúng định dạng"));
        }
        if (khachHangService.exitsKhachHangByEmail(emailDK)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản đã tồn tại"));
        }
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(khachHangService.taoMaKhachHang());
        khachHang.setTenKhachHang(hoTen);
        khachHang.setEmail(emailDK);
        khachHang.setSdt("0123456789");
        khachHang.setMatKhau(hashPasswork);
        khachHang.setNgayTao(new Date());
        khachHang.setTrangThai(true);
        khachHangService.saveKhachHang(khachHang);
        GioHang gioHang = new GioHang();
        gioHang.setMaGioHang(gioHangService.taoMaGioHang());
        gioHang.setIdKhachHang(khachHang);
        gioHang.setNgayTao(new Date());
        gioHang.setTrangThai(true);
        gioHang.setGhiChu("Giỏ hàng tự động tạo khi khách hàng đăng ký");
        gioHangService.save(gioHang);
        return ResponseEntity.ok().body(Map.of("message", "Đăng ký thành công"));
    }

    @GetMapping("/dang-xuat")
    public String dangXuat() {
        session.setAttribute("khachHang", null);
        return "redirect:/";
    }

    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private boolean emailValidate(String email) {
        if (email == null) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }

    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*!]).*$";

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return Pattern.matches(PASSWORD_REGEX, password);
    }

}

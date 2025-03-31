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

    @GetMapping("dang_ky_moi")
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

            boolean kh =khachHangService.layLaiKhachHang(khachHang);
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


    @PostMapping("/dang-nhap")
    @ResponseBody
    public ResponseEntity<?> dangNhap(@RequestBody Map<String, String> body) {
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

        if(nhanVien!=null){
            session.setAttribute("nhanVienSession",nhanVien);
            return ResponseEntity.ok().body(Map.of("message","Đăng nhập thành công"));
        }else if(khachHang!=null){
            session.setAttribute("khachHangSession",khachHang);
            return ResponseEntity.ok().body(Map.of("message","Đăng nhập thành công"));
        }else{

            return ResponseEntity.badRequest().body(Map.of("message","Sai thông tin đăng nhập"));
        }
//        Map<String, Object> response = new HashMap<>();

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

    @PostMapping("/dang-ky")
    @ResponseBody
    public ResponseEntity<?> dangKy(@RequestBody Map<String, Object> thongTinDangKy) {
        String emailDK = (String) thongTinDangKy.get("emailDK");
        String hoTen = (String) thongTinDangKy.get("hoTen");
        String matKhauDK = (String) thongTinDangKy.get("matKhauDK");

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
            System.out.println(khachHangService.exitsKhachHangByEmail(emailDK));
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản đã tồn tại"));
        }
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(khachHangService.taoMaKhachHang());
        khachHang.setTenKhachHang(hoTen);
        khachHang.setEmail(emailDK);
        khachHang.setMatKhau(matKhauDK);
        khachHang.setNgayTao(new Date());
        khachHang.setTrangThai(true);
        khachHangService.saveKhachHang(khachHang);
        KhachHang kh = khachHangService.findByEmailAndMatKhau(emailDK, matKhauDK);
        GioHang gioHang = new GioHang();
        gioHang.setMaGioHang(gioHangService.taoMaGioHang());
        gioHang.setIdKhachHang(kh);
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

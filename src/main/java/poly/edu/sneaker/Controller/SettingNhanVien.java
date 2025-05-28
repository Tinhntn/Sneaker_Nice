package poly.edu.sneaker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.EmailService;
import poly.edu.sneaker.Service.NhanVienService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@RequestMapping("/capnhat")
public class SettingNhanVien {
    @Autowired
    NhanVienService nhanVienService;
    @Autowired
    EmailService emailService;
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }
    @Autowired
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    @Autowired
    HttpSession session;
    @GetMapping("/laylaimatkhau")
    @ResponseBody
    public ResponseEntity<?> layLaiMatKhau() {
        try {
            String email = getCurrentUserEmail(); // Lấy email người dùng đang đăng nhập
            NhanVien nv = nhanVienService.getNhanVienByEmail(email);

            if (nv == null) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không tìm thấy nhân viên"));

            // Tạo mã xác nhận
            String maXacNhan = String.valueOf(new Random().nextInt(900000) + 100000); // 6 số

            // Gửi email
            String subject = "Mã xác nhận lấy lại mật khẩu";
            String body = "Mã xác nhận của bạn là: " + maXacNhan;

            emailService.sendLayLaiMatKhau(nv.getEmail(), subject, body);

            // Lưu tạm vào session (hoặc Redis, hoặc DB tạm)
            session.setAttribute("maXacNhan", maXacNhan);
            session.setAttribute("emailXacNhan", nv.getEmail());

            return ResponseEntity.ok(Map.of("success", true, "maXacNhan", maXacNhan)); // hoặc bỏ không gửi mã về frontend nếu muốn bảo mật
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi: " + e.getMessage()));
        }
    }
    @PostMapping("/tao-mat-khau-moi")
    @ResponseBody
    public ResponseEntity<?> taoMatKhauMoi(HttpSession session) {
        try {
            String email = (String) session.getAttribute("emailXacNhan");
            if (email == null) return ResponseEntity.badRequest().body(Map.of("message", "Phiên làm việc đã hết hạn."));

            NhanVien nv = nhanVienService.getNhanVienByEmail(email);
            if (nv == null) return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy nhân viên."));

            // Tạo mật khẩu mới
            String newPassword = UUID.randomUUID().toString().substring(0, 8); // Ví dụ: 8 ký tự
            String encodedPassword = passwordEncoder.encode(newPassword);

            nv.setMatKhau(encodedPassword);
            nhanVienService.saveNhanVien(nv);

            // Gửi mật khẩu mới
            String subject = "Mật khẩu mới của bạn";
            String body = "Mật khẩu mới của bạn là: " + newPassword;

            emailService.sendLayLaiMatKhau(email, subject, body);

            // Xoá session
            session.removeAttribute("emailXacNhan");
            session.removeAttribute("maXacNhan");

            return ResponseEntity.ok(Map.of("message", "Mật khẩu mới đã được gửi tới email của bạn"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi server: " + e.getMessage()));
        }
    }

    @GetMapping("/thongtincanhan")
    public String thongtincanhan(Model model) {

        NhanVien nhanVien = nhanVienService.getNhanVienByEmail(getCurrentUserEmail());
        model.addAttribute("nhanVien", nhanVien);
        return "admin/nhanvien/thong-tin-ca-nhan";
    }
    @PutMapping(value = "/cap-nhat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> capNhatThongTin(
            @RequestPart("nhanVien") String nhanVienJson,
            @RequestPart(value = "hinhAnhFile", required = false) MultipartFile hinhAnhFile) {

        try {
            NhanVien nv = nhanVienService.getNhanVienByEmail(getCurrentUserEmail());

            // Chuyển đổi JSON string thành đối tượng NhanVien
            ObjectMapper objectMapper = new ObjectMapper();
            NhanVien nhanVien = objectMapper.readValue(nhanVienJson, NhanVien.class);
            // Xử lý upload file nếu có
            if (hinhAnhFile != null && !hinhAnhFile.isEmpty()) {
                // Tạo tên file duy nhất
                String fileName = System.currentTimeMillis() + "_" + hinhAnhFile.getOriginalFilename();
                // Thư mục lưu ảnh (trong static/images)
                String uploadDir = "src/main/resources/static/images/";

                // Tạo thư mục nếu chưa tồn tại
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // Lưu file
                Path path = Paths.get(uploadDir, fileName);
                Files.copy(hinhAnhFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Lưu đường dẫn ảnh dùng để hiển thị (dùng trong HTML)
                nhanVien.setHinhAnh("/images/" + fileName);  // ✅ Đường dẫn đúng
            }else{
                nhanVien.setHinhAnh(nv.getHinhAnh());
            }


            // Cập nhật ngày sửa
            nhanVien.setNgaySua(new Date());
            nhanVien.setId(nv.getId());
            nhanVien.setIdChucVu(nv.getIdChucVu());
            nhanVien.setMaNhanVien(nv.getMaNhanVien());
            nhanVien.setNgayTao(nv.getNgayTao());
            nhanVien.setTrangThai(nv.getTrangThai());
            nhanVien.setMatKhau(nv.getMatKhau());
            // Lưu thông tin nhân viên
            nhanVienService.updateNhanVien(nhanVien,nhanVien.getId());

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Cập nhật thông tin thành công"
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi upload hình ảnh: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi server: " + e.getMessage()
            ));
        }
    }
    @PostMapping("/doi-mat-khau")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        try {
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");
            // Lấy thông tin nhân viên

            NhanVien nhanVien = nhanVienService.getNhanVienByEmail(getCurrentUserEmail());
            if (nhanVien == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message","Người dùng không tồn tại"));
            }

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            // Kiểm tra mật khẩu hiện tại
            if (!passwordEncoder.matches(currentPassword, nhanVien.getMatKhau())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message","Mật khẩu không đúng"));
            }
            System.out.println(nhanVien);
            System.out.println(isPasswordValid(newPassword));
            // Validate mật khẩu mới
            if (!isPasswordValid(newPassword)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm 1 ký tự hoa và 1 ký tự đặc biệt"));

            }
            // Mã hóa và lưu mật khẩu mới
            nhanVien.setMatKhau(passwordEncoder.encode(newPassword));
            System.out.println(nhanVien.getMatKhau());
            nhanVienService.saveNhanVien(nhanVien);
            return ResponseEntity.ok().body(Collections.singletonMap("message","Đổi mật khẩu thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message","Đổi mật khẩu thất bại"));
        }
    }

    private boolean isPasswordValid(String password) {
        // Kiểm tra mật khẩu có ít nhất 8 ký tự, 1 ký tự hoa và 1 ký tự đặc biệt
        String pattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$";
        return password.matches(pattern);
    }
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}

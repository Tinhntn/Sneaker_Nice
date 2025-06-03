package poly.edu.sneaker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.NhanVienCustom;
import poly.edu.sneaker.DAO.NhanVienRequest;
import poly.edu.sneaker.DAO.NhanVienUpdateRequest;
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.ChucVuService;
import poly.edu.sneaker.Service.KhachHangService;
import poly.edu.sneaker.Service.NhanVienService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/nhanvien")
public class NhanVienController {
    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private ChucVuService chucVuService;

    @Autowired
    KhachHangService khachHangService;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String SDT_PATTERN = "^0\\d{9,10}$";
    @GetMapping("/hienthi")
    public String hienThiNhanVien(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Boolean trangThai) {

        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"ngayTao"));
        Page<NhanVienCustom> nhanVienCustomPageable;

        if (keyword != null && !keyword.isEmpty()) {
            nhanVienCustomPageable = nhanVienService.search(keyword,trangThai, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            nhanVienCustomPageable = nhanVienService.getAll(pageable);
        }

        List<ChucVu> listChucVu = chucVuService.getAll();
        model.addAttribute("listChucVu", listChucVu);
        model.addAttribute("nhanVienCustomList", nhanVienCustomPageable.getContent());
        model.addAttribute("currentPage", nhanVienCustomPageable.getNumber());
        model.addAttribute("totalPages", nhanVienCustomPageable.getTotalPages());

        return "admin/nhanVien/ListNhanVien";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addNhanVien(
            @RequestBody Map<String, Object> requestBody) {
        try {
            String hoVaTen = (String) requestBody.get("hovaten");
            Integer idcv = (Integer) requestBody.get("idcv");
            Boolean gioiTinh = (Boolean) requestBody.get("gioitinh");
            String diaChi = (String) requestBody.get("diachi");
            String sdt = (String) requestBody.get("sdt");
            String email = (String) requestBody.get("email");
            Boolean trangThai = (Boolean) requestBody.get("trangthai");

            if (gioiTinh == null ) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Giới tính không được để trống"));
            }
            if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Họ tên không được để trống"));
            }
            Object ngaySinhObj = requestBody.get("ngaysinh");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date ngaySinhFormat = sdf.parse(requestBody.get("ngaysinh").toString());
            Date ngaySinh = null;
            if (ngaySinhObj != null) {
                if (ngaySinhObj instanceof String) {
                    // Thử parse từ nhiều định dạng
                    String[] dateFormats = {
                            "yyyy-MM-dd",
                            "EEE MMM dd HH:mm:ss zzz yyyy", // Format "Mon May 14 00:00:00 ICT 1990"
                            "dd/MM/yyyy"
                    };

                    for (String format : dateFormats) {
                        try {
                            ngaySinh = new SimpleDateFormat(format).parse((String) ngaySinhObj);
                            break;
                        } catch (ParseException e) {
                            // Bỏ qua và thử format tiếp theo
                        }
                    }

                    if (ngaySinh == null) {
                        return ResponseEntity.badRequest().body(
                                Map.of("success", false, "message", "Định dạng ngày không hợp lệ")
                        );
                    }
                } else if (ngaySinhObj instanceof Long) {
                    // Nếu là timestamp
                    ngaySinh = new Date((Long) ngaySinhObj);
                }
            }

            if(ngaySinhFormat.after(new Date())){
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Ngày sinh không được ở tương lai"));
            }

            if (diaChi== null || diaChi.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Không được để trống địa chỉ"));
            }
            if (sdt == null || sdt.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Không được để trống số điện thoại"));
            } else if (!sdt.matches(SDT_PATTERN)) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Số điện thoại không đúng định dạng (bắt đầu bằng 0, tối thiểu 10 số)"));
            }

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email không được để trống"));
            } else if (!email.matches(EMAIL_PATTERN)) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email không đúng định dạng"));
            }

            ChucVu chucVu = chucVuService.findChucVuById(idcv);
            if (chucVu == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("success", false, "message", "Không tìm thấy chức vụ")
                );
            }
            List<NhanVien> lstNhanVien = nhanVienService.findAllNhanVien();
            for(NhanVien nhanVien1 : lstNhanVien) {
                if(nhanVien1.getEmail().equalsIgnoreCase(email)){
                    return ResponseEntity.badRequest().body(Map.of("success",false,"message","email đã tồn tại"));
                }
            }
            List<KhachHang> lstKhachHang = khachHangService.findAll();
            for (KhachHang kh : lstKhachHang) {
                if ((kh.getEmail() != null && kh.getEmail().equalsIgnoreCase(email)) ||
                        (kh.getSdt() != null && kh.getSdt().equalsIgnoreCase(sdt))) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "email hoặc số điện thoại đã tồn tại"
                    ));
                }
            }

            // Create new employee
            String matKhau = "SN12345@"; // Default password
            NhanVien nhanVien = new NhanVien();
            nhanVien.setMaNhanVien(nhanVienService.taoMa());
            nhanVien.setHoVaTen(hoVaTen);
            nhanVien.setIdChucVu(chucVu);
            nhanVien.setNgaySinh(ngaySinh);
            nhanVien.setGioiTinh(gioiTinh);
            nhanVien.setDiaChi(diaChi);
            nhanVien.setSdt(sdt);
            nhanVien.setEmail(email);
            nhanVien.setMatKhau(new BCryptPasswordEncoder(10).encode(matKhau));
            nhanVien.setNgayTao(new Date());
            nhanVien.setTrangThai(trangThai);
            nhanVienService.saveNhanVien(nhanVien);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thêm nhân viên thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "Lỗi khi thêm nhân viên: " + e.getMessage())
            );
        }
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseEntity<?> getNhanVienDetail(@PathVariable("id") int id) {
        try {
            NhanVien detailNhanVien = nhanVienService.findNhanVienById(id);
            if (detailNhanVien == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("success", false, "message", "Không tìm thấy nhân viên")
                );
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", detailNhanVien
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "Lỗi khi lấy thông tin nhân viên")
            );
        }
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateNhanVien(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, Object> requestBody) {

        try {
            // Extract data from request body
            String hoVaTen = (String) requestBody.get("hovaten");
            Integer idcv = (Integer) requestBody.get("idcv");
            Boolean gioiTinh = (Boolean) requestBody.get("gioitinh");
            String diaChi = (String) requestBody.get("diachi");
            String sdt = (String) requestBody.get("sdt");
            String email = (String) requestBody.get("email");
            Boolean trangThai = (Boolean) requestBody.get("trangthai");
            List<NhanVien> lstNhanVien = nhanVienService.findAllNhanVien();
            boolean coAdmin = lstNhanVien.stream()
                    .anyMatch(item -> item.getIdChucVu() != null
                            && item.getIdChucVu().getMaChucVu() != null
                            && item.getIdChucVu().getMaChucVu().toUpperCase().endsWith("ADMIN"));
            String maChucVu = chucVuService.findChucVuById(idcv).getMaChucVu();



            if (!trangThai) {
                boolean laAdmin = maChucVu != null && maChucVu.equalsIgnoreCase("ADMIN");

                if (laAdmin && !coAdmin) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Phải có tối thiểu 1 tài khoản admin trong hệ thống"
                    ));
                }
            }

            // Validate input
            if (gioiTinh == null ) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Giới tính không được để trống"));
            }
            if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Họ tên không được để trống"));
            }

            Object ngaySinhObj = requestBody.get("ngaysinh");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date ngaySinhFormat = sdf.parse(requestBody.get("ngaysinh").toString());
            Date ngaySinh = null;
            if (ngaySinhObj != null) {
                if (ngaySinhObj instanceof String) {
                    // Thử parse từ nhiều định dạng
                    String[] dateFormats = {
                            "yyyy-MM-dd",
                            "EEE MMM dd HH:mm:ss zzz yyyy", // Format "Mon May 14 00:00:00 ICT 1990"
                            "dd/MM/yyyy"
                    };

                    for (String format : dateFormats) {
                        try {
                            ngaySinh = new SimpleDateFormat(format).parse((String) ngaySinhObj);
                            break;
                        } catch (ParseException e) {
                            // Bỏ qua và thử format tiếp theo
                        }
                    }

                    if (ngaySinh == null) {
                        return ResponseEntity.badRequest().body(
                                Map.of("success", false, "message", "Định dạng ngày không hợp lệ")
                        );
                    }
                } else if (ngaySinhObj instanceof Long) {
                    // Nếu là timestamp
                    ngaySinh = new Date((Long) ngaySinhObj);
                }
            }

            if(ngaySinhFormat.after(new Date())){
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Ngày sinh không được ở tương lai"));
            }

            if (diaChi== null || diaChi.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Không được để trống địa chỉ"));
            }
            if (sdt == null || sdt.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Không được để trống số điện thoại"));
            } else if (!sdt.matches(SDT_PATTERN)) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Số điện thoại không đúng định dạng (bắt đầu bằng 0, tối thiểu 10 số)"));
            }

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email không được để trống"));
            } else if (!email.matches(EMAIL_PATTERN)) {
                return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email không đúng định dạng"));
            }

            NhanVien nhanVien = nhanVienService.findNhanVienById(id);

            if (nhanVien == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("success", false, "message", "Không tìm thấy nhân viên")
                );
            }


            if(nhanVien.getIdChucVu().getMaChucVu().toUpperCase().endsWith("ADMIN")&&!maChucVu.toUpperCase().endsWith("ADMIN")){
                return ResponseEntity.badRequest().body(Map.of("message","Không thể hạ phân quyền của bản thân"));
            }
            ChucVu chucVu = chucVuService.findChucVuById(idcv);
            if (chucVu == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("success", false, "message", "Không tìm thấy chức vụ")
                );
            }
            for(NhanVien nhanVien1 : lstNhanVien) {
                if(nhanVien1.getId()!=nhanVien.getId()&&nhanVien1.getEmail().equalsIgnoreCase(email)){
                    return ResponseEntity.badRequest().body(Map.of("success",false,"message","email đã tồn tại"));
                }
            }
            List<KhachHang> lstKhachHang = khachHangService.findAll();
            for (KhachHang kh : lstKhachHang) {
                if ((kh.getEmail() != null && kh.getEmail().equalsIgnoreCase(email)) ||
                        (kh.getSdt() != null && kh.getSdt().equalsIgnoreCase(sdt))) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "email hoặc số điện thoại đã tồn tại"
                    ));
                }
            }

            // Update fields
            nhanVien.setHoVaTen(hoVaTen);
            nhanVien.setIdChucVu(chucVu);
            nhanVien.setNgaySinh(ngaySinh);
            nhanVien.setGioiTinh(gioiTinh);
            nhanVien.setDiaChi(diaChi);
            nhanVien.setSdt(sdt);
            nhanVien.setEmail(email);
            nhanVien.setTrangThai(trangThai);
            nhanVien.setNgaySua(new Date());
            nhanVienService.updateNhanVien(nhanVien, id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật nhân viên thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "Lỗi khi cập nhật nhân viên: " + e.getMessage())
            );
        }
    }
}

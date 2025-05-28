package poly.edu.sneaker.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.NhanVienCustom;
import poly.edu.sneaker.DAO.NhanVienRequest;
import poly.edu.sneaker.DAO.NhanVienUpdateRequest;
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.ChucVuService;
import poly.edu.sneaker.Service.NhanVienService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/nhanvien")
public class NhanVienController {
    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private ChucVuService chucVuService;

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String SDT_PATTERN = "^0\\d{9,10}$";

    @GetMapping("/hienthi")
    public String hienThiNhanVien(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(required = false) String keyword) {

        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<NhanVienCustom> nhanVienCustomPageable;

        if (keyword != null && !keyword.isEmpty()) {
            nhanVienCustomPageable = nhanVienService.search(keyword, pageable);
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
    public ResponseEntity<?> addNhanVien(@RequestParam(value ="hovaten", required = false) String hoVaTen,
                                         @RequestParam(value ="idcv", required = false) Integer idcv,
                                         @RequestParam(value = "ngaysinh", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySinh,
                                         @RequestParam(value ="gioitinh", required = false) Boolean gioiTinh,
                                         @RequestParam(value ="diachi", required = false) String diaChi,
                                         @RequestParam(value ="sdt", required = false) String sdt,
                                         @RequestParam(value ="email", required = false) String email,
                                         @RequestParam(value ="trangthai", required = false) Boolean trangThai) {
        try {
            // Validate input
            Map<String, String> errors = new HashMap<>();
            if (gioiTinh == null ) {
                errors.put("gioiTinh", "Giới tính không được để trống");
            }
            if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
                errors.put("hoVaTen", "Họ và tên không được để trống");
            }
            if (ngaySinh == null||ngaySinh.toString().isEmpty()) {
                errors.put("ngaySinh", "Ngày sinh không được để trống");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date ngaySinhFormat = sdf.parse(ngaySinh.toString());

            if(ngaySinhFormat.after(new Date())){
                errors.put("ngaySinh","Ngày sinh không được ở tương lai");
            }

            if (diaChi== null || diaChi.trim().isEmpty()) {
                errors.put("diaChi", "Địa chỉ không được để trống");
            }

            if (sdt == null || sdt.trim().isEmpty()) {
                errors.put("sdt", "Số điện thoại không được để trống");
            } else if (sdt.matches(SDT_PATTERN)) {
                errors.put("sdt", "Số điện thoại không đúng định dạng (bắt đầu bằng 0, tối thiểu 10 số)");
            }

            if (email == null || email.trim().isEmpty()) {
                errors.put("email", "Email không được để trống");
            } else if (email.matches(EMAIL_PATTERN)) {
                errors.put("email", "Email không đúng định dạng");
            }            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "errors", errors)
                );
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
                    return ResponseEntity.badRequest().body(Map.of("succsess",false,"message","email đã tồn tại"));
                }
            }
            // Validate input
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "errors", errors)
                );
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
            nhanVien.setTrangThai(true);
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

            // Validate input
            if (gioiTinh == null ) {
                return ResponseEntity.badRequest().body(Map.of("succsess",false,"message","Giới tính không được để trống"));
            }
            if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("succsess",false,"message","Họ tên không được để trống"));
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
                return ResponseEntity.badRequest().body(Map.of("succsess",false,"message","Ngày sinh không được ở tương lai"));
            }

            if (diaChi== null || diaChi.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("succsess",false,"message","Không được để trống địa chỉ"));
            }
            System.out.println(sdt);
            if (sdt == null || sdt.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message","Không được để trống số điện thoại"));
            } else if (sdt.matches(SDT_PATTERN)) {
                return ResponseEntity.badRequest().body(Map.of("message","Số điện thoại không đúng định dạng (bắt đầu bằng 0, tối thiểu 10 số)"));
            }

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("succsess",false,"message","Email không được để trống"));
            } else if (email.matches(EMAIL_PATTERN)) {
                return ResponseEntity.badRequest().body(Map.of("succsess",false,"message","Email không đúng định dạng"));
            }


            NhanVien nhanVien = nhanVienService.findNhanVienById(id);
            if (nhanVien == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("success", false, "message", "Không tìm thấy nhân viên")
                );
            }

            ChucVu chucVu = chucVuService.findChucVuById(idcv);
            if (chucVu == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("success", false, "message", "Không tìm thấy chức vụ")
                );
            }
            List<NhanVien> lstNhanVien = nhanVienService.findAllNhanVien();
            for(NhanVien nhanVien1 : lstNhanVien) {
                if(nhanVien1.getId()!=nhanVien.getId()&&nhanVien1.getEmail().equalsIgnoreCase(email)){
                    return ResponseEntity.badRequest().body(Map.of("succsess",false,"message","email đã tồn tại"));
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

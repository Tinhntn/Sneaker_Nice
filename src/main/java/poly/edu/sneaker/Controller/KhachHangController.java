package poly.edu.sneaker.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.GioHang;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Service.GioHangService;
import poly.edu.sneaker.Service.KhachHangService;
import jakarta.validation.Valid;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Controller
@RequestMapping("/khach_hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    GioHangService gioHangService;

    @Autowired
    HttpSession session;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @GetMapping("/hienthi")
    public String hienThiKhachHang(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false) String sortDir,
                                   @RequestParam(required = false) String trangThai) {
        Page<KhachHang> khachHangPage;

        if (keyword != null && !keyword.isEmpty()) {
            khachHangPage = khachHangService.search(keyword, PageRequest.of(page, size));
        } else {
            // Nếu trangThai là chuỗi rỗng, chuyển thành null
            Boolean trangThaiFilter = (trangThai == null || trangThai.isEmpty()) ? null : Boolean.valueOf(trangThai);
            khachHangPage = khachHangService.filterAndSort(trangThaiFilter, sortBy, sortDir, PageRequest.of(page, size));
        }

        model.addAttribute("listKH", khachHangPage.getContent());
        model.addAttribute("currentPage", khachHangPage.getNumber());
        model.addAttribute("totalPages", khachHangPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("keyword", keyword);
        return "admin/khach_hang/ListKhachHang";
    }

    // Hiển thị form thêm khách hàng
    @GetMapping("/add")
    public String showAddForm(Model model) {
        KhachHang khachHang = new KhachHang();
        // Sinh mã khách hàng tự động và đảm bảo không trùng lặp
        String maKhachHang = khachHangService.taoMaKhachHang();
        while (khachHangService.findByMaKhachHang(maKhachHang) != null) {
            maKhachHang = khachHangService.taoMaKhachHang();
        }
        khachHang.setMaKhachHang(maKhachHang);
        model.addAttribute("khachHang", khachHang);
        return "admin/khach_hang/add";
    }


    @PostMapping("/add")
    public String addKhachHang(@ModelAttribute("khachHang") KhachHang khachHang, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // Validate tên khách hàng
        if (khachHang.getTenKhachHang() == null || khachHang.getTenKhachHang().isBlank()) {
            bindingResult.rejectValue("tenKhachHang", "error.khachHang", "Tên khách hàng không được để trống");
        } else if (khachHang.getTenKhachHang().length() < 2 || khachHang.getTenKhachHang().length() > 100) {
            bindingResult.rejectValue("tenKhachHang", "error.khachHang", "Tên khách hàng phải từ 2 đến 100 ký tự");
        }

        // Validate email
        if (khachHang.getEmail() == null || khachHang.getEmail().isBlank()) {
            bindingResult.rejectValue("email", "error.khachHang", "Email không được để trống");
        } else if (!khachHang.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            bindingResult.rejectValue("email", "error.khachHang", "Email không đúng định dạng");
        }

        // Validate số điện thoại
        if (khachHang.getSdt() == null || khachHang.getSdt().isBlank()) {
            bindingResult.rejectValue("sdt", "error.khachHang", "Số điện thoại không được để trống");
        } else if (!khachHang.getSdt().matches("\\d+")) {
            bindingResult.rejectValue("sdt", "error.khachHang", "Số điện thoại chỉ được chứa chữ số");
        } else if (khachHang.getSdt().length() < 10 || khachHang.getSdt().length() > 15) {
            bindingResult.rejectValue("sdt", "error.khachHang", "Số điện thoại phải từ 10 đến 15 ký tự");
        }

        // Validate ngày sinh
        if (khachHang.getNgaySinh() == null) {
            bindingResult.rejectValue("ngaySinh", "error.khachHang", "Ngày sinh không được để trống");
        } else if (khachHang.getNgaySinh().after(new Date())) {
            bindingResult.rejectValue("ngaySinh", "error.khachHang", "Ngày sinh không được lớn hơn ngày hiện tại");
        }

        // Nếu có lỗi, trả về trang thêm khách hàng
        if (bindingResult.hasErrors()) {
            return "admin/khach_hang/add";
        }

        try {
            // Tạo mã khách hàng tự động
            String maKhachHang = khachHangService.taoMaKhachHang();
            while (khachHangService.findByMaKhachHang(maKhachHang) != null) {
                maKhachHang = khachHangService.taoMaKhachHang();
            }
            khachHang.setMaKhachHang(maKhachHang);
            String matKhauTam = "12345";
            // Mã hóa mật khẩu
                khachHang.setMatKhau(passwordEncoder.encode(matKhauTam));
            // Lưu khách hàng
            khachHangService.saveKhachHang(khachHang);
            // Tạo giỏ hàng tự động cho khách hàng
            GioHang gioHang = new GioHang();
            gioHang.setMaGioHang(gioHangService.taoMaGioHang());
            gioHang.setIdKhachHang(khachHang);
            gioHang.setNgayTao(new Date());
            gioHang.setTrangThai(true);
            gioHang.setGhiChu("Giỏ hàng tự động tạo khi thêm khách hàng");
            gioHangService.save(gioHang);

            redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng và tạo giỏ hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm khách hàng!");
        }
        return "redirect:/khach_hang/hienthi";
    }
    // Hiển thị form cập nhật khách hàng
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        KhachHang khachHang = khachHangService.findKhachHangById(id);
        if (khachHang == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng không tồn tại!");
            return "redirect:/khach_hang/hienthi";
        }
        model.addAttribute("khachHang", khachHang);
        return "admin/khach_hang/update";
    }

    @PostMapping("/update/{id}")
    public String updateKhachHang(@PathVariable("id") int id, @ModelAttribute("khachHang") KhachHang khachHang, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // Validate tên khách hàng
        if (khachHang.getTenKhachHang() == null || khachHang.getTenKhachHang().isBlank()) {
            bindingResult.rejectValue("tenKhachHang", "error.khachHang", "Tên khách hàng không được để trống");
        } else if (khachHang.getTenKhachHang().length() < 2 || khachHang.getTenKhachHang().length() > 100) {
            bindingResult.rejectValue("tenKhachHang", "error.khachHang", "Tên khách hàng phải từ 2 đến 100 ký tự");
        }

        // Validate email
        if (khachHang.getEmail() == null || khachHang.getEmail().isBlank()) {
            bindingResult.rejectValue("email", "error.khachHang", "Email không được để trống");
        } else if (!khachHang.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            bindingResult.rejectValue("email", "error.khachHang", "Email không đúng định dạng");
        }
        //sdt
        if (khachHang.getSdt() == null || khachHang.getSdt().isBlank()) {
            bindingResult.rejectValue("sdt", "error.khachHang", "Số điện thoại không được để trống");
        } else if (!khachHang.getSdt().matches("\\d+")) {
            bindingResult.rejectValue("sdt", "error.khachHang", "Số điện thoại chỉ được chứa chữ số");
        } else if (khachHang.getSdt().length() < 10 || khachHang.getSdt().length() > 15) {
            bindingResult.rejectValue("sdt", "error.khachHang", "Số điện thoại phải từ 10 đến 15 ký tự");
        }
        // Validate mật khẩu

        if (bindingResult.hasErrors()) {
            return "admin/khach_hang/update";
        }

        // Logic cũ: Cập nhật khách hàng
        try {
            // Lấy khách hàng hiện tại từ cơ sở dữ liệu
            KhachHang existingKhachHang = khachHangService.findKhachHangById(id);
            if (existingKhachHang != null) {

                // Cập nhật thông tin khách hàng
                khachHang.setId(id);
                khachHang.setMatKhau(existingKhachHang.getMatKhau());
                khachHangService.updateKhachHang(khachHang, id);

                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khách hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật khách hàng!");
        }
        return "redirect:/khach_hang/hienthi";
    }

    // Xóa khách hàng theo ID
    @GetMapping("/delete/{id}")
    public String deleteKhachHang(@PathVariable("id") Integer id,
                                  RedirectAttributes redirectAttributes) {
        try {
            khachHangService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa khách hàng!");
        }
        return "redirect:/khach_hang/hienthi";
    }
    @GetMapping("/detail/{id}")
    public String detailKhachHang(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            KhachHang khachHang = khachHangService.findKhachHangById(id);
            if (khachHang == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng không tồn tại!");
                return "redirect:/khach_hang/hienthi";
            }
            model.addAttribute("khachHang", khachHang);
            return "admin/khach_hang/detailKhachHang";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải thông tin khách hàng!");
            return "redirect:/khach_hang/hienthi";
        }
    }

    // Toggle trạng thái khách hàng
    @PostMapping("/toggleStatus/{id}")
    public String toggleStatus(@PathVariable("id") Integer id,
                               RedirectAttributes redirectAttributes) {
        try {
            KhachHang khachHang = khachHangService.findKhachHangById(id);
            if (khachHang == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng không tồn tại!");
                return "redirect:/khach_hang/hienthi";
            }
            khachHang.setTrangThai(!khachHang.getTrangThai());
            khachHangService.saveKhachHang(khachHang);
            redirectAttributes.addFlashAttribute("successMessage", "Thay đổi trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thay đổi trạng thái khách hàng!");
        }
        return "redirect:/khach_hang/hienthi";
    }


}

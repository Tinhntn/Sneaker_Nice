package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.KhachHang;
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


    // Xử lý thêm khách hàng (validate, upload hình ảnh)
    @PostMapping("/add")
    public String addKhachHang(@Valid @ModelAttribute("khachHang") KhachHang khachHang,
                               BindingResult bindingResult,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/khach_hang/add";
        }
        try {
            // Kiểm tra email đã tồn tại
            if (khachHangService.findByEmail(khachHang.getEmail()) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại!");
                return "redirect:/khach_hang/add";
            }
            // Xử lý file upload nếu có
            if (!imageFile.isEmpty()) {
                String fileName = imageFile.getOriginalFilename();
                String uploadDir = "src/main/resources/static/images/";
                Path path = Paths.get(uploadDir + fileName);
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                khachHang.setHinhAnh("/images/" + fileName);
            }
            khachHang.setNgayTao(new Date());
            khachHang.setNgaySua(new Date());
            khachHangService.saveKhachHang(khachHang);
            redirectAttributes.addFlashAttribute("successMessage", "Khách hàng được thêm thành công!");
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

    // Xử lý cập nhật khách hàng (validate, upload hình ảnh nếu có file mới)
    @PostMapping("/update/{id}")
    public String updateKhachHang(@PathVariable("id") Integer id,
                                  @Valid @ModelAttribute("khachHang") KhachHang khachHang,
                                  BindingResult bindingResult,
                                  @RequestParam("imageFile") MultipartFile imageFile,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/khach_hang/update";
        }
        try {
            KhachHang existingKhachHang = khachHangService.findKhachHangById(id);
            if (existingKhachHang == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng không tồn tại!");
                return "redirect:/khach_hang/hienthi";
            }
            khachHang.setId(id);
            khachHang.setNgayTao(existingKhachHang.getNgayTao());
            khachHang.setNgaySua(new Date());
            // Nếu có file mới, upload và cập nhật hình ảnh; nếu không, giữ nguyên hình cũ
            if (!imageFile.isEmpty()) {
                String fileName = imageFile.getOriginalFilename();
                String uploadDir = "src/main/resources/static/images/";
                Path path = Paths.get(uploadDir + fileName);
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                khachHang.setHinhAnh("/images/" + fileName);
            } else {
                khachHang.setHinhAnh(existingKhachHang.getHinhAnh());
            }
            khachHangService.updateKhachHang(khachHang, id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khách hàng thành công!");
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

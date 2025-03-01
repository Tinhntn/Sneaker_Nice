package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Service.KhachHangService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/khach_hang")
public class KhachHangController {

    private final KhachHangService khachHangService;

    @Autowired
    public KhachHangController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    @GetMapping("/hienthi")
    public String hienThiKhachHang(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(required = false) String search,
                                   Model model) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> listKH;
        if (search != null && !search.isEmpty()) {
            listKH = khachHangService.findByTenKhachHangContainingOrEmailContainingOrSdtContaining(search, search, search, pageable);
        } else {
            listKH = khachHangService.findAll(pageable);
        }
        model.addAttribute("listKH", listKH.getContent());
        model.addAttribute("currentPage", listKH.getNumber());
        model.addAttribute("totalPages", listKH.getTotalPages());
        model.addAttribute("search", search);
        return "admin/khach_hang/ListKhachHang";
    }

    @GetMapping("/delete/{id}")
    public String deleteKhachHang(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            khachHangService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa khách hàng!");
        }
        return "redirect:/khach_hang/hienthi";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("khachHang", new KhachHang());
        return "admin/khach_hang/add";
    }

    @PostMapping("/add")
    public String addKhachHang(@Valid @ModelAttribute("khachHang") KhachHang khachHang, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/khach_hang/add";
        }
        try {
            khachHangService.save(khachHang);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm khách hàng!");
        }
        return "redirect:/khach_hang/hienthi";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        KhachHang khachHang = khachHangService.findById(id);
        if (khachHang == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng không tồn tại!");
            return "redirect:/khach_hang/hienthi";
        }
        model.addAttribute("khachHang", khachHang);
        return "admin/khach_hang/update";
    }

    @PostMapping("/update/{id}")
    public String editKhachHang(@PathVariable("id") Integer id, @Valid @ModelAttribute("khachHang") KhachHang khachHang, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/khach_hang/update";
        }
        try {
            KhachHang existingKhachHang = khachHangService.findById(id);
            if (existingKhachHang != null) {
                khachHang.setId(id);
                khachHang.setNgayTao(existingKhachHang.getNgayTao());
                khachHang.setDeletedAt(existingKhachHang.getDeletedAt() != null ? existingKhachHang.getDeletedAt() : false);
                khachHangService.update(khachHang);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khách hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật khách hàng!");
        }
        return "redirect:/khach_hang/hienthi";
    }
}
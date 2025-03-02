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

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping("/hienthi")
    public String hienThiKhachHang(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> khachHangPage;

        if (keyword != null && !keyword.isEmpty()) {
            khachHangPage = khachHangService.search(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            khachHangPage = khachHangService.getAll(pageable);
        }

        model.addAttribute("listKH", khachHangPage.getContent());
        model.addAttribute("currentPage", khachHangPage.getNumber());
        model.addAttribute("totalPages", khachHangPage.getTotalPages());
        return "admin/khach_hang/ListKhachHang";
    }

    @PostMapping("/add")
    public String addKhachHang(@Valid @ModelAttribute("khachHang") KhachHang khachHang, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/khach_hang/add";
        }
        try {
            if (khachHangService.findByEmail(khachHang.getEmail()) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại!");
                return "redirect:/khach_hang/add";
            }
            khachHangService.saveKhachHang(khachHang);
            redirectAttributes.addFlashAttribute("successMessage", "Khách hàng được thêm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm khách hàng!");
        }
        return "redirect:/khach_hang/hienthi";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        KhachHang khachHang = khachHangService.findKhachHangById(id);
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
            KhachHang existingKhachHang = khachHangService.findKhachHangById(id);
            if (existingKhachHang != null) {
                khachHang.setId(id);
                khachHang.setNgayTao(existingKhachHang.getNgayTao());
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
}
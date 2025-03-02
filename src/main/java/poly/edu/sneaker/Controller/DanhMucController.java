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
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Service.DanhMucService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/danh_muc")
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping("/hienthi")
    public String hienThiDanhMuc(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<DanhMuc> danhMucPage;

        if (keyword != null && !keyword.isEmpty()) {
            danhMucPage = danhMucService.search(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            danhMucPage = danhMucService.getAll(pageable);
        }

        model.addAttribute("listDM", danhMucPage.getContent());
        model.addAttribute("currentPage", danhMucPage.getNumber());
        model.addAttribute("totalPages", danhMucPage.getTotalPages());
        return "admin/danh_muc/ListDanhMuc";
    }

    @PostMapping("/add")
    public String addDanhMuc(@Valid @ModelAttribute("danhMuc") DanhMuc danhMuc, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/danh_muc/add";
        }
        try {
            if (danhMucService.findByMaDanhMuc(danhMuc.getMaDanhMuc()) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mã danh mục đã tồn tại!");
                return "redirect:/danh_muc/add";
            }
            danhMucService.saveDanhMuc(danhMuc);
            redirectAttributes.addFlashAttribute("successMessage", "Danh mục được thêm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm danh mục!");
        }
        return "redirect:/danh_muc/hienthi";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        DanhMuc danhMuc = danhMucService.findDanhMucById(id);
        if (danhMuc == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Danh mục không tồn tại!");
            return "redirect:/danh_muc/hienthi";
        }
        model.addAttribute("danhMuc", danhMuc);
        return "admin/danh_muc/update";
    }

    @PostMapping("/update/{id}")
    public String editDanhMuc(@PathVariable("id") Integer id, @Valid @ModelAttribute("danhMuc") DanhMuc danhMuc, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/danh_muc/update";
        }
        try {
            DanhMuc existingDanhMuc = danhMucService.findDanhMucById(id);
            if (existingDanhMuc != null) {
                danhMuc.setId(id);
                danhMuc.setNgayTao(existingDanhMuc.getNgayTao());
                danhMucService.updateDanhMuc(danhMuc, id);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Danh mục không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật danh mục!");
        }
        return "redirect:/danh_muc/hienthi";
    }

    @GetMapping("/delete/{id}")
    public String deleteDanhMuc(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            danhMucService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa danh mục!");
        }
        return "redirect:/danh_muc/hienthi";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("danhMuc", new DanhMuc());
        return "admin/danh_muc/add";
    }
}
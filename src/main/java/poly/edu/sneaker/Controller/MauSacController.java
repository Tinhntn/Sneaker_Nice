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
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Service.MauSacService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/mau_sac")
public class MauSacController {

    @Autowired
    private MauSacService mauSacService;

    @GetMapping("/hienthi")
    public String hienThiMauSac(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword) {
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<MauSac> mauSacPage;

        if (keyword != null && !keyword.isEmpty()) {
            mauSacPage = mauSacService.search(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            mauSacPage = mauSacService.getAll(pageable);
        }

        model.addAttribute("listMS", mauSacPage.getContent());
        model.addAttribute("currentPage", mauSacPage.getNumber());
        model.addAttribute("totalPages", mauSacPage.getTotalPages());
        return "admin/mau_sac/ListMauSac";
    }

    @PostMapping("/add")
    public String addMauSac(@Valid @ModelAttribute("mauSac") MauSac mauSac, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/mau_sac/add";
        }
        try {
            if (mauSacService.findByMaMauSac(mauSac.getMaMauSac()) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mã màu sắc đã tồn tại!");
                return "redirect:/mau_sac/add";
            }
            mauSacService.save(mauSac);
            redirectAttributes.addFlashAttribute("successMessage", "Màu sắc được thêm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm màu sắc!");
        }
        return "redirect:/mau_sac/hienthi";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        MauSac mauSac = mauSacService.findMauSacById(id);
        if (mauSac == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Màu sắc không tồn tại!");
            return "redirect:/mau_sac/hienthi";
        }
        model.addAttribute("mauSac", mauSac);
        return "admin/mau_sac/update";
    }

    @PostMapping("/update/{id}")
    public String editMauSac(@PathVariable("id") Integer id, @Valid @ModelAttribute("mauSac") MauSac mauSac, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/mau_sac/update";
        }
        try {
            MauSac existingMauSac = mauSacService.findMauSacById(id);
            if (existingMauSac != null) {
                mauSac.setId(id);
                mauSac.setNgayTao(existingMauSac.getNgayTao());
                mauSacService.update(mauSac, id);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật màu sắc thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Màu sắc không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật màu sắc!");
        }
        return "redirect:/mau_sac/hienthi";
    }

    @GetMapping("/delete/{id}")
    public String deleteMauSac(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            mauSacService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa màu sắc thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa màu sắc!");
        }
        return "redirect:/mau_sac/hienthi";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("mauSac", new MauSac());
        return "admin/mau_sac/add";
    }
}
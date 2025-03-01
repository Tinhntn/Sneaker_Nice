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

    private final MauSacService mauSacService;

    @Autowired
    public MauSacController(MauSacService mauSacService) {
        this.mauSacService = mauSacService;
    }

    @GetMapping("/hienthi")
    public String hienThiMauSac(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) String search,
                                Model model) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<MauSac> listMS;
        if (search != null && !search.isEmpty()) {
            listMS = mauSacService.findByMaMauSacContainingOrTenMauSacContaining(search, search, pageable);
        } else {
            listMS = mauSacService.findAll(pageable);
        }
        model.addAttribute("listMS", listMS.getContent());
        model.addAttribute("currentPage", listMS.getNumber());
        model.addAttribute("totalPages", listMS.getTotalPages());
        model.addAttribute("search", search);
        return "admin/mau_sac/ListMauSac";
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

    @PostMapping("/add")
    public String addMauSac(@Valid @ModelAttribute("mauSac") MauSac mauSac, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/mau_sac/add";
        }
        try {
            mauSacService.save(mauSac);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm màu sắc thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm màu sắc!");
        }
        return "redirect:/mau_sac/hienthi";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        MauSac mauSac = mauSacService.findById(id);
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
            MauSac existingMauSac = mauSacService.findById(id);
            if (existingMauSac != null) {
                mauSac.setId(id);
                mauSac.setNgayTao(existingMauSac.getNgayTao());
                mauSac.setDeletedAt(existingMauSac.getDeletedAt() != null ? existingMauSac.getDeletedAt() : false);
                mauSacService.update(mauSac);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật màu sắc thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Màu sắc không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật màu sắc!");
        }
        return "redirect:/mau_sac/hienthi";
    }
}
package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Service.ChucVuService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/chuc_vu")
public class ChucVuController {

    @Autowired
    private ChucVuService chucVuService;

    // Hiển thị danh sách chức vụ
    @GetMapping("/hienthi")
    public String hienThiChucVu(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) String keyword) {
        int size = 5; // Số lượng bản ghi trên mỗi trang
        Pageable pageable = PageRequest.of(page, size);
        Page<ChucVu> chucVuPage;

        if (keyword != null && !keyword.isEmpty()) {
            chucVuPage = chucVuService.search(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            chucVuPage = chucVuService.getAll(pageable);
        }

        model.addAttribute("listCV", chucVuPage.getContent());
        model.addAttribute("currentPage", chucVuPage.getNumber());
        model.addAttribute("totalPages", chucVuPage.getTotalPages());
        return "admin/chuc_vu/ListChucVu";
    }

    // Hiển thị form thêm chức vụ
    @GetMapping("/add")
    public String showAddForm(Model model) {
        ChucVu chucVu = new ChucVu();
        String maChucVu = chucVuService.taoMaChucVu();
        while (chucVuService.findByMaChucVu(maChucVu) != null) {
            maChucVu = chucVuService.taoMaChucVu();
        }
        chucVu.setMaChucVu(maChucVu);
        model.addAttribute("chucVu", chucVu);
        return "admin/chuc_vu/add";
    }

    // Xử lý thêm chức vụ
    @PostMapping("/add")
    public String addChucVu(@Valid @ModelAttribute("chucVu") ChucVu chucVu,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        // Kiểm tra trùng tên chức vụ
        if (chucVuService.getAll().stream().anyMatch(cv -> cv.getTenChucVu().equalsIgnoreCase(chucVu.getTenChucVu()))) {
            bindingResult.addError(new FieldError("chucVu", "tenChucVu", "Tên chức vụ đã tồn tại"));
        }

        validateChucVu(chucVu, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/chuc_vu/add";
        }
        try {
            chucVuService.save(chucVu);
            redirectAttributes.addFlashAttribute("successMessage", "Chức vụ được thêm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm chức vụ!");
        }
        return "redirect:/chuc_vu/hienthi";
    }

    // Hiển thị form cập nhật chức vụ
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        ChucVu chucVu = chucVuService.findChucVuById(id);
        if (chucVu == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chức vụ không tồn tại!");
            return "redirect:/chuc_vu/hienthi";
        }
        model.addAttribute("chucVu", chucVu);
        return "admin/chuc_vu/update";
    }

    // Xử lý cập nhật chức vụ
    @PostMapping("/update/{id}")
    public String editChucVu(@PathVariable("id") Integer id,
                             @Valid @ModelAttribute("chucVu") ChucVu chucVu,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        // Kiểm tra trùng tên chức vụ (ngoại trừ chính nó)
        if (chucVuService.getAll().stream()
                .anyMatch(cv -> !cv.getId().equals(id) && cv.getTenChucVu().equalsIgnoreCase(chucVu.getTenChucVu()))) {
            bindingResult.addError(new FieldError("chucVu", "tenChucVu", "Tên chức vụ đã tồn tại"));
        }

        validateChucVu(chucVu, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/chuc_vu/update";
        }
        try {
            ChucVu existingChucVu = chucVuService.findChucVuById(id);
            if (existingChucVu != null) {
                chucVu.setId(id);
                chucVuService.update(chucVu, id);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chức vụ thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Chức vụ không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật chức vụ!");
        }
        return "redirect:/chuc_vu/hienthi";
    }

    // Xóa chức vụ
    @GetMapping("/delete/{id}")
    public String deleteChucVu(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            chucVuService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chức vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa chức vụ!");
        }
        return "redirect:/chuc_vu/hienthi";
    }

    // Hàm validate dữ liệu chức vụ
    private void validateChucVu(ChucVu chucVu, BindingResult bindingResult) {
        if (chucVu.getTenChucVu() == null || chucVu.getTenChucVu().isEmpty()) {
            bindingResult.addError(new FieldError("chucVu", "tenChucVu", "Tên chức vụ không được để trống"));
        }
    }
}
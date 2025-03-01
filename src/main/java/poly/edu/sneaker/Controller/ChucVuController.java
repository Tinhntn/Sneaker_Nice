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
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Service.ChucVuService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/chuc_vu")
public class ChucVuController {

    private final ChucVuService chucVuService;

    @Autowired
    public ChucVuController(ChucVuService chucVuService) {
        this.chucVuService = chucVuService;
    }

    @GetMapping("/hienthi")
    public String hienThiChucVu(@RequestParam(required = false) String search, Pageable pageable, Model model) {
        Page<ChucVu> page;
        if (search != null && !search.isEmpty()) {
            page = chucVuService.findByTenChucVuOrMaChucVuAndDeletedAt(search, search, false, pageable);
        } else {
            page = chucVuService.findAll(pageable);
        }
        model.addAttribute("listCV", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("search", search);
        return "admin/chuc_vu/ListChucVu";
    }

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

    @GetMapping("/listPage")
    @ResponseBody
    public Page<ChucVu> listPage(
            @RequestParam(defaultValue = "0") int page, // Mặc định là trang 0
            @RequestParam(defaultValue = "10") int size // Mặc định là 10 bản ghi mỗi trang
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return chucVuService.listPage(pageable);
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("chucVu", new ChucVu());
        return "admin/chuc_vu/add";
    }

    @PostMapping("/add")
    public String addChucVu(@Valid @ModelAttribute("chucVu") ChucVu chucVu, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/chuc_vu/add";
        }
        try {
            chucVuService.save(chucVu);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm chức vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm chức vụ!");
        }
        return "redirect:/chuc_vu/hienthi";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        ChucVu chucVu = chucVuService.findById(id);
        if (chucVu == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chức vụ không tồn tại!");
            return "redirect:/chuc_vu/hienthi";
        }
        model.addAttribute("chucVu", chucVu);
        return "admin/chuc_vu/update";
    }

    @PostMapping("/update/{id}")
    public String editChucVu(@PathVariable("id") Integer id, @Valid @ModelAttribute("chucVu") ChucVu chucVu, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/chuc_vu/update";
        }
        try {
            ChucVu existingChucVu = chucVuService.findById(id);
            if (existingChucVu != null) {
                chucVu.setId(id);
                chucVuService.update(chucVu);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chức vụ thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Chức vụ không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật chức vụ!");
        }
        return "redirect:/chuc_vu/hienthi";
    }
}
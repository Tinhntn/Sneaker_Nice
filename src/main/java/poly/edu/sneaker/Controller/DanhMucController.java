package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Service.DanhMucService;
import jakarta.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/danh_muc")
public class DanhMucController {

    private final DanhMucService danhMucService;

    @Autowired
    public DanhMucController(DanhMucService danhMucService) {
        this.danhMucService = danhMucService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping("/hienthi")
    public String hienThi(Model model, @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) String search) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<DanhMuc> listDM;
        if (search != null && !search.isEmpty()) {
            listDM = danhMucService.findByTenDanhMucContainingAndDeletedAt(search, false, pageable);
        } else {
            listDM = danhMucService.findAllDanhMuc(pageable);
        }
        model.addAttribute("listDM", listDM.getContent());
        model.addAttribute("currentPage", listDM.getNumber());
        model.addAttribute("totalPages", listDM.getTotalPages());
        model.addAttribute("search", search);
        return "admin/danh_muc/ListDanhMuc";
    }


    @GetMapping("/delete/{id}")
    public String deleteDanhMuc(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            danhMucService.delete(id);
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

    @PostMapping("/add")
    public String addDanhMuc(@Valid @ModelAttribute("danhMuc") DanhMuc danhMuc, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/danh_muc/add";
        }
        try {
            danhMucService.save(danhMuc);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm danh mục thành công!");
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
                danhMuc.setDeletedAt(existingDanhMuc.getDeletedAt() != null ? existingDanhMuc.getDeletedAt() : false);
                danhMucService.updateDanhMuc(danhMuc);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Danh mục không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật danh mục!");
        }
        return "redirect:/danh_muc/hienthi";
    }
}
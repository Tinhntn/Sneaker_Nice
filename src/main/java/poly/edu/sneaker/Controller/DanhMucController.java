package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Service.DanhMucService;

import jakarta.validation.Valid;

import java.util.*;

@Controller
@RequestMapping("/danh_muc")
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping("/hienthi")
    public String hienThiDanhMuc(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword) {
        int size = 5;
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
        // Kiểm tra trùng tên danh mục
        if (danhMucService.getAllDanhMucs().stream().anyMatch(dm -> dm.getTenDanhMuc().equalsIgnoreCase(danhMuc.getTenDanhMuc()))) {
            bindingResult.rejectValue("tenDanhMuc", "error.danhMuc", "Tên danh mục đã tồn tại");
        }

        // Kiểm tra trùng mã danh mục
        if (danhMucService.findByMaDanhMuc(danhMuc.getMaDanhMuc()) != null) {
            bindingResult.rejectValue("maDanhMuc", "error.danhMuc", "Mã danh mục đã tồn tại");
        }

        if (bindingResult.hasErrors()) {
            return "admin/danh_muc/add";
        }

        try {
            String maDanhMuc = danhMucService.taoMaDanhMuc();
            while (danhMucService.findByMaDanhMuc(maDanhMuc) != null) {
                maDanhMuc = danhMucService.taoMaDanhMuc();
            }
            danhMuc.setMaDanhMuc(maDanhMuc);
            danhMucService.save(danhMuc);
            redirectAttributes.addFlashAttribute("successMessage", "Danh mục được thêm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm danh mục!");
        }
        return "redirect:/danh_muc/hienthi";
    }
    @PostMapping("/them_nhanh")
    @ResponseBody
    public ResponseEntity<?> themNhanh(@ModelAttribute DanhMuc danhMuc) {

        if (danhMuc == null|| danhMuc.getTenDanhMuc() == null || danhMuc.getTenDanhMuc().trim().isEmpty()){
            return ResponseEntity.badRequest().body(Collections.singletonMap("message","Bạn cần nhập đủ thông tin"));
        }
        String tenDanhMuc = danhMuc.getTenDanhMuc();
        ArrayList<DanhMuc> danhMucs = danhMucService.getAllDanhMucs();
        for(DanhMuc danhMuc1 : danhMucs){
            if (danhMuc1.getTenDanhMuc().trim().equalsIgnoreCase(tenDanhMuc.trim())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên danh mục đã tồn tại"));
            }
        }
        DanhMuc newDM = new DanhMuc();
        newDM.setTenDanhMuc(tenDanhMuc);
        newDM.setNgayTao(new Date());
        newDM.setTrangThai(true);
        newDM.setMaDanhMuc(danhMucService.taoMaDanhMuc());
        danhMucService.save(newDM);
        return ResponseEntity.ok().body(Map.of("message","Thêm hãng mới thành công","success",true));
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
        // Kiểm tra trùng tên danh mục (ngoại trừ chính nó)
        if (danhMucService.getAllDanhMucs().stream()
                .anyMatch(dm -> !dm.getId().equals(id) && dm.getTenDanhMuc().equalsIgnoreCase(danhMuc.getTenDanhMuc()))) {
            bindingResult.rejectValue("tenDanhMuc", "error.danhMuc", "Tên danh mục đã tồn tại");
        }

        if (bindingResult.hasErrors()) {
            return "admin/danh_muc/update";
        }

        try {
            DanhMuc existingDanhMuc = danhMucService.findDanhMucById(id);
            if (existingDanhMuc != null) {
                danhMuc.setId(id);
                danhMuc.setNgayTao(existingDanhMuc.getNgayTao());
                danhMucService.update(danhMuc, id);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Danh mục không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật danh mục!");
        }
        return "redirect:/danh_muc/hienthi";
    }
    @PostMapping("/toggleStatus/{id}")
    public String toggleStatus(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            DanhMuc danhMuc = danhMucService.findDanhMucById(id);
            if (danhMuc != null) {
                danhMuc.setTrangThai(!danhMuc.getTrangThai());
                danhMucService.save(danhMuc);
                redirectAttributes.addFlashAttribute("successMessage", "Thay đổi trạng thái thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Danh mục không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thay đổi trạng thái danh mục!");
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
        DanhMuc danhMuc = new DanhMuc();
        String maDanhMuc = danhMucService.taoMaDanhMuc();
        while (danhMucService.findByMaDanhMuc(maDanhMuc) != null) {
            maDanhMuc = danhMucService.taoMaDanhMuc();
        }
        danhMuc.setMaDanhMuc(maDanhMuc);
        model.addAttribute("danhMuc", danhMuc);
        return "admin/danh_muc/add";
    }
}
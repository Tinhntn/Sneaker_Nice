package poly.edu.sneaker.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Model.Size;
import poly.edu.sneaker.Service.SizeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/size")
public class SizeController {

    @Autowired
    SizeService sizeService;


    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page){
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);
        Page<Size> sizePage = sizeService.getAll(pageable);

        System.out.println("List size: " + sizePage.getContent().size());

        model.addAttribute("sizeCustomList", sizePage.getContent());
        model.addAttribute("currentPage", sizePage.getNumber());
        model.addAttribute("totalPages", sizePage.getTotalPages());

        return "admin/size/ListSize";
    }


    @PostMapping("/add")
    public String add(@RequestParam("masize") String maSize,
                      @RequestParam("tensize") String tenSize,
                      RedirectAttributes redirectAttributes
    ){



        if (maSize == null || maSize.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã size không được để trống.");
            return "redirect:/size/hienthi";
        }

        if (tenSize == null || tenSize.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên size không được để trống.");
            return "redirect:/size/hienthi";
        }
         maSize =sizeService.taoMaSize();
        ArrayList<Size> lstMauSac = sizeService.findAll();
        for ( Size h : lstMauSac
        ) {
            if(h.getMaSize().equals(maSize)){
                maSize=sizeService.taoMaSize();
            }

        }
        Size size = new Size();
        size.setMaSize(maSize);
        size.setTenSize(tenSize);
        size.setNgayTao(new Date());
        size.setTrangThai(true);

        try {
            sizeService.saveSize(size);
            redirectAttributes.addFlashAttribute("successMessage", "Size được thêm thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/size/hienthi";
        }
        return "redirect:/size/hienthi";
    }
    @PostMapping("/them_nhanh")
    @ResponseBody
    public ResponseEntity<?> themNhanh(@ModelAttribute Size size) {

        if (size == null || size.getTenSize() == null || size.getTenSize().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Bạn cần nhập đủ thông tin"));
        }

        String tenSize = size.getTenSize().trim();

        // Regex: chỉ cho phép số nguyên hoặc số có 1 chữ số thập phân (.5)
        if (!tenSize.matches("^\\d+(\\.5)?$")) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên size chỉ được là số nguyên hoặc .5 (ví dụ: 37 hoặc 37.5)"));
        }

        ArrayList<Size> sizes = sizeService.findAll();
        for(Size size1 : sizes){
            if (size1.getTenSize().trim().equalsIgnoreCase(tenSize.trim())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên size đã tồn tại"));
            }
        }
        Size newSize = new Size();
        newSize.setTenSize(tenSize);
        newSize.setNgayTao(new Date());
        newSize.setTrangThai(true);
        newSize.setMaSize(sizeService.taoMaSize());
        sizeService.save(newSize);
        return ResponseEntity.ok().body(Map.of("message","Thêm size mới thành công","success",true));
    }
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") int id, Model model){

        System.out.println("idHang: " + id);
        Size detailSize = sizeService.getSizeById(id);

        System.out.println("ten: " + detailSize.getTenSize());
        model.addAttribute("detailSize", detailSize);
        return "admin/size/UpdateSize";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Integer idSize,
                         @RequestParam("masize") String maSize,
                         @RequestParam("tensize") String tenSize,
                         @RequestParam("trangthai") Boolean trangThai,
                         RedirectAttributes redirectAttributes
    ){

        if (maSize == null || maSize.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã size nkhông được để trống.");
            return "redirect:/size/hienthi";
        }

        if (tenSize == null || tenSize.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên size không được để trống.");
            return "redirect:/size/hienthi";
        }

        Size size = sizeService.getSizeById(idSize);
        size.setMaSize(maSize);
        size.setTenSize(tenSize);
        size.setNgaySua(new Date());
        size.setTrangThai(trangThai);

        try {
            sizeService.updateSize(size, idSize);
            redirectAttributes.addFlashAttribute("successMessage", "Size đã được cập nhật thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/size/hienthi";
        }
        return "redirect:/size/hienthi";
    }

}

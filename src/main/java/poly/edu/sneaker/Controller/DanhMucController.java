package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/danh_muc")
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate starDate,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate endDate,
                          @RequestParam(required = false) Integer trangThai) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Boolean tt =null;
        if(trangThai!=null){
            if(trangThai==0){
                tt=true;
            }else if(trangThai==1){
                tt=false;
            }
        }
        Page<DanhMuc> danhMucPage = danhMucService.locDanhMuc(keyword,starDate,endDate,tt,pageable);
        model.addAttribute("lstDanhMuc", danhMucPage.getContent());
        model.addAttribute("currentPage", danhMucPage.getNumber());
        model.addAttribute("totalPages", danhMucPage.getTotalPages());
        return "admin/danh_muc/listDanhMuc";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> add(
            @RequestParam("tenDanhMuc") String tenDanhMuc,
            @RequestParam("trangThai") Boolean trangThai) {
        if (tenDanhMuc == null || tenDanhMuc.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }
        List<DanhMuc> lstDanhMuc = danhMucService.getAllDanhMucs();
        for (DanhMuc dn : lstDanhMuc) {
            if (dn.getTenDanhMuc().equalsIgnoreCase(tenDanhMuc)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên danh mục đã tồn tại"));
            }
        }
        DanhMuc danhMuc = new DanhMuc();
        danhMuc.setMaDanhMuc(danhMucService.taoMaDanhMuc());
        danhMuc.setTenDanhMuc(tenDanhMuc);
        danhMuc.setTrangThai(trangThai);
        danhMuc.setNgayTao(new Date());
        try {
            danhMucService.save(danhMuc);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Thêm thất bại"));
        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));
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


    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable("id") Integer idDanhMuc,
                                    @RequestParam("tenDanhMuc") String tenDanhMuc,
                                    @RequestParam("trangThai") Boolean trangThai) {
        if (tenDanhMuc == null || tenDanhMuc.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }
        DanhMuc danhMuc = danhMucService.findDanhMucById(idDanhMuc);
        danhMuc.setTenDanhMuc(tenDanhMuc);
        danhMuc.setNgaySua(new Date());
        danhMuc.setTrangThai(trangThai);
        try {
            danhMucService.update(danhMuc, idDanhMuc);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Cập nhật thất bại"));

        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));

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


}
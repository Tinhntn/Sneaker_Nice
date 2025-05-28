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
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import poly.edu.sneaker.Service.MauSacService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mau_sac")
public class MauSacController {

    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    private MauSacService mauSacService;

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
        Page<MauSac> mauSacPage = mauSacService.locMauSac(keyword,starDate,endDate,tt,pageable);
        model.addAttribute("lstMauSac", mauSacPage.getContent());
        model.addAttribute("currentPage", mauSacPage.getNumber());
        model.addAttribute("totalPages", mauSacPage.getTotalPages());
        return "admin/mau_sac/ListMauSac";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> add(
            @RequestParam("tenMauSac") String tenMauSac,
            @RequestParam("trangThai") Boolean trangThai) {
        if (tenMauSac == null || tenMauSac.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }
        List<MauSac> lstMauSac = mauSacService.findAll();
        for (MauSac ms : lstMauSac) {
            if (ms.getTenMauSac().equalsIgnoreCase(tenMauSac)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên màu sắc đã tồn tại"));
            }
        }
        MauSac mauSac = new MauSac();
        mauSac.setMaMauSac(mauSacService.taoMaMauSac());
        mauSac.setTenMauSac(tenMauSac);
        mauSac.setTrangThai(trangThai);
        mauSac.setNgayTao(new Date());
        try {
            mauSacService.save(mauSac);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Thêm thất bại"));
        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));
    }
    @PostMapping("/them_nhanh")
    @ResponseBody
    public ResponseEntity<?> themNhanh(@ModelAttribute MauSac mauSac) {

        if(mauSac==null|| mauSac.getTenMauSac() == null || mauSac.getTenMauSac().trim().isEmpty()){
            return ResponseEntity.badRequest().body(Collections.singletonMap("message","Bạn cần nhập đủ thông tin"));
        }
        String tenMauSac = mauSac.getTenMauSac();
        ArrayList<MauSac> mauSacs = mauSacService.findAll();
        for(MauSac ms : mauSacs){
            if (ms.getTenMauSac().trim().equalsIgnoreCase(tenMauSac.trim())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên màu sắc đã tồn tại"));
            }
        }
        MauSac newMS = new MauSac();
        newMS.setTenMauSac(tenMauSac);
        newMS.setNgayTao(new Date());
        newMS.setTrangThai(true);
        newMS.setMaMauSac(mauSacService.taoMaMauSac());
        mauSacService.save(newMS);
        return ResponseEntity.ok().body(Map.of("message","Thêm màu sắc mới thành công","success",true));
    }


    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable("id") Integer idMauSac,
                                    @RequestParam("tenMauSac") String tenMauSac,
                                    @RequestParam("trangThai") Boolean trangThai) {
        if (tenMauSac == null || tenMauSac.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }

        List<MauSac> lstMauSac = mauSacService.findAll();
        for (MauSac ms : lstMauSac) {
            if (ms.getId()!=idMauSac&&ms.getTenMauSac().equalsIgnoreCase(tenMauSac)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên màu sắc đã tồn tại"));
            }
        }
        MauSac mauSac = mauSacService.findMauSacById(idMauSac);
        mauSac.setTenMauSac(tenMauSac);
        mauSac.setNgaySua(new Date());
        mauSac.setTrangThai(trangThai);

        try {
            mauSacService.update(mauSac);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Cập nhật thất bại"));

        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));

    }

    @PostMapping("/toggleStatus/{id}")
    public String toggleStatus(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            MauSac mauSac = mauSacService.findMauSacById(id);
            if (mauSac != null) {
                mauSac.setTrangThai(!mauSac.getTrangThai());
                mauSacService.save(mauSac);
                redirectAttributes.addFlashAttribute("successMessage", "Thay đổi trạng thái thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Màu sắc không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thay đổi trạng thái màu sắc!");
        }
        return "redirect:/mau_sac/hienthi";
    }

}
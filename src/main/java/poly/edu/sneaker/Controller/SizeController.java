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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import poly.edu.sneaker.Service.SizeService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/size")
public class SizeController {

    @Autowired
    SizeService sizeService;
    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;

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
        Page<Size> sizePage = sizeService.locSize(keyword,starDate,endDate,tt,pageable);
        model.addAttribute("lstSize", sizePage.getContent());
        model.addAttribute("currentPage", sizePage.getNumber());
        model.addAttribute("totalPages", sizePage.getTotalPages());
        return "admin/size/ListSize";
    }


    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> add(
            @RequestParam("tenSize") String tenSize,
            @RequestParam("trangThai") Boolean trangThai) {
        if (tenSize == null || tenSize.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }
        if (!tenSize.matches("^\\d+(\\.\\d{1})?$")) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message","Tên size phải là số và chỉ có 1 chữ số thập phân."));
        }
        List<Size> lstSize = sizeService.findAll();
        for (Size s : lstSize) {
            if (s.getTenSize().equalsIgnoreCase(tenSize)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên size đã tồn tại"));
            }
        }
        Size size = new Size();
        size.setMaSize(sizeService.taoMaSize());
        size.setTenSize(tenSize);
        size.setTrangThai(trangThai);
        size.setNgayTao(new Date());
        try {
            sizeService.save(size);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Thêm thất bại"));
        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));
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


    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable("id") Integer idSize,
                                    @RequestParam("tenSize") String tenSize,
                                    @RequestParam("trangThai") Boolean trangThai) {
        if (tenSize == null || tenSize.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }
        if (!tenSize.matches("^\\d+(\\.\\d{1})?$")) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message","Tên size phải là số và chỉ có 1 chữ số thập phân."));
        }
        List<Size> lstSize = sizeService.findAll();
        for (Size s : lstSize) {
            if (s.getId()!=idSize&&s.getTenSize().equalsIgnoreCase(tenSize)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên size đã tồn tại"));
            }
        }
        Size size = sizeService.findById(idSize);
        size.setTenSize(tenSize);
        size.setNgaySua(new Date());
        size.setTrangThai(trangThai);
        try {
            sizeService.update(size);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Cập nhật thất bại"));

        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));

    }

}

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
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Service.HangService;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/hang")
public class HangController {

    @Autowired
    HangService hangService;

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate starDate,
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
        Page<Hang> hangPage = hangService.locHang(keyword,starDate,endDate,tt,pageable);
        model.addAttribute("hangCustomList", hangPage.getContent());
        model.addAttribute("currentPage", hangPage.getNumber());
        model.addAttribute("totalPages", hangPage.getTotalPages());
        return "admin/hang/ListHang";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> add(
            @RequestParam("tenHang") String tenHang,
            @RequestParam("trangThai") Boolean trangThai
    ) {
        if (tenHang == null || tenHang.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }
        List<Hang> lstHang = hangService.getAllHangs();
        for (Hang hang : lstHang) {
            if (hang.getTenHang().equalsIgnoreCase(tenHang)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên hãng đã tồn tại"));
            }
        }
        Hang hang = new Hang();
        hang.setMaHang(hangService.taoMaHang());
        hang.setTenHang(tenHang);
        hang.setTrangThai(trangThai);
        hang.setNgayTao(new Date());
        try {
            hangService.saveHang(hang);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Thêm hãng thất bại"));
        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));


    }

    @PostMapping("/them_nhanh")
    @ResponseBody
    public ResponseEntity<?> themNhanh(@ModelAttribute Hang hang) {
        if (hang == null || hang.getTenHang() == null || hang.getTenHang().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Bạn cần nhập đủ thông tin"));
        }
        String tenHang = hang.getTenHang();
        List<Hang> hangs = hangService.getAllHangs();
        for (Hang hangg : hangs) {
            if (hangg.getTenHang().trim().equalsIgnoreCase(tenHang.trim())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên hãng đã tồn tại"));
            }
        }
        Hang hangg = new Hang();
        hangg.setTenHang(tenHang);
        hangg.setNgayTao(new Date());
        hangg.setTrangThai(true);
        hangg.setMaHang(hangService.taoMaHang());
        hangService.saveHang(hangg);
        return ResponseEntity.ok().body(Map.of("message", "Thêm hãng mới thành công", "success", true));
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable("id") Integer idHang,
                                    @RequestParam("tenHang") String tenHang,
                                    @RequestParam("trangThai") Boolean trangThai) {
        if (tenHang == null || tenHang.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }
        List<Hang> lstHang = hangService.getAllHangs();
        for (Hang hang : lstHang) {
            if (hang.getId()!=idHang&&hang.getTenHang().equalsIgnoreCase(tenHang)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên hãng đã tồn tại"));
            }
        }
        Hang hang = hangService.getHangById(idHang);
        hang.setTenHang(tenHang);
        hang.setNgaySua(new Date());
        hang.setTrangThai(trangThai);
        try {
            hangService.updateHang(hang, idHang);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Cập nhật thất bại"));

        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));

    }

}

package poly.edu.sneaker.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.HangService;

import java.util.Date;

@Controller
@RequestMapping("/hang")
public class HangController {

    @Autowired
    HangService hangService;

        @GetMapping("/hienthi")
        public String hienthi(Model model, @RequestParam(defaultValue = "0") int page){
            int size = 5;

            Pageable pageable = PageRequest.of(page, size);
            Page<Hang> hangPage = hangService.getAll(pageable);
            model.addAttribute("hangCustomList", hangPage.getContent());
            model.addAttribute("currentPage", hangPage.getNumber());
            model.addAttribute("totalPages", hangPage.getTotalPages());

            return "admin/hang/ListHang";
        }

    @PostMapping("/add")
    public String add(@RequestParam("mahang") String maHang,
                      @RequestParam("tenhang") String tenHang,
                      RedirectAttributes redirectAttributes
    ){

        System.out.println("hoten: " + maHang);
        System.out.println("idcv: " + tenHang);

        if (maHang == null || maHang.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã hãng không được để trống.");
            return "redirect:/hang/hienthi";
        }

        if (tenHang == null || tenHang.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên hãng không được để trống.");
            return "redirect:/hang/hienthi";
        }

        Hang hang = new Hang();
        hang.setMaHang(maHang);
        hang.setTenHang(tenHang);
        hang.setNgayTao(new Date());
        hang.setTrangThai(true);

        try {
            hangService.saveHang(hang);
            redirectAttributes.addFlashAttribute("successMessage", "Hãng được thêm thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/hang/hienthi";
        }
        return "redirect:/hang/hienthi";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") int id, Model model){

        System.out.println("idHang: " + id);
        Hang detailHang = hangService.getHangById(id);

        System.out.println("ten: " + detailHang.getTenHang());
        model.addAttribute("detailHang", detailHang);
        return "admin/hang/UpdateHang";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Integer idHang,
                         @RequestParam("mahang") String maHang,
                         @RequestParam("tenhang") String tenHang,
                         @RequestParam("trangthai") Boolean trangThai,
                         RedirectAttributes redirectAttributes
    ){

        if (maHang == null || maHang.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã hãng nkhông được để trống.");
            return "redirect:/hang/hienthi";
        }

        if (tenHang == null || tenHang.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên hãng không được để trống.");
            return "redirect:/hang/hienthi";
        }

        Hang hang = hangService.getHangById(idHang);
        hang.setMaHang(maHang);
        hang.setTenHang(tenHang);
        hang.setNgaySua(new Date());
        hang.setTrangThai(trangThai);

        try {
            hangService.updateHang(hang, idHang);
            redirectAttributes.addFlashAttribute("successMessage", "Hãng đã được cập nhật thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/hang/hienthi";
        }
        return "redirect:/hang/hienthi";
    }

}

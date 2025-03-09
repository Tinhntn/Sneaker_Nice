package poly.edu.sneaker.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.KhuyenMaiCustom;
import poly.edu.sneaker.Model.KhuyenMai;
import poly.edu.sneaker.Service.KhuyenMaiService;

import java.util.*;

@Controller
@RequestMapping("/khuyenmai")
public class KhuyenMaiController {

    @Autowired
    KhuyenMaiService khuyenMaiService;

    @GetMapping("/hienthi")
    public String hienthi(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) String keyword) {

        int size = 5; // Số lượng khuyến mại trên mỗi trang
        Pageable pageable = PageRequest.of(page, size);

        Page<KhuyenMaiCustom> pageKhuyenMaiCustoms;
        if (keyword != null && !keyword.isEmpty()) {
            pageKhuyenMaiCustoms = khuyenMaiService.findKhuyenMaiByMaKhuyenMaiContainingOrTenKhuyenMaiContaining(keyword, keyword,pageable);
            model.addAttribute("keyword", keyword);
        } else {
            pageKhuyenMaiCustoms = khuyenMaiService.getAll(pageable);
        }

        ArrayList<KhuyenMai> lstKhuyenMai = khuyenMaiService.getAllKhuyenMai();
        String maKM = khuyenMaiService.taoMaoKhuyenMai();
        for (KhuyenMai km : lstKhuyenMai
        ) {
            if(km.getMaKhuyenMai().equals(maKM)){
                maKM=khuyenMaiService.taoMaoKhuyenMai();
            }
        }
        model.addAttribute("maKhuyenMai",maKM);
        model.addAttribute("listKhuyenMai", pageKhuyenMaiCustoms.getContent());
        model.addAttribute("currentPage", pageKhuyenMaiCustoms.getNumber());
        model.addAttribute("totalPages", pageKhuyenMaiCustoms.getTotalPages());
        model.addAttribute("khuyenMai", new KhuyenMai());  // Thêm đối tượng mới để binding dữ liệu form

        return "admin/khuyenmai/addkhuyenmai";
    }

    @PostMapping("/add")
    public String addKhuyenMai(@ModelAttribute KhuyenMai khuyenMai, RedirectAttributes redirectAttributes) {

        // Kiểm tra các trường bắt buộc
        if (khuyenMai.getMaKhuyenMai() == null || khuyenMai.getMaKhuyenMai().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã khuyến mại không được để trống.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (khuyenMai.getTenKhuyenMai() == null || khuyenMai.getTenKhuyenMai().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên khuyến mại không được để trống.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (Objects.isNull(khuyenMai.getGiaTriGiam()) || khuyenMai.getGiaTriGiam() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giá trị giảm phải lớn hơn 0.");
            return "redirect:/khuyenmai/hienthi";
        }

        if (Objects.isNull(khuyenMai.getDieuKienApDung())  || khuyenMai.getDieuKienApDung() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Điều kiện áp dụng phải lớn hơn 0.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (khuyenMai.getNgayBatDau() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày bắt đầu không được để trống.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (khuyenMai.getNgayKetThuc() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày kết thúc không được để trống.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (khuyenMai.getSoLuong() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số lượng phải lớn hơn 0.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (!khuyenMai.getNgayKetThuc().after(khuyenMai.getNgayBatDau())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày kết thúc phải lớn hơn ngày bắt đầu.");
            return "redirect:/khuyenmai/hienthi";
        }

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        khuyenMai.setNgayTao(new Date());
        khuyenMai.setLoaiKhuyenMai(true);
        khuyenMai.setNgaySua(new Date());

        if (khuyenMai.getNgayBatDau().before(endOfDay) && khuyenMai.getNgayKetThuc().after(startOfDay)) {
            khuyenMai.setTrangThai(true);
        } else {
            khuyenMai.setTrangThai(false);
        }
        try {
            khuyenMaiService.addKhuyenMai(khuyenMai);
            redirectAttributes.addFlashAttribute("successMessage", "Khuyến mại đã được thêm thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/khuyenmai/hienthi";
        }

        return "redirect:/khuyenmai/hienthi";
    }

    @GetMapping("/detail/{id}")
    public String updateKhuyenMai(@PathVariable("id") int id, Model model) {
        KhuyenMai khuyenMai = khuyenMaiService.detailKhuyenMai(id);
        if (khuyenMai != null) {
            model.addAttribute("khuyenMai", khuyenMai);
            return "admin/khuyenmai/updatekhuyenmai";
        } else {
            model.addAttribute("errorMessage", "Khuyến mại không tồn tại!");
            return "redirect:/khuyenmai/hienthi";
        }
    }

    @PostMapping("/update/{id}")
    public String updateKhuyenMai(@ModelAttribute KhuyenMai khuyenMai, @PathVariable("id") int id, RedirectAttributes redirectAttributes) {

        KhuyenMai khuyenMai1 = khuyenMaiService.detailKhuyenMai(id);

        // Kiểm tra các trường bắt buộc
        if (khuyenMai.getMaKhuyenMai() == null || khuyenMai.getMaKhuyenMai().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã khuyến mại không được để trống.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (khuyenMai.getTenKhuyenMai() == null || khuyenMai.getTenKhuyenMai().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên khuyến mại không được để trống.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (Objects.isNull(khuyenMai.getGiaTriGiam()) || khuyenMai.getGiaTriGiam() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giá trị giảm phải lớn hơn 0.");
            return "redirect:/khuyenmai/hienthi";
        }

        if (Objects.isNull(khuyenMai.getDieuKienApDung())  || khuyenMai.getDieuKienApDung() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Điều kiện áp dụng phải lớn hơn 0.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (khuyenMai.getNgayBatDau() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày bắt đầu không được để trống.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (khuyenMai.getNgayKetThuc() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày kết thúc không được để trống.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (khuyenMai.getSoLuong() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số lượng phải lớn hơn 0.");
            return "redirect:/khuyenmai/hienthi";
        }
        if (!khuyenMai.getNgayKetThuc().after(khuyenMai.getNgayBatDau())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày kết thúc phải lớn hơn ngày bắt đầu.");
            return "redirect:/khuyenmai/hienthi";
        }

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        khuyenMai.setNgayTao(khuyenMai1.getNgayTao());
        khuyenMai.setNgaySua(new Date());

        if (khuyenMai.getNgayBatDau().before(endOfDay) && khuyenMai.getNgayKetThuc().after(startOfDay)) {
            khuyenMai.setTrangThai(true);
        } else {
            khuyenMai.setTrangThai(false);
        }
        try {
            khuyenMaiService.updateKhuyenMai(khuyenMai, id);
            redirectAttributes.addFlashAttribute("successMessage", "Khuyến mại đã được cập nhật thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/khuyenmai/hienthi";
        }

        return "redirect:/khuyenmai/hienthi";
    }


}

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
import poly.edu.sneaker.DAO.KhuyenMaiCustom;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.KhuyenMai;
import poly.edu.sneaker.Service.HoaDonService;
import poly.edu.sneaker.Service.KhuyenMaiService;

import java.util.*;

@Controller
@RequestMapping("/khuyenmai")
public class KhuyenMaiController {

    @Autowired
    KhuyenMaiService khuyenMaiService;
    @Autowired
    HoaDonService hoaDonService;

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
//        model.addAttribute("khuyenMai", new KhuyenMai());  // Thêm đối tượng mới để binding dữ liệu form

        return "admin/khuyenmai/listkhuyenmai";
    }

    @GetMapping("/addshow")
    public String showAddNhanVienPage(Model model) {
        model.addAttribute("khuyenMai", new KhuyenMai()); // Thêm object vào model
        return "admin/khuyenmai/addkhuyenmai";
    }

    @PostMapping("/add")
    public String addKhuyenMai(@RequestParam("tenKhuyenMai") String tenKhuyenMai,
                               @RequestParam(value = "giaTriGiam", required = false) Float giaTriGiam,
                               @RequestParam(value = "dieuKienApDung", required = false) Float dieuKienApDung,
                               @RequestParam("loaiKhuyenMai") Boolean loaiKhuyenMai,
                               @RequestParam(value = "mucGiamGiaToiDa", required = false) Float mucGiamGiaToiDa,
                               @RequestParam(value = "ngayBatDau", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                               @RequestParam(value = "ngayKetThuc", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc,
                               @RequestParam(value = "soLuong", required = false) Integer soLuong,
                               RedirectAttributes redirectAttributes) {

        // Kiểm tra tên khuyến mại không được bỏ trống
        if (tenKhuyenMai == null || tenKhuyenMai.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tên khuyến mại không được để trống.");
            return "redirect:/khuyenmai/addshow";
        }

        // Kiểm tra giá trị giảm phải lớn hơn 0
        if (giaTriGiam == null || giaTriGiam <= 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị giảm phải lớn hơn 0.");
            return "redirect:/khuyenmai/addshow";
        }

        // Kiểm tra điều kiện áp dụng và mức giảm giá tối đa nếu cần thiết (nếu có)
        if (dieuKienApDung != null && dieuKienApDung <= 0) {
            redirectAttributes.addFlashAttribute("error", "Điều kiện áp dụng phải");
            return "redirect:/khuyenmai/addshow";
        }



        if (tenKhuyenMai == null) {
            redirectAttributes.addFlashAttribute("error", "Tên khuyến mại không được để trống.");
            return "redirect:/khuyenmai/addshow";
        }

        if (giaTriGiam == null ) {
            redirectAttributes.addFlashAttribute("error", "Giá trị giảm phải lớn hơn 0.");
            return "redirect:/khuyenmai/addshow";
        }

        if (mucGiamGiaToiDa == null ) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa nhập mức giảm tối đa.");
            return "redirect:/khuyenmai/addshow";
        }

        if (dieuKienApDung == null || dieuKienApDung <= 0) {
            redirectAttributes.addFlashAttribute("error", "Điều kiện áp dụng phải lớn hơn 0.");
            return "redirect:/khuyenmai/addshow";
        }

        if (ngayBatDau == null) {
            redirectAttributes.addFlashAttribute("error", "Ngày bắt đầu không được để trống.");
            return "redirect:/khuyenmai/addshow";
        }

        if (ngayKetThuc == null) {
            redirectAttributes.addFlashAttribute("error", "Ngày kết thúc không được để trống.");
            return "redirect:/khuyenmai/addshow";
        }


        if (soLuong <= 0) {
            redirectAttributes.addFlashAttribute("error", "Số lượng phải lớn hơn 0.");
            return "redirect:/khuyenmai/addshow";
        }

        if (!ngayKetThuc.after(ngayBatDau)) {
            redirectAttributes.addFlashAttribute("error", "Ngày kết thúc phải lớn hơn ngày bắt đầu.");
            return "redirect:/khuyenmai/addshow";
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
        KhuyenMai khuyenMai = new KhuyenMai();
        khuyenMai.setTenKhuyenMai(tenKhuyenMai);
        khuyenMai.setGiaTriGiam(giaTriGiam);
        khuyenMai.setDieuKienApDung(dieuKienApDung);
        khuyenMai.setLoaiKhuyenMai(loaiKhuyenMai);
        khuyenMai.setMucGiamGiaToiDa(mucGiamGiaToiDa);
        khuyenMai.setNgayBatDau(ngayBatDau);
        khuyenMai.setNgayKetThuc(ngayKetThuc);
        khuyenMai.setSoLuong(soLuong);
        khuyenMai.setMaKhuyenMai(khuyenMaiService.taoMaoKhuyenMai());
        khuyenMai.setNgayTao(new Date());
        khuyenMai.setNgaySua(new Date());
        if (ngayBatDau.before(endOfDay) && ngayKetThuc.after(startOfDay)) {
            khuyenMai.setTrangThai(true);
        } else {
            khuyenMai.setTrangThai(false);
        }

        try {
            khuyenMaiService.addKhuyenMai(khuyenMai);
            redirectAttributes.addFlashAttribute("success", "Khuyến mại đã được thêm thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/khuyenmai/hienthi";
        }

        return "redirect:/khuyenmai/hienthi";
    }


    @GetMapping("/detail/{id}")
    public String updateKhuyenMai(@PathVariable("id") int id,
                                  @RequestParam(defaultValue = "0") int page,Model model) {
        int size = 15;
        KhuyenMai khuyenMai = khuyenMaiService.detailKhuyenMai(id);
        Page<HoaDon> hd = hoaDonService.timHoaDonTheoIdKhuyenMai(id,page,size);
        model.addAttribute("hoaDonPage", hd);

        if (khuyenMai != null) {
            model.addAttribute("khuyenMai", khuyenMai);
            return "admin/khuyenmai/updatekhuyenmai";
        } else {
            model.addAttribute("errorMessage", "Khuyến mại không tồn tại!");
            return "redirect:/khuyenmai/hienthi";
        }
    }

    @PostMapping("/update")
    public String updateKhuyenMai(@RequestParam("id") int id,
                                  @RequestParam(value = "tenKhuyenMai", required = false )String tenKhuyenMai,
                                  @RequestParam(value = "giaTriGiam", required = false) Float giaTriGiam,
                                  @RequestParam(value = "dieuKienApDung", required = false) Float dieuKienApDung,
                                  @RequestParam("loaiKhuyenMai") Boolean loaiKhuyenMai,
                                  @RequestParam("trangThai") Boolean trangThai,
                                  @RequestParam(value = "mucGiamGiaToiDa", required = false) Float mucGiamGiaToiDa,
                                  @RequestParam(value = "ngayBatDau", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                                  @RequestParam(value = "ngayKetThuc", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc,
                                  @RequestParam(value = "soLuong", required = false) Integer soLuong,
                                  RedirectAttributes redirectAttributes) {

        // Validate
        if (tenKhuyenMai == null || tenKhuyenMai.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tên khuyến mại không được để trống.");
            return "redirect:/khuyenmai/detail/" + id;
        }

        if (giaTriGiam == null || giaTriGiam <= 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị giảm phải lớn hơn 0.");
            return "redirect:/khuyenmai/detail/" + id;
        }

        if (mucGiamGiaToiDa == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa nhập mức giảm tối đa.");
            return "redirect:/khuyenmai/detail/" + id;
        }

        if (dieuKienApDung == null || dieuKienApDung <= 0) {
            redirectAttributes.addFlashAttribute("error", "Điều kiện áp dụng phải lớn hơn 0.");
            return "redirect:/khuyenmai/detail/" + id;
        }

        if (ngayBatDau == null) {
            redirectAttributes.addFlashAttribute("error", "Ngày bắt đầu không được để trống.");
            return "redirect:/khuyenmai/detail/" + id;
        }

        if (ngayKetThuc == null) {
            redirectAttributes.addFlashAttribute("error", "Ngày kết thúc không được để trống.");
            return "redirect:/khuyenmai/detail/" + id;
        }

        if (soLuong == null || soLuong <= 0) {
            redirectAttributes.addFlashAttribute("error", "Số lượng phải lớn hơn 0.");
            return "redirect:/khuyenmai/detail/" + id;
        }

        if (!ngayKetThuc.after(ngayBatDau)) {
            redirectAttributes.addFlashAttribute("error", "Ngày kết thúc phải lớn hơn ngày bắt đầu.");
            return "redirect:/khuyenmai/detail/" + id;
        }

        // Lấy khuyến mãi cũ để giữ lại thông tin không thay đổi
        KhuyenMai old = khuyenMaiService.detailKhuyenMai(id);
        // Cập nhật thông tin khuyến mãi
        KhuyenMai khuyenMai = new KhuyenMai();
        khuyenMai.setId(id);
        khuyenMai.setTenKhuyenMai(tenKhuyenMai);
        khuyenMai.setGiaTriGiam(giaTriGiam);
        khuyenMai.setDieuKienApDung(dieuKienApDung);
        khuyenMai.setLoaiKhuyenMai(loaiKhuyenMai);
        khuyenMai.setMucGiamGiaToiDa(mucGiamGiaToiDa);
        khuyenMai.setNgayBatDau(ngayBatDau);
        khuyenMai.setNgayKetThuc(ngayKetThuc);
        khuyenMai.setSoLuong(soLuong);
        khuyenMai.setDaSuDung(old.getDaSuDung());
        khuyenMai.setMaKhuyenMai(old.getMaKhuyenMai()); // giữ nguyên mã
        khuyenMai.setNgayTao(old.getNgayTao());
        khuyenMai.setNgaySua(new Date());
        khuyenMai.setTrangThai(trangThai);
        khuyenMaiService.updateKhuyenMai(khuyenMai, id);
        redirectAttributes.addFlashAttribute("success", "Khuyến mại đã được cập nhật thành công!");
        return "redirect:/khuyenmai/hienthi";
    }



}

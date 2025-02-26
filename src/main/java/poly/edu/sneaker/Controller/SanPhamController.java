package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.ChatLieu;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.SanPham;
import poly.edu.sneaker.Service.ChatLieuService;
import poly.edu.sneaker.Service.DanhMucService;
import poly.edu.sneaker.Service.HangService;
import poly.edu.sneaker.Service.SanPhamService;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/sanpham")
public class SanPhamController {
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private HangService hangService;
    @Autowired
    private DanhMucService danhMucService;
    @Autowired
    private ChatLieuService chatLieuService;

    @GetMapping("/hienthi")
    public String hienThi(Model model,RedirectAttributes redirectAttributes, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword) {
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<SanPham> lstSanPham;
        if (keyword != null && !keyword.isEmpty()) {
            lstSanPham = sanPhamService.findByMaSanPhamOrTenSanPham(keyword, keyword, pageable);
            model.addAttribute("keyword", keyword);
            if(lstSanPham==null){
                redirectAttributes.addFlashAttribute("successMessage", "Không tìm thấy sản phẩm");
                lstSanPham = sanPhamService.findAll(pageable);
            }
        } else  {
            lstSanPham = sanPhamService.findAll(pageable);
        }
        model.addAttribute("lstSanPham", lstSanPham.getContent());
        model.addAttribute("currentPage", lstSanPham.getNumber());
        model.addAttribute("totalPages", lstSanPham.getTotalPages());
        return "admin/sanpham/listSanPham";
    }

    @GetMapping("/themsanpham")
    public String hienThiFormThem(Model model) {
        model.addAttribute("sanPham", new SanPham()); // Đối tượng rỗng để Thymeleaf bind dữ liệu

        List<Hang> lstHang = hangService.getAllHangs();
        List<DanhMuc> lstDanhMuc = danhMucService.getAllDanhMucs();
        List<ChatLieu> lstChatLieu = chatLieuService.getAllChatLieus();
        List<SanPham> lstSanPham = sanPhamService.getAllSanPhams();
        String maSanPham = sanPhamService.taoMaSanPham();
        for (SanPham sp : lstSanPham
        ) {
            if (sp.getMaSanPham().equals(maSanPham)) {
                maSanPham = sanPhamService.taoMaSanPham();
            }
        }
        model.addAttribute("lstHang", lstHang);
        model.addAttribute("lstDanhMuc", lstDanhMuc);
        model.addAttribute("lstChatlieu", lstChatLieu);
        model.addAttribute("maSanPham",maSanPham);

        return "admin/sanpham/addSanPham";
    }

    @PostMapping("/themsanpham")
    public String them(@ModelAttribute SanPham sanPham,
                       @RequestParam("hang")int idHang,
                       @RequestParam("danhMuc")int idDanhMuc,
                       @RequestParam("chatLieu")int idChatLieu,
                       RedirectAttributes redirectAttributes){
        // Gán giá trị từ combobox vào sản phẩm
        sanPham.setIdHang(hangService.getHangById(idHang));
        sanPham.setIdDanhMuc(danhMucService.findDanhMucById(idDanhMuc));
        sanPham.setIdChatLieu(chatLieuService.getChatLieuById(idChatLieu));
        sanPham.setTrangThai(true);
        sanPham.setNgaySua(new Date());
        sanPham.setNgayTao(new Date());
        // Lưu sản phẩm vào database
        sanPhamService.save(sanPham);
        // Thông báo thành công
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
        return "redirect:/sanpham/hienthi";
    }
    @GetMapping("/chitietsanpham/{id}")
    public String chiTietSanPham(@PathVariable("id")int idSanPham,Model model){
        SanPham sanPham = sanPhamService.findById(idSanPham);
        model.addAttribute("sanPham",sanPham);
        List<Hang> lstHang = hangService.getAllHangs();
        List<DanhMuc> lstDanhMuc = danhMucService.getAllDanhMucs();
        List<ChatLieu> lstChatLieu = chatLieuService.getAllChatLieus();
        List<SanPham> lstSanPham = sanPhamService.getAllSanPhams();
        model.addAttribute("lstHang", lstHang);
        model.addAttribute("lstDanhMuc", lstDanhMuc);
        model.addAttribute("lstChatlieu", lstChatLieu);
        return "admin/sanpham/updateSanPham";
    }
    @PostMapping("/capnhatsanpham/{id}")
    public String updateSanPham(@PathVariable("id") int id, @ModelAttribute("sanPham") SanPham sanPham, RedirectAttributes redirectAttributes) {
        try {
            SanPham existingSanPham = sanPhamService.findById(id);
            if (existingSanPham != null) {
                existingSanPham.setTenSanPham(sanPham.getTenSanPham());
                existingSanPham.setIdHang(sanPham.getIdHang());
                existingSanPham.setIdChatLieu(sanPham.getIdChatLieu());
                existingSanPham.setIdDanhMuc(sanPham.getIdDanhMuc());
                existingSanPham.setNgaySua(new Date());
                existingSanPham.setTrangThai(sanPham.getTrangThai());

                sanPhamService.update(existingSanPham);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật sản phẩm!");
        }
        return "redirect:/sanpham/hienthi";
    }


}

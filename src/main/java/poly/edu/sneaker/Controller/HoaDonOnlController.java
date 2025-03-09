package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.HoaDonChiTietDTO;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import poly.edu.sneaker.Service.HoaDonChiTietOnlService;
import poly.edu.sneaker.Service.HoaDonOnlService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hoadononline")
public class HoaDonOnlController {

    @Autowired
    HoaDonOnlService hoaDonOnlService;

    @Autowired
    HoaDonChiTietOnlService hoaDonChiTietOnlService;

    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page){
        int size = 1;

        Pageable pageable = PageRequest.of(page, size);

        Page<HoaDonOnlCustom> listHoaDonDH = hoaDonOnlService.getHoaDonCustomDH(pageable);
        Integer sizeDH = listHoaDonDH.getContent().size();
        model.addAttribute("sizeDH",sizeDH);

        Page<HoaDonOnlCustom> listHoaDonCXN = hoaDonOnlService.getHoaDonOLChoxacnhan(pageable);
        Integer sizecxn = listHoaDonCXN.getContent().size();
        model.addAttribute("sizecxn",sizecxn);

        Page<HoaDonOnlCustom> listHoaDonCLH = hoaDonOnlService.getHoaDonOLCholayhang(pageable);
        Integer sizeclh = listHoaDonCLH.getContent().size();
        model.addAttribute("sizeclh",sizeclh);

        Page<HoaDonOnlCustom> listHoaDonDG = hoaDonOnlService.getHoaDonCustomDG(pageable);
        Integer sizedg = listHoaDonDG.getContent().size();
        model.addAttribute("sizedg",sizedg);

        Page<HoaDonOnlCustom> listHoaDonHT = hoaDonOnlService.getHoaDonCustomHT(pageable);
        Integer sizeht = listHoaDonHT.getContent().size();
        model.addAttribute("sizeht",sizeht);

        System.out.println("List hoa don dh: " + listHoaDonDH.getContent().size());

        model.addAttribute("listHoaDonDH", listHoaDonDH.getContent());
        model.addAttribute("currentPage", listHoaDonDH.getNumber());
        model.addAttribute("totalPages", listHoaDonDH.getTotalPages());

        model.addAttribute("listHoaDonCXN", listHoaDonCXN.getContent());
        model.addAttribute("currentPage", listHoaDonCXN.getNumber());
        model.addAttribute("totalPages", listHoaDonCXN.getTotalPages());

        model.addAttribute("listHoaDonCLH", listHoaDonCLH.getContent());
        model.addAttribute("currentPage", listHoaDonCLH.getNumber());
        model.addAttribute("totalPages", listHoaDonCLH.getTotalPages());

        model.addAttribute("listHoaDonDG", listHoaDonDG.getContent());
        model.addAttribute("currentPage", listHoaDonDG.getNumber());
        model.addAttribute("totalPages", listHoaDonDG.getTotalPages());

        model.addAttribute("listHoaDonHT", listHoaDonHT.getContent());
        model.addAttribute("currentPage", listHoaDonHT.getNumber());
        model.addAttribute("totalPages", listHoaDonHT.getTotalPages());

        return "admin/hoa-don/hoadononline";
    }

    @PostMapping("/xacnhanhoadoncho/{id}")
    public String xacnhanhoadoncho(@PathVariable int id,
                                   Model model, RedirectAttributes redirectAttributes) {

        NhanVien nv = new NhanVien();
        nv.setId(4);

        HoaDon hd = hoaDonOnlService.detailHD(id);
        hd.setTrangThai(2);
        hd.setIdNhanVien(nv);
        hoaDonOnlService.updateHoaDon(hd,id);
        redirectAttributes.addFlashAttribute("thanhcong","thành công");
        return "redirect:/hoadononline/hienthi";
    }

    @PostMapping("/xacnhanhoadondg/{id}")
    public String xacnhanhoadondg(@PathVariable int id,
                                  @RequestParam(value = "tennguoigiao", defaultValue = "trong") String tennguoigiao,
                                  @RequestParam(value = "sdtnguoigiao", defaultValue = "trong") String sdtnguoigiao,
                                  Model model, RedirectAttributes redirectAttributes) {

        NhanVien nv = new NhanVien();
        nv.setId(4);

        HoaDon hd = hoaDonOnlService.detailHD(id);
        hd.setTenNguoiGiao(tennguoigiao);
        hd.setSdtNguoiGiao(sdtnguoigiao);
        hd.setTrangThai(3);
        hoaDonOnlService.updateHoaDon(hd,id);
        redirectAttributes.addFlashAttribute("thanhcong","thành công");
        return "redirect:/hoadononline/hienthi";
    }

    @PostMapping("/xacnhanhoadonhoanthanh/{id}")
    public String xacnhanhoadonhoanthanh(@PathVariable int id,
                                         Model model, RedirectAttributes redirectAttributes) {

        NhanVien nv = new NhanVien();
        nv.setId(4);

        HoaDon hd = hoaDonOnlService.detailHD(id);
        hd.setTrangThai(4);
        hoaDonOnlService.updateHoaDon(hd,id);
        redirectAttributes.addFlashAttribute("thanhcong","thành công");
        return "redirect:/hoadononline/hienthi";
    }

    @PostMapping("/huydh/{id}")
    public String huydonhang(@PathVariable int id, @RequestParam(value = "ghichu",
            defaultValue = "trong") String ghichu,
                             Model model, RedirectAttributes redirectAttributes) {
        HoaDon hd = hoaDonOnlService.detailHD(id);
        hd.setGhiChu(ghichu);
        hd.setTrangThai(0);
        hoaDonOnlService.updateHoaDon(hd,id);
        List<HoaDonChiTietOnlCustom> chiTietHoaDonList = hoaDonChiTietOnlService.findByHoaDonId(hd);

        for(HoaDonChiTietOnlCustom ct : chiTietHoaDonList){
            ChiTietSanPham dt = chiTietSanPhamService.findById(ct.getId());
            System.out.println(ct.getSoLuong());
            System.out.println(dt.getSoLuong());
            dt.setSoLuong(dt.getSoLuong()+ ct.getSoLuong());
            chiTietSanPhamService.update(dt);
        }
        redirectAttributes.addFlashAttribute("thanhcong","Hủy thành công");
        return "redirect:/hoadononline/hienthi";
    }

    @GetMapping("/detailhdct/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable int id) {
        System.out.println("Nhận yêu cầu lấy chi tiết đơn hàng cho ID: " + id);

        HoaDon hoaDon = hoaDonOnlService.detailHD(id);
        if (hoaDon == null) {
            System.out.println("Lỗi: Không tìm thấy hóa đơn với ID " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hóa đơn");
        }

        List<HoaDonChiTietOnlCustom> chiTietHoaDon = hoaDonChiTietOnlService.findByHoaDonId(hoaDon);
        if (chiTietHoaDon == null || chiTietHoaDon.isEmpty()) {
            System.out.println("Lỗi: Không tìm thấy chi tiết hóa đơn cho ID " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chi tiết hóa đơn");
        }

        // Chuyển danh sách entity thành danh sách DTO
        List<HoaDonChiTietDTO> chiTietDTOs = chiTietHoaDon.stream().map(ct ->
                new HoaDonChiTietDTO(ct.getIdChiTietSanPham(), ct.getSoLuong(), ct.getTongTrongLuong(), ct.getDonGia())
        ).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("maHoaDon", hoaDon.getMaHoaDon());
        response.put("tenKhachHang", hoaDon.getIdKhachHang().getTenKhachHang());
        response.put("sdt", hoaDon.getIdKhachHang().getSdt());
        response.put("tongTien", hoaDon.getTongTien());
        response.put("chiTietSanPham", chiTietDTOs);

        return ResponseEntity.ok(response);
    }
}

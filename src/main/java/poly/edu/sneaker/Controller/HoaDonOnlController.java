package poly.edu.sneaker.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.HoaDonChiTietDTO;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import poly.edu.sneaker.Service.HoaDonChiTietOnlService;
import poly.edu.sneaker.Service.HoaDonOnlService;
import poly.edu.sneaker.Service.KhachHangService;

import java.util.*;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/hoadononline")
public class HoaDonOnlController {

    @Autowired
    HoaDonOnlService hoaDonOnlService;

    @Autowired
    HoaDonChiTietOnlService hoaDonChiTietOnlService;

    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;

    @Autowired
    KhachHangService khachHangService;

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

    @GetMapping("/detailhoadononlinect/{id}")
    public String chitiethoadononline(@PathVariable int id, Model model, @RequestParam(defaultValue = "0") int page) {
//
//        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietOnlService.findHoaDonChiTietByIdHoaDon(id);
//        model.addAttribute("hoaDonChiTiet", hoaDonChiTiet);

        HoaDon hoaDon = hoaDonOnlService.detailHD(id);
        if (hoaDon == null) {
            System.out.println("Lỗi: Không tìm thấy hóa đơn với ID " + id);
            return "redirect:/hoadononline/hienthi";
        }
        List<HoaDonChiTiet> chiTietHoaDon = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(id);
        if (chiTietHoaDon == null || chiTietHoaDon.isEmpty()) {
            System.out.println("Lỗi: Không tìm thấy chi tiết hóa đơn cho ID " + id);
            return "redirect:/hoadononline/hienthi";
        }
        model.addAttribute("chiTietHoaDon", chiTietHoaDon);
        model.addAttribute("hoaDon", hoaDon);
        System.out.println("List chi tiết hóa đơn: " + chiTietHoaDon.size());

        int size = 1;

        Pageable pageable = PageRequest.of(page, size);

        Page<HoaDonOnlCustom> listHoaDonDH = hoaDonOnlService.getHoaDonCustomDH(pageable);
        Integer sizeDH = listHoaDonDH.getContent().size();
        model.addAttribute("sizeDH",sizeDH);

        Page<HoaDonOnlCustom> listHoaDonCXN = hoaDonOnlService.getHoaDonOLChoxacnhan(pageable);
        Integer sizecxn = listHoaDonCXN.getContent().size();
        model.addAttribute("sizecxn",sizecxn);
        System.out.println("CXN" + listHoaDonCXN.getContent().size());

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

        List<ChiTietSanPham>  sanPhamChiTiet = chiTietSanPhamService.getALl();
        model.addAttribute("sanPhamChiTiet", sanPhamChiTiet);

        return "admin/hoa-don/detailhoadononline";
    }

    @GetMapping("/detailkhachhang/{id}")
    public String detailkhachhang(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        KhachHang detailkh = khachHangService.findKhachHangById(id);
        if (detailkh == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng không tồn tại!");
            return "redirect:/hoadononline/hienthi";
        }
        model.addAttribute("detailkh", detailkh);
        return "admin/hoa-don/updatekhachhang";
    }

    @PostMapping("/updatekhachhang/{id}")
    public String updatekhachhang(@PathVariable("id") Integer id,
                                    @RequestParam("tenKhachHang") String tenKhachHang,
                                    @RequestParam("tinhThanhPho") String tinhThanhPho,
                                    @RequestParam("quanHuyen") String quanHuyen,
                                    @RequestParam("phuongXa") String phuongXa,
                                    @RequestParam("sdt") String sdt,
                                    @RequestParam("trangThai") Boolean trangThai
                                    , Model model) {
        System.out.println("id khachhang" + id);

               KhachHang khachHang = new KhachHang();
               khachHang.setId(id);
               khachHang.setTenKhachHang(tenKhachHang);
               khachHang.setTinhThanhPho(tinhThanhPho);
               khachHang.setQuanHuyen(quanHuyen);
               khachHang.setPhuongXa(phuongXa);
               khachHang.setTrangThai(trangThai);
               khachHangService.updateKhachHangHung(khachHang, id);

        System.out.println("id khachhang2" + id);
        return "redirect:/hoadononline/detailkhachhang/" + id;
    }

    @PostMapping("/update-soluong")
    public ResponseEntity<?> updateSoLuong(@RequestBody Map<String, Object> payload) {
        try {
            int idChiTietHoaDon = Integer.parseInt(payload.get("idChiTietHoaDon").toString());
            int idChiTietSanPham = Integer.parseInt(payload.get("idChiTietSanPham").toString());
            int soLuongMoi = Integer.parseInt(payload.get("soLuongMoi").toString());

            // Gọi service cập nhật số lượng
            hoaDonChiTietOnlService.updateSoLuong(idChiTietHoaDon, idChiTietSanPham, soLuongMoi);

            return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    //Thêm sản phẩm chi tiết vào hóa đơn
    @PostMapping("/them-chi-tiet")
    public ResponseEntity<Map<String, Object>> themChiTiet(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra dữ liệu đầu vào
            if (!payload.containsKey("idHoaDon") || !payload.containsKey("idChiTietSanPham") || !payload.containsKey("soLuong")) {
                response.put("success", false);
                response.put("message", "Thiếu dữ liệu cần thiết.");
                return ResponseEntity.badRequest().body(response);
            }

            // Lấy giá trị ID hóa đơn & ID chi tiết sản phẩm
            int idHoaDon, idChiTietSanPham, soLuong;
            float donGia = 0;
            int trangThai = 1; // Mặc định trạng thái = 1

            try {
                idHoaDon = Integer.parseInt(payload.get("idHoaDon").toString());
                idChiTietSanPham = Integer.parseInt(payload.get("idChiTietSanPham").toString());
                soLuong = Integer.parseInt(payload.get("soLuong").toString());

                // Kiểm tra đơn giá (nếu có)
                if (payload.containsKey("donGia")) {
                    donGia = Float.parseFloat(payload.get("donGia").toString());
                }
                // Kiểm tra trạng thái (nếu có)
                if (payload.containsKey("trangThai")) {
                    trangThai = Integer.parseInt(payload.get("trangThai").toString());
                }
            } catch (NumberFormatException e) {
                response.put("success", false);
                response.put("message", "Dữ liệu không hợp lệ.");
                return ResponseEntity.badRequest().body(response);
            }

            // Tạo đối tượng HoaDon và ChiTietSanPham
            HoaDon hoaDon = new HoaDon();
            hoaDon.setId(idHoaDon);

            ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
            chiTietSanPham.setId(idChiTietSanPham);

            // Tạo đối tượng HoaDonChiTiet
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setIdHoaDon(hoaDon);
            hoaDonChiTiet.setIdChiTietSanPham(chiTietSanPham);
            hoaDonChiTiet.setSoLuong(soLuong);
            hoaDonChiTiet.setDonGia(donGia);
            hoaDonChiTiet.setNgayTao(new Date());
            hoaDonChiTiet.setNgaySua(new Date());
            hoaDonChiTiet.setTrangThai(trangThai);

            // Gọi Service để thêm hoặc cập nhật hóa đơn chi tiết
            boolean result = hoaDonChiTietOnlService.themSPCTVaoHDCT(hoaDonChiTiet);

            if (result) {
                response.put("success", true);
                response.put("message", "Cập nhật hóa đơn chi tiết thành công.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không thể thêm/cập nhật hóa đơn chi tiết.");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi xử lý backend: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @DeleteMapping("/xoa-chi-tiet/{idHoaDon}/{idChiTietSanPham}")
    public ResponseEntity<Map<String, Object>> xoaChiTiet(@PathVariable int idHoaDon, @PathVariable int idChiTietSanPham) {
        Map<String, Object> response = new HashMap<>();
        try {
            hoaDonChiTietOnlService.xoaSPCTVaoHDCT(idHoaDon, idChiTietSanPham);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


}

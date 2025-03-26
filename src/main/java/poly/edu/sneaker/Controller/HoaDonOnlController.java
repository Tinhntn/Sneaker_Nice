package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.*;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    SizeService sizeService;
    @Autowired
    LichSuTrnngThaiService lichSuTrnngThaiService;

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page) {
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);

        Page<HoaDonOnlCustom> listHoaDonDH = hoaDonOnlService.getHoaDonCustomDH(pageable);
        Integer sizeDH = listHoaDonDH.getContent().size();
        model.addAttribute("sizeDH", sizeDH);

        Page<HoaDonOnlCustom> listHoaDonCXN = hoaDonOnlService.getHoaDonOLChoxacnhan(pageable);
        Integer sizecxn = listHoaDonCXN.getContent().size();
        model.addAttribute("sizecxn", sizecxn);

        Page<HoaDonOnlCustom> listHoaDonCLH = hoaDonOnlService.getHoaDonOLCholayhang(pageable);
        Integer sizeclh = listHoaDonCLH.getContent().size();
        model.addAttribute("sizeclh", sizeclh);

        Page<HoaDonOnlCustom> listHoaDonDG = hoaDonOnlService.getHoaDonCustomDG(pageable);
        Integer sizedg = listHoaDonDG.getContent().size();
        model.addAttribute("sizedg", sizedg);

        Page<HoaDonOnlCustom> listHoaDonHT = hoaDonOnlService.getHoaDonCustomHT(pageable);
        Integer sizeht = listHoaDonHT.getContent().size();
        model.addAttribute("sizeht", sizeht);

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


        HoaDon hd = hoaDonOnlService.detailHD(id);
        hd.setTrangThai(2);
        hoaDonOnlService.updateHoaDon(hd, id);
        redirectAttributes.addFlashAttribute("thanhcong", "thành công");
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
        hoaDonOnlService.updateHoaDon(hd, id);
        redirectAttributes.addFlashAttribute("thanhcong", "thành công");
        return "redirect:/hoadononline/hienthi";
    }

    @PostMapping("/xacnhanhoadonhoanthanh/{id}")
    public String xacnhanhoadonhoanthanh(@PathVariable int id,
                                         Model model, RedirectAttributes redirectAttributes) {

        NhanVien nv = new NhanVien();
        nv.setId(4);

        HoaDon hd = hoaDonOnlService.detailHD(id);
        hd.setTrangThai(4);
        hoaDonOnlService.updateHoaDon(hd, id);
        redirectAttributes.addFlashAttribute("thanhcong", "thành công");
        return "redirect:/hoadononline/hienthi";
    }

    @PostMapping("/huydh/{id}")
    public String huydonhang(@PathVariable int id, @RequestParam(value = "ghichu",
            defaultValue = "trong") String ghichu,
                             Model model, RedirectAttributes redirectAttributes) {
        HoaDon hd = hoaDonOnlService.detailHD(id);
        hd.setGhiChu(ghichu);
        hd.setTrangThai(0);
        hoaDonOnlService.updateHoaDon(hd, id);
        List<HoaDonChiTietOnlCustom> chiTietHoaDonList = hoaDonChiTietOnlService.findByHoaDonId(hd);

        for (HoaDonChiTietOnlCustom ct : chiTietHoaDonList) {
            ChiTietSanPham dt = chiTietSanPhamService.findById(ct.getId());
            System.out.println(ct.getSoLuong());
            System.out.println(dt.getSoLuong());
            dt.setSoLuong(dt.getSoLuong() + ct.getSoLuong());
            chiTietSanPhamService.update(dt);
        }
        redirectAttributes.addFlashAttribute("thanhcong", "Hủy thành công");
        return "redirect:/hoadononline/hienthi";
    }

    @GetMapping("/detailhoadononlinect/{id}")
    public String chitiethoadononline(@PathVariable int id, Model model, @RequestParam(defaultValue = "0") int page) {
//
//        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietOnlService.findHoaDonChiTietByIdHoaDon(id);
//        model.addAttribute("hoaDonChiTiet", hoaDonChiTiet);
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
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
        List<TrangThaiDonHang> lstTrangThaiDonHang = lichSuTrnngThaiService.getAllByIdHoaDon(id);
        HoaDon hoaDon1 = hoaDonService.findById(id);
        model.addAttribute("hoaDon", hoaDon1);
        model.addAttribute("lichSuTrangThai", lstTrangThaiDonHang);
        model.addAttribute("chiTietHoaDon", chiTietHoaDon);
        model.addAttribute("hoaDon", hoaDon);

        List<Size> lstSize = sizeService.findAll();
        model.addAttribute("lstSize", lstSize);
        Page<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamService.findAll(pageable);

// Lọc sản phẩm có số lượng > 0
        List<ChiTietSanPham> sanPhamCoSoLuong = chiTietSanPhams.getContent()
                .stream()
                .filter(ctsp -> ctsp.getSoLuong() > 0)
                .collect(Collectors.toList());

// Chuyển lại thành Page
        Page<ChiTietSanPham> sanPhamChiTiet = new PageImpl<>(sanPhamCoSoLuong, pageable, sanPhamCoSoLuong.size());

        model.addAttribute("sanPhamChiTiet", sanPhamChiTiet);
        model.addAttribute("currentPageCTSP", sanPhamChiTiet.getNumber());
        model.addAttribute("totalPagesCTSP", sanPhamChiTiet.getTotalPages());
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

    @PostMapping("/update-sanpham")
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

        ChiTietSanPham chiTietSanPham;
        HoaDonChiTiet hoaDonChiTiet;

        try {
            chiTietSanPham = chiTietSanPhamService.findById(idChiTietSanPham);
            hoaDonChiTiet = hoaDonChiTietOnlService.findByIdHoaDonAndIdChiTietSanPham(idHoaDon, idChiTietSanPham);

            hoaDonChiTietOnlService.xoaSPCTVaoHDCT(idHoaDon, idChiTietSanPham);
            chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() + hoaDonChiTiet.getSoLuong());

            chiTietSanPhamService.update(chiTietSanPham);
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/doi-trang-thai")
    @ResponseBody
    public ResponseEntity<?> DoiTrangThai(@RequestBody Map<String, Object> formData) {
        try {
            int idhoadon = Integer.parseInt(formData.get("idHoaDon").toString());
            String ghichu = formData.get("ghiChu").toString();
            int trangthai = Integer.parseInt(formData.get("trangThai").toString());
            HoaDon hoaDon = hoaDonService.findById(idhoadon);
            if (trangthai == 3) {
                lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
                return ResponseEntity.ok(Map.of("message", "Chờ lấy hàng"));
            } else if (trangthai == 4) {
                if (hoaDon.getTenNguoiGiao() == null || hoaDon.getTenNguoiGiao().isEmpty() || hoaDon.getSdtNguoiGiao() == null || hoaDon.getSdtNguoiGiao().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng cập nhật thông tin người giao hàng"));
                }
                lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
                return ResponseEntity.ok(Map.of("message", "Đang giao"));

            } else if (trangthai == 5) {
                lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
                return ResponseEntity.ok(Map.of("message", "Đã giao"));
            } else if (trangthai == 6) {
                List<HoaDonChiTietOnlCustom> chiTietHoaDon = hoaDonChiTietOnlService.findByHoaDonId(hoaDon);
                for (HoaDonChiTietOnlCustom ct : chiTietHoaDon
                ) {
                    chiTietSanPhamService.capNhatSoLuongKhiHuyHoaDon(ct.getIdChiTietSanPham(), ct.getSoLuong());
                }
                lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);

                return ResponseEntity.ok(Map.of("message", "Đã hủy hóa đơn"));
            }
            return ResponseEntity.ok(Map.of("message", "Đổi trạng thái thất bại"));

        } catch (Exception e) {
            // Ghi lại log lỗi để kiểm tra sau
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng điền đầy đủ thông tin"));
        }

    }

    @PostMapping("/updatehoadon")
    @ResponseBody
    public ResponseEntity<?> capNhatThongTinHoaDon(@RequestBody Map<String, Object> hoaDon) {
        try {
            System.out.println(hoaDon);
            if (hoaDon == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Không có hóa đơn"));
            }
            HoaDon hoaDon1 = hoaDonService.findById(Integer.parseInt(hoaDon.get("id").toString()));
            if (hoaDon1 == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy hóa đơn"));
            }
            String tenNguoiNhan = hoaDon.get("tenKhachHang").toString();
            String sdtNguoiNhan = hoaDon.get("sdt").toString();
            String tinhThanhPho = hoaDon.get("tinhThanhPho").toString();
            String quanHuyen = hoaDon.get("quanHuyen").toString();
            String phuongXa = hoaDon.get("phuongXa").toString();
            String tenNguoiGiao = hoaDon.get("tenNguoiGiao").toString();
            String sdtNguoiGiao = hoaDon.get("sdtNguoiGiao").toString();
            String donViGiaoHang = hoaDon.get("donViGiaoHang").toString();
            hoaDon1.setTenNguoiNhan(tenNguoiNhan);
            hoaDon1.setSdtNguoiNhan(sdtNguoiNhan);
            hoaDon1.setTinhThanhPho(tinhThanhPho);
            hoaDon1.setQuanHuyen(quanHuyen);
            hoaDon1.setPhuongXa(phuongXa);
            hoaDon1.setTenNguoiGiao(tenNguoiGiao);
            hoaDon1.setSdtNguoiGiao(sdtNguoiGiao);
            hoaDon1.setDonViGiaoHang(donViGiaoHang);
            hoaDon1.setNgaySua(new Date());
            hoaDonService.save(hoaDon1);
            return ResponseEntity.ok(Map.of("message", "Cập nhật thông tin giao hàng thành công"));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Lỗi khi cập nhật thông tin đơn hàng"));
        }


    }
}

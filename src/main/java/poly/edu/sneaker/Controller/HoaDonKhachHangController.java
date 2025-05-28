package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/hoadononlinekhachhang")
public class HoaDonKhachHangController {

    @Autowired
    HoaDonOnlService hoaDonOnlService;

    @Autowired
    HoaDonChiTietOnlService hoaDonChiTietOnlService;

    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    HoaDonChiTietService hoaDonChiTietService;
    @Autowired
    KhachHangService khachHangService;
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    SizeService sizeService;
    @Autowired
    LichSuTrnngThaiService lichSuTrnngThaiService;
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }
//    @PostMapping("/doi-trang-thai")
//    @ResponseBody
//    public ResponseEntity<?> DoiTrangThai(@RequestBody Map<String, Object> formData) {
//        System.out.println("Đã gọi");
//        try {
//            System.out.println("đã gọi"+formData);
//            int idhoadon = Integer.parseInt(formData.get("idHoaDon").toString());
//            String ghichu = formData.get("ghiChu").toString();
//            int trangthai = Integer.parseInt(formData.get("trangThai").toString());
//            HoaDon hoaDon = hoaDonService.findById(idhoadon);
//            System.out.println(idhoadon);
//            System.out.println(ghichu);
//            System.out.println(trangthai);
//            if (trangthai == 3) {
//                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
//                if (!doiTT) {
//                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
//                }
//                return ResponseEntity.ok(Map.of("message", "Chờ lấy hàng"));
//            } else if (trangthai == 4) {
//                if (hoaDon.getTenNguoiGiao() == null || hoaDon.getTenNguoiGiao().isEmpty() || hoaDon.getSdtNguoiGiao() == null || hoaDon.getSdtNguoiGiao().isEmpty()) {
//                    return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng cập nhật thông tin người giao hàng"));
//                }
//                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
//                if (!doiTT) {
//                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
//                }
//                return ResponseEntity.ok(Map.of("message", "Đang giao"));
//
//            } else if (trangthai == 5) {
//                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
//                if (!doiTT) {
//                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
//                }
//                return ResponseEntity.ok(Map.of("message", "Đã giao"));
//            } else if (trangthai == 6) {
//                List<HoaDonChiTietOnlCustom> chiTietHoaDon = hoaDonChiTietOnlService.findByHoaDonId(hoaDon);
//                System.out.println("đã hu ho đơn"+trangthai);
//                if(hoaDon.getTrangThai()==4||hoaDon.getTrangThai()==10||hoaDon.getTrangThai()==11){
//                    for (HoaDonChiTietOnlCustom ct : chiTietHoaDon
//                    ) {
//                        chiTietSanPhamService.capNhatSoLuongKhiHuyHoaDon(ct.getIdChiTietSanPham(), ct.getSoLuong());
//                    }
//                }
//                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
//                if (!doiTT) {
//                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
//                }
//                return ResponseEntity.ok(Map.of("message", "Đã hủy hóa đơn"));
//            }else if(trangthai ==11){
//                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
//                if (!doiTT) {
//                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
//                }
//                return ResponseEntity.ok(Map.of("message", "Giao hàng thất bại"));
//            }
//            return ResponseEntity.ok(Map.of("message", "Đổi trạng thái thất bại"));
//
//        } catch (Exception e) {
//            // Ghi lại log lỗi để kiểm tra sau
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
//        }
//
//    }
    @PostMapping("/MuaLai")
    public ResponseEntity<?> muaLaiDonHang(@RequestBody int idHoaDon){

        HoaDon hoaDon = hoaDonService.findById(idHoaDon);
        if(hoaDon==null){
            return ResponseEntity.badRequest().body(Map.of("message","Không tìm thấy hóa đon"));
        }
        HoaDon hoaDon1 = new HoaDon();
        if(hoaDon.getIdNhanVien()!=null){
            hoaDon1.setIdNhanVien(hoaDon.getIdNhanVien());
        }
        if(hoaDon.getIdKhachHang()!=null){
            hoaDon1.setIdKhachHang(hoaDon.getIdKhachHang());
        }
        hoaDon1.setMaHoaDon(hoaDonService.taoMaHoaDon());
        hoaDon1.setThanhTien(0);
        hoaDon1.setGhiChu("Mua lại hoa đơn"+hoaDon.getMaHoaDon());
        hoaDon1.setNgayTao(new Date());
        hoaDon1.setNgaySua(new Date());
        hoaDon1.setTienKhachDua(0);
        hoaDon1.setTongTien(0);
        hoaDon1.setTienThua(0);
        hoaDon1.setTongTienGiam(0);
        hoaDon1.setDonViGiaoHang(hoaDon.getDonViGiaoHang());
        hoaDon1.setPhiShip(hoaDon.getPhiShip());
        hoaDon1.setEmailNguoiNhan(hoaDon.getEmailNguoiNhan());
        hoaDon1.setTenNguoiNhan(hoaDon.getTenNguoiNhan());
        hoaDon1.setSdtNguoiNhan(hoaDon.getSdtNguoiNhan());
        hoaDon1.setDiaChiChiTiet(hoaDon.getDiaChiChiTiet());
        hoaDon1.setTinhThanhPho(hoaDon.getTinhThanhPho());
        hoaDon1.setQuanHuyen(hoaDon.getQuanHuyen());
        hoaDon1.setPhuongXa(hoaDon.getPhuongXa());
        hoaDon1.setLoaiHoaDon(hoaDon.getLoaiHoaDon());
        hoaDon1.setLoaiThanhToan(hoaDon.getLoaiThanhToan());
        hoaDon1.setTrangThai(2);
        hoaDonService.save(hoaDon1);

        float tongTien = 0;
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(hoaDon.getId());
        for (HoaDonChiTiet hd : hoaDonChiTiets) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(hd.getIdChiTietSanPham().getId());
            int soLuongTon = chiTietSanPham.getSoLuong();
            int soLuongCan = hd.getSoLuong();

            if (soLuongTon <= 0) {
                continue;
            }
            HoaDonChiTiet hd1 = new HoaDonChiTiet();
            hd1.setIdHoaDon(hoaDon1);
            hd1.setSoLuong(Math.min(soLuongCan, soLuongTon));
            hd1.setIdChiTietSanPham(hd.getIdChiTietSanPham());
            hd1.setTongTrongLuong(chiTietSanPham.getTrongLuong()*chiTietSanPham.getSoLuong());
            hd1.setDonGia(chiTietSanPham.getGiaBan());
            hd1.setGhiChu("Đơn hàng mua lại từ hóa đơn"+hd1.getIdHoaDon().getMaHoaDon());
            hd1.setNgayTao(new Date());
            hd1.setNgaySua(new Date());
            hd1.setTrangThai(1);
            hoaDonChiTietService.saveHoaDonChiTiet(hd1);
            tongTien += hd1.getSoLuong()*hd1.getDonGia();
        }
        hoaDon1.setTongTien(tongTien);
        hoaDon1.setThanhTien(tongTien+hoaDon1.getPhiShip());
        hoaDonService.save(hoaDon1);
        return ResponseEntity.ok().body(Map.of("message","Mua lại thành công","URL","hoadononlinekhachhang/detailhoadononlinect/"+hoaDon1.getId()));
    }
    @GetMapping("/hienthi")
    public String hienthi(
//                            @PathVariable("id") int id,
                            Model model, @RequestParam(defaultValue = "0") int page) {
        int size = 5;

        KhachHang khachHangService1 = khachHangService.findByEmail(getCurrentUserEmail());

        Pageable pageable = PageRequest.of(page, size);

        Page<HoaDonOnlCustom> listHoaDonTatCaKH = hoaDonOnlService.getHoaDonCustomTatCaKH(pageable, khachHangService1.getId());
        Integer sizeTatCaKH = listHoaDonTatCaKH.getContent().size();
        model.addAttribute("sizeDHKH", sizeTatCaKH);

        Page<HoaDonOnlCustom> listHoaDonDHKH = hoaDonOnlService.getHoaDonCustomDHKH(pageable, khachHangService1.getId());
        Integer sizeDHKH = listHoaDonDHKH.getContent().size();
        model.addAttribute("sizeDHKH", sizeDHKH);

        Page<HoaDonOnlCustom> listHoaDonCXNKH = hoaDonOnlService.getHoaDonOLChoxacnhanKH(pageable, khachHangService1.getId());

        Integer sizecxnKH = listHoaDonCXNKH.getContent().size();
        model.addAttribute("sizecxnKH", sizecxnKH);

        Page<HoaDonOnlCustom> listHoaDonCLHKH = hoaDonOnlService.getHoaDonOLCholayhangKH(pageable, khachHangService1.getId());
        Integer sizeclhKH = listHoaDonCLHKH.getContent().size();
        model.addAttribute("sizeclhKH", sizeclhKH);

        Page<HoaDonOnlCustom> listHoaDonDGKH = hoaDonOnlService.getHoaDonCustomDGKH(pageable, khachHangService1.getId());
        Integer sizedgKH = listHoaDonDGKH.getContent().size();
        model.addAttribute("sizedgKH", sizedgKH);

        Page<HoaDonOnlCustom> listHoaDonHTKH = hoaDonOnlService.getHoaDonCustomHTKH(pageable, khachHangService1.getId());
        Integer sizehtKH = listHoaDonHTKH.getContent().size();
        model.addAttribute("sizehtKH", sizehtKH);



        model.addAttribute("listHoaDonTatCaKH", listHoaDonTatCaKH.getContent());
        model.addAttribute("currentPage", listHoaDonTatCaKH.getNumber());
        model.addAttribute("totalPages", listHoaDonTatCaKH.getTotalPages());

        model.addAttribute("listHoaDonDHKH", listHoaDonDHKH.getContent());
        model.addAttribute("currentPage", listHoaDonDHKH.getNumber());
        model.addAttribute("totalPages", listHoaDonDHKH.getTotalPages());

        model.addAttribute("listHoaDonCXNKH", listHoaDonCXNKH.getContent());
        model.addAttribute("currentPage", listHoaDonCXNKH.getNumber());
        model.addAttribute("totalPages", listHoaDonCXNKH.getTotalPages());

        model.addAttribute("listHoaDonCLHKH", listHoaDonCLHKH.getContent());
        model.addAttribute("currentPage", listHoaDonCLHKH.getNumber());
        model.addAttribute("totalPages", listHoaDonCLHKH.getTotalPages());

        model.addAttribute("listHoaDonDGKH", listHoaDonDGKH.getContent());
        model.addAttribute("currentPage", listHoaDonDGKH.getNumber());
        model.addAttribute("totalPages", listHoaDonDGKH.getTotalPages());

        model.addAttribute("listHoaDonHTKH", listHoaDonHTKH.getContent());
        model.addAttribute("currentPage", listHoaDonHTKH.getNumber());
        model.addAttribute("totalPages", listHoaDonHTKH.getTotalPages());

        return "user/hoadon/listHoaDonKhachHang";
    }

    @PostMapping("/xacnhanhoadoncho/{id}")
    public String xacnhanhoadoncho(@PathVariable int id,
                                   Model model, RedirectAttributes redirectAttributes) {


        HoaDon hd = hoaDonOnlService.detailHD(id);
        hd.setTrangThai(2);
        hoaDonOnlService.updateHoaDon(hd, id);
        redirectAttributes.addFlashAttribute("thanhcong", "thành công");
        return "redirect:/hoadononlinekhachhang/hienthi";
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
        return "redirect:/hoadononlinekhachhang/hienthi";
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
        return "redirect:/hoadononlinekhachhang/hienthi";
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
        return "redirect:/hoadononlinekhachhang/hienthi";
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
            return "redirect:/hoadononlinekhachhang/hienthi";
        }
        List<HoaDonChiTiet> chiTietHoaDon = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(id);
        if (chiTietHoaDon == null || chiTietHoaDon.isEmpty()) {
            System.out.println("Lỗi: Không tìm thấy chi tiết hóa đơn cho ID " + id);
            return "redirect:/hoadononlinekhachhang/hienthi";
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
        return "user/hoadon/detailHoaDonKhachHang";
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

            ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(idChiTietSanPham);

            if(chiTietSanPham.getSoLuong()<=soLuong){
                response.put("success", false);
                response.put("message", "Không thể thêm/cập nhật hóa đơn chi tiết.");
                return ResponseEntity.badRequest().body(response);
            }
            // Tạo đối tượng HoaDonChiTiet
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setIdHoaDon(hoaDon);
            hoaDonChiTiet.setIdChiTietSanPham(chiTietSanPham);
            hoaDonChiTiet.setSoLuong(soLuong);
            hoaDonChiTiet.setDonGia(chiTietSanPham.getGiaBan());
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

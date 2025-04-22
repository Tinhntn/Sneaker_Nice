package poly.edu.sneaker.Controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.*;

import java.lang.reflect.Array;

import org.springframework.web.servlet.config.annotation.RedirectViewControllerRegistration;
import org.springframework.web.servlet.view.RedirectView;
import poly.edu.sneaker.Config.VnPayConfig;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.*;
import poly.edu.sneaker.Service.Implement.VnPay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/gio-hang")
public class GioHangController {
    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    GioHangService gioHangService;
    @Autowired
    GioHangChiTietService gioHangChiTietService;
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    HoaDonChiTietService hoaDonChiTietService;
    @Autowired
    HttpSession httpSession;

    @Autowired
    LichSuTrnngThaiService lichSuTrnngThaiService;
    @Autowired
    KhuyenMaiService khuyenMaiService;
    @Autowired
    private VnPay vnPay;
    @Autowired
    KhachHangService khachHangService;

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }


    @GetMapping("/tim-giam-gia")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> timGiamGia(@RequestParam("maKhuyenMai") String maKhuyenMai) {
        Map<String, Object> response = new HashMap<>();
        KhuyenMai khuyenMai = khuyenMaiService.findByMaKhuyenMai(maKhuyenMai);
        if (khuyenMai != null) {
            response.put("khuyenMai", khuyenMai);
            return ResponseEntity.ok(response);
        }
        response.put("message", "Mã giảm giá không tồn tại");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/ma-giam-gia")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> layMaGiamGia(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String maKm = request.get("maKhuyenMai");
        if (maKm == null || maKm.isEmpty()) {
            response.put("message", "Mã khuyến mãi không hợp lệ");
            return ResponseEntity.badRequest().body(response);
        }
        double tongTien;
        try {
            tongTien = Double.parseDouble(request.get("totalPrice"));
        } catch (NumberFormatException | NullPointerException e) {
            response.put("message", "Tổng tiền không hợp lệ");
            return ResponseEntity.badRequest().body(response);
        }

        KhuyenMai khuyenMai = khuyenMaiService.findByMaKm(maKm);
        if (khuyenMai != null) {
            if (khuyenMai.getDieuKienApDung() > tongTien) {
                response.put("message", "Đơn hàng chưa đủ điều kiện dùng mã giảm giá");
                return ResponseEntity.badRequest().body(response);
            }
            double giaGiam;
            if (khuyenMai.getLoaiKhuyenMai()) { // Giảm giá theo số tiền
                giaGiam = khuyenMai.getGiaTriGiam();
            } else { // Giảm giá theo %
                double giamGiaPhanTram = (tongTien * khuyenMai.getGiaTriGiam()) / 100;
                giaGiam = Math.min(giamGiaPhanTram, khuyenMai.getMucGiamGiaToiDa()); // Không vượt quá mức giảm tối đa
            }

            response.put("message", "Thêm mã khuyến mãi thành công");
            response.put("giaGiam", giaGiam);
            return ResponseEntity.ok(response);
        }

        response.put("message", "Mã khuyến mãi không tồn tại");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/thanh-toan")
    @ResponseBody
    public ResponseEntity<?> thanhToanOnline(@RequestBody Map<String, Object> sanPhamThanhToan) {
        KhachHang khachHang = khachHangService.findByEmail(getCurrentUserEmail());
        GioHang gioHang = gioHangService.findGioHangByIDKH(khachHang.getId());
        try {
            if (sanPhamThanhToan == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Dữ liệu không hợp lệ"));
            }
            if (sanPhamThanhToan.get("selectedIds") == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sản phẩm không hợp lệ"));
            }
            Object selectedIdsRaw = sanPhamThanhToan.get("selectedIds");

            List<Integer> idGHCT = new ArrayList<>();

            if (selectedIdsRaw instanceof List<?>) {
                for (Object idObj : (List<?>) selectedIdsRaw) {
                    try {
                        idGHCT.add(Integer.parseInt(idObj.toString()));
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body(Map.of("message", "ID sản phẩm không hợp lệ"));
                    }
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu selectedIds không hợp lệ"));
            }
            List<GioHangChiTiet> lstGioHangChiTiet = new ArrayList<>();
            for (Integer id : idGHCT) {
                lstGioHangChiTiet.add(gioHangChiTietService.findById(id));
            }
            if (lstGioHangChiTiet.size() == 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Chưa chọn sản phẩm"));
            }
            float tongTien = (float) lstGioHangChiTiet.stream().mapToDouble(GioHangChiTiet::getTongTien).sum();
            float tongTienGiam = 0;
            KhuyenMai khuyenMai = null;
            // Tạo hóa đơn mới
            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaHoaDon(hoaDonService.taoMaHoaDon());
            if (gioHang.getIdKhuyenMai() != null) {
                khuyenMai = khuyenMaiService.findById(gioHang.getIdKhuyenMai().getId());
                if (khuyenMai != null) {
                    if (khuyenMai.getLoaiKhuyenMai()) { // Giảm giá theo số tiền
                        tongTienGiam = khuyenMai.getGiaTriGiam();
                    } else { // Giảm giá theo %
                        double giamGiaPhanTram = (tongTien * khuyenMai.getGiaTriGiam()) / 100;
                        tongTienGiam = (float) Math.min(giamGiaPhanTram, khuyenMai.getMucGiamGiaToiDa()); // Không vượt quá mức giảm tối đa
                    }

                    hoaDon.setIdKhuyenMai(khuyenMai);
                }
            }

            hoaDon.setIdKhachHang(khachHang);
            hoaDon.setTenNguoiNhan((String) sanPhamThanhToan.get("hoVaTen"));
            hoaDon.setSdtNguoiNhan((String) sanPhamThanhToan.get("soDienThoai"));
            hoaDon.setEmailNguoiNhan((String) sanPhamThanhToan.get("email"));
            hoaDon.setTinhThanhPho((String) sanPhamThanhToan.get("province"));
            hoaDon.setQuanHuyen((String) sanPhamThanhToan.get("district"));
            hoaDon.setPhuongXa((String) sanPhamThanhToan.get("ward"));
            hoaDon.setDiaChiChiTiet((String) sanPhamThanhToan.get("diaChiCuThe"));
            hoaDon.setGhiChu((String) sanPhamThanhToan.get("ghiChu"));
            hoaDon.setTienKhachDua(0);
            hoaDon.setLoaiHoaDon(true);
            hoaDon.setTienThua(0);
            hoaDon.setTongTienGiam(tongTienGiam);
            // Kiểm tra và parse giá trị float an toàn
            try {
                hoaDon.setPhiShip(Float.parseFloat(sanPhamThanhToan.get("tienShip").toString()));
                hoaDon.setTongTien(tongTien);
                hoaDon.setThanhTien(tongTien - tongTienGiam);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Lỗi định dạng số tiền");
            }
            hoaDon.setNgayTao(new Date());
            hoaDon.setDonViGiaoHang("Giao hàng tiết kiệm");
            hoaDon.setLoaiHoaDon(true);
            hoaDon.setLoaiThanhToan(true);
            hoaDon.setTrangThai(2);
            // Lưu hóa đơn vào database
            hoaDonService.save(hoaDon);
            if (khuyenMai != null) {
                khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() + 1);
                khuyenMaiService.updateKhuyenMai(khuyenMai, khuyenMai.getId());
            }
            //Cập nhật số lượng khuyến mại sau khi khách hàng thanh toán
            TrangThaiDonHang trangThaiDonHang = new TrangThaiDonHang();
            trangThaiDonHang.setTrangThai(hoaDon.getTrangThai());
            trangThaiDonHang.setGhiChu("Thanh toán khi nhận hàng - Chờ xác nhận");
            trangThaiDonHang.setIdHoaDon(hoaDon);
            trangThaiDonHang.setNgayCapNhat(new Date());
            lichSuTrnngThaiService.saveLichSuTrangThai(trangThaiDonHang);
            // Lấy danh sách sản phẩm trong giỏ hàng


            for (GioHangChiTiet ghct : lstGioHangChiTiet) {
                HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
                hoaDonChiTiet.setIdHoaDon(hoaDonService.findById(hoaDon.getId()));
                hoaDonChiTiet.setSoLuong(ghct.getSoLuong());
                hoaDonChiTiet.setIdChiTietSanPham(ghct.getIdChiTietSanPham());
                hoaDonChiTiet.setTongTrongLuong(ghct.getTongTrongLuong());
                hoaDonChiTiet.setDonGia(ghct.getDonGia());
                hoaDonChiTiet.setNgayTao(new Date());
                hoaDonChiTiet.setTrangThai(2);
                // Lưu chi tiết hóa đơn từ giỏ hàng chi tiết
                hoaDonChiTietService.saveHoaDonChiTiet(hoaDonChiTiet);
                //Xóa giỏ hàng chi tiết khi thanh toán thành công
                gioHangChiTietService.deleteGioHangChitiet(ghct.getId());
            }
            return ResponseEntity.ok(Map.of("message", "Đặt hàng thành công", "redirectUrl", "/gio-hang/checkout-success"));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi khi thanh toán: " + e.getMessage());
        }
    }


    @GetMapping("/vnpay-return")
    public RedirectView vnpayReturn(HttpServletRequest request) {
        // Kiểm tra tính hợp lệ của giao dịch
        int paymentStatus = vnPay.orderReturn(request);

        if (paymentStatus == 1) {
            Map<String, Object> giaoDich = (Map<String, Object>) httpSession.getAttribute("giaoDichTam");
            if (giaoDich == null) {
                return new RedirectView("/gio-hang/checkout-failed"); // Chuyển hướng trực tiếp

            }

            // Lấy thông tin khách hàng
            KhachHang khachHang = khachHangService.findByEmail(getCurrentUserEmail());
            GioHang gioHang = gioHangService.findGioHangByIDKH(khachHang.getId());
            // Lấy ID khuyến mãi an toàn
            Integer idKhuyenMai = (gioHang.getIdKhuyenMai() != null) ? gioHang.getIdKhuyenMai().getId() : null;
            KhuyenMai khuyenMai = (idKhuyenMai != null) ? khuyenMaiService.findById(idKhuyenMai) : null;

            // Tạo hóa đơn mới
            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaHoaDon((String) giaoDich.get("maHoaDon"));
            hoaDon.setIdKhuyenMai(khuyenMai);
            hoaDon.setIdKhachHang(khachHang);
            hoaDon.setTenNguoiNhan((String) giaoDich.get("hoVaTen"));
            hoaDon.setSdtNguoiNhan((String) giaoDich.get("soDienThoai"));
            hoaDon.setEmailNguoiNhan((String) giaoDich.get("email"));
            hoaDon.setTinhThanhPho((String) giaoDich.get("province"));
            hoaDon.setQuanHuyen((String) giaoDich.get("district"));
            hoaDon.setPhuongXa((String) giaoDich.get("ward"));
            hoaDon.setDiaChiChiTiet((String) giaoDich.get("diaChiCuThe"));
            hoaDon.setGhiChu((String) giaoDich.get("ghiChu"));
            try {
                hoaDon.setTienKhachDua(Float.parseFloat(giaoDich.get("thanhTien").toString()));
                hoaDon.setPhiShip(Float.parseFloat(giaoDich.get("tienShip").toString()));
                hoaDon.setTongTien(Float.parseFloat(giaoDich.get("tongTien").toString()));
                hoaDon.setTongTienGiam(Float.parseFloat(giaoDich.get("tongTienGiam").toString()));
                hoaDon.setThanhTien(Float.parseFloat(giaoDich.get("thanhTien").toString()));
            } catch (NumberFormatException e) {
                return new RedirectView("/gio-hang/checkout-failed"); // Chuyển hướng trực tiếp
            }

            hoaDon.setNgayTao(new Date());
            hoaDon.setDonViGiaoHang("Giao hàng tiết kiệm");
            hoaDon.setLoaiHoaDon(true);
            hoaDon.setLoaiThanhToan(false);
            hoaDon.setTrangThai(10);
            hoaDonService.save(hoaDon);
            khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() + 1);
            khuyenMaiService.updateKhuyenMai(khuyenMai, khuyenMai.getId());
            // Lưu lịch sử trạng thái đơn hàng
            TrangThaiDonHang trangThaiDonHang = new TrangThaiDonHang();
            trangThaiDonHang.setTrangThai(10);
            trangThaiDonHang.setGhiChu("Đã thanh toán VNPay - chờ xác nhận");
            trangThaiDonHang.setIdHoaDon(hoaDon);
            trangThaiDonHang.setNgayCapNhat(new Date());
            lichSuTrnngThaiService.saveLichSuTrangThai(trangThaiDonHang);

            // Lấy danh sách sản phẩm
            List<GioHangChiTiet> lstGioHangChiTiet = (List<GioHangChiTiet>) giaoDich.get("lstGioHangChiTiet");
            if (lstGioHangChiTiet == null || lstGioHangChiTiet.isEmpty()) {
                return new RedirectView("/gio-hang/checkout-failed"); // Chuyển hướng trực tiếp
            }

            for (GioHangChiTiet idGioHangChiTiet : lstGioHangChiTiet) {

                // Kiểm tra số lượng sản phẩm trong kho
                ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(idGioHangChiTiet.getIdChiTietSanPham().getId());
                if (chiTietSanPham == null) {
                    return new RedirectView("/gio-hang/checkout-failed"); // Chuyển hướng trực tiếp
                }
                if (chiTietSanPham.getSoLuong() < idGioHangChiTiet.getSoLuong()) {
                    return new RedirectView("/gio-hang/checkout-failed"); // Chuyển hướng trực tiếp
                }

                // Tạo hóa đơn chi tiết
                HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
                hoaDonChiTiet.setIdHoaDon(hoaDon);
                hoaDonChiTiet.setSoLuong(idGioHangChiTiet.getSoLuong());
                hoaDonChiTiet.setIdChiTietSanPham(idGioHangChiTiet.getIdChiTietSanPham());
                hoaDonChiTiet.setTongTrongLuong(idGioHangChiTiet.getTongTrongLuong());
                hoaDonChiTiet.setDonGia(idGioHangChiTiet.getDonGia());
                hoaDonChiTiet.setNgayTao(new Date());
                hoaDonChiTiet.setTrangThai(1);
                hoaDonChiTietService.saveHoaDonChiTiet(hoaDonChiTiet);
                // Xóa sản phẩm khỏi giỏ hàng
                gioHangChiTietService.deleteGioHangChitiet(idGioHangChiTiet.getId());
            }

            // Xóa session sau khi giao dịch hoàn tất
            httpSession.removeAttribute("giaoDichTam");
            httpSession.removeAttribute("khachHangSession");

            return new RedirectView("/gio-hang/checkout-success"); // Chuyển hướng trực tiếp
        } else if (paymentStatus == 0) {
            //giao dich that bai
            return new RedirectView("/gio-hang/checkout-failed"); // Chuyển hướng trực tiếp
        } else {
            //Giao dich khong hop le
            return new RedirectView("/gio-hang/checkout-failed"); // Chuyển hướng trực tiếp
        }
    }

    @GetMapping("/checkout-success")
    public String checkoutSuccess() {
        return "/user/sanpham/checkoutSuccess";
    }

    @PostMapping("/vnpay-payment")
    @ResponseBody
    public ResponseEntity<?> ThanhToanVNPay(@RequestBody Map<String, Object> sanPhamThanhToan,
                                            HttpServletRequest request) {
        KhachHang khachHangSession = khachHangService.findByEmail(getCurrentUserEmail());
        GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangSession.getId());

        // Kiểm tra đăng nhập
        if (khachHangSession == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Bạn chưa đăng nhập"));
        }

        try {

            if (sanPhamThanhToan.get("selectedIds") == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sản phẩm không hợp lệ"));
            }
            Object selectedIdsRaw = sanPhamThanhToan.get("selectedIds");

            List<Integer> idGHCT = new ArrayList<>();

            if (selectedIdsRaw instanceof List<?>) {
                for (Object idObj : (List<?>) selectedIdsRaw) {
                    try {
                        idGHCT.add(Integer.parseInt(idObj.toString()));
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body(Map.of("message", "ID sản phẩm không hợp lệ"));
                    }
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu selectedIds không hợp lệ"));
            }
            List<GioHangChiTiet> lstGioHangChiTiet = new ArrayList<>();
            for (Integer id : idGHCT) {
                lstGioHangChiTiet.add(gioHangChiTietService.findById(id));
            }
            if (lstGioHangChiTiet.size() == 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Chưa chọn sản phẩm"));
            }
            String maHoaDon = hoaDonService.taoMaHoaDon();
            float tongTien = (float) lstGioHangChiTiet.stream().mapToDouble(GioHangChiTiet::getTongTien).sum();
            float tongTienGiam = 0;
            KhuyenMai khuyenMai = null;

            if (gioHang.getIdKhuyenMai() != null) {
                khuyenMai = khuyenMaiService.findById(gioHang.getIdKhuyenMai().getId());
                if (khuyenMai != null) {
                    if (khuyenMai.getLoaiKhuyenMai()) { // Giảm giá theo số tiền
                        tongTienGiam = khuyenMai.getGiaTriGiam();
                    } else { // Giảm giá theo %
                        double giamGiaPhanTram = (tongTien * khuyenMai.getGiaTriGiam()) / 100;
                        tongTienGiam = (float) Math.min(giamGiaPhanTram, khuyenMai.getMucGiamGiaToiDa()); // Không vượt quá mức giảm tối đa
                    }
                }
            }
            int thanhTien = (int) ((int) tongTien - tongTienGiam);
            // Lưu tạm thông tin giao dịch vào session (hoặc Redis, DB nếu cần)
            Map<String, Object> giaoDichTam = new HashMap<>(sanPhamThanhToan);
            giaoDichTam.put("idKhachHang", khachHangSession.getId());
            giaoDichTam.put("maHoaDon", maHoaDon);
            giaoDichTam.put("thanhTien", thanhTien);
            giaoDichTam.put("tongTien", tongTien);
            giaoDichTam.put("tongTienGiam", tongTienGiam);
            giaoDichTam.put("lstGioHangChiTiet", lstGioHangChiTiet);
            httpSession.setAttribute("giaoDichTam", giaoDichTam);
            String orderInfo = URLEncoder.encode("Thanh toan don hang" + maHoaDon, StandardCharsets.UTF_8.toString());
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            // Tạo URL thanh toán
            String vnpayUrl = vnPay.createOrder(thanhTien, orderInfo, baseUrl, request);
            return ResponseEntity.ok(Map.of("redirectUrl", vnpayUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi thanh toán: " + e.getMessage()));
        }
    }

    @GetMapping("/thanh-toan")
    public String thanhToan(Model model) {
        KhachHang khachHangSessiong = khachHangService.findByEmail(getCurrentUserEmail());
        if (khachHangSessiong == null) {
            model.addAttribute("message", "Bạn chưa đăng nhập!");
            return "redirect:/dang-nhap";
        }
        GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangSessiong.getId());
        if (gioHang == null) {
            model.addAttribute("message", "Giỏ hàng của bạn đang trống!");
            return "redirect:/Sneakers_Nice/hienthi";
        }
        ArrayList<GioHangChiTiet> lstGioHangChiTiet = gioHangChiTietService.findByIdGioHang(gioHang.getId());
        if (lstGioHangChiTiet == null || lstGioHangChiTiet.isEmpty()) {
            model.addAttribute("message", "Giỏ hàng của bạn đang trống!");
            return "redirect:/Sneakers_Nice/hienthi";
        }

        double tien = 0;
        for (GioHangChiTiet ghct : lstGioHangChiTiet
        ) {
            tien += ghct.getTongTien();
        }
        double shippingFee = 65000; // Giả sử phí ship cố định
        double tongTien = tien + shippingFee;
        List<KhuyenMai> lstKhuyenMai = khuyenMaiService.getKhuyenMaiByDieuKienGiam((float) tongTien);
        List<KhuyenMai> filterKhuyenMai = lstKhuyenMai.stream()
                .filter(Objects::nonNull) // Lọc các phần tử null
                .sorted(Comparator.comparingDouble((KhuyenMai km) -> {
                    if (km.getLoaiKhuyenMai()) { // Tránh lỗi null
                        return km.getGiaTriGiam();
                    } else {
                        double giamTheoPhanTram = km.getGiaTriGiam() * tongTien / 100;
                        double mucGiamToiDa = km.getMucGiamGiaToiDa();
                        return Math.min(giamTheoPhanTram, mucGiamToiDa);
                    }
                }).reversed())
                .collect(Collectors.toList());
        float tongTienGiam;
        if (filterKhuyenMai.isEmpty()) {
            tongTienGiam = 0;
        } else {
            KhuyenMai km = filterKhuyenMai.get(0);
            if (km != null) {
                if (km.getLoaiKhuyenMai()) {
                    tongTienGiam = km.getGiaTriGiam();
                } else {
                    double t1 = km.getMucGiamGiaToiDa();
                    double t2 = tongTien * km.getGiaTriGiam() / 100;
                    tongTienGiam = (float) Math.min(t1, t2);
                }
            } else {
                tongTienGiam = 0;
            }

        }

        model.addAttribute("lstKhuyenMai", filterKhuyenMai);
        model.addAttribute("giamGia", tongTienGiam);
        model.addAttribute("khachHang", khachHangSessiong);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("totalPrice", tongTien);
        model.addAttribute("lstGioHangChiTiet", lstGioHangChiTiet);
        return "/user/sanpham/checkout";
    }

    @GetMapping("/changeChoice")
    @ResponseBody
    public ResponseEntity<?> changeChoice(@RequestParam("tongTien") int tongTien, Model model) {
        List<KhuyenMai> lstKhuyenMai = khuyenMaiService.getKhuyenMaiByDieuKienGiam((float) tongTien);
        List<KhuyenMai> filterKhuyenMai = lstKhuyenMai.stream()
                .filter(Objects::nonNull) // Lọc các phần tử null
                .sorted(Comparator.comparingDouble((KhuyenMai km) -> {
                    if (km.getLoaiKhuyenMai()) { // Tránh lỗi null
                        return km.getGiaTriGiam();
                    } else {
                        double giamTheoPhanTram = km.getGiaTriGiam() * tongTien / 100;
                        double mucGiamToiDa = km.getMucGiamGiaToiDa();
                        return Math.min(giamTheoPhanTram, mucGiamToiDa);
                    }
                }).reversed())
                .collect(Collectors.toList());
        float tongTienGiam;

        if (filterKhuyenMai.isEmpty()) {
            tongTienGiam = 0;
        } else {

            KhuyenMai km = filterKhuyenMai.get(0);
            if (km != null) {
                if (km.getLoaiKhuyenMai()) {
                    tongTienGiam = km.getGiaTriGiam();
                } else {
                    double t1 = km.getMucGiamGiaToiDa();
                    double t2 = tongTien * km.getGiaTriGiam() / 100;
                    tongTienGiam = (float) Math.min(t1, t2);
                }
            } else {
                tongTienGiam = 0;
            }

        }
        model.addAttribute("lstKhuyenMai", filterKhuyenMai);
        return ResponseEntity.ok(Map.of(
                "tongTienGiam", tongTienGiam,
                "filterKhuyenMai", filterKhuyenMai
        ));
    }

    @PostMapping("/addVoucher")
    @ResponseBody
    public ResponseEntity<?> addVoucher(@RequestBody Map<String, Object> Object) {
        if (Object == null) {
            return ResponseEntity.badRequest().body("Voucher is null");
        }
        if (Object.get("selectedIds") == null) {
            return ResponseEntity.badRequest().body("Chưa có sản phẩm được chọn");
        }
        KhuyenMai khuyenMai = khuyenMaiService.findById(Integer.parseInt(String.valueOf(Object.get("idKhuyenMai"))));
        if (khuyenMai == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã khuyến mãi đã hết"));
        }
        try {

            float tongTien = Float.parseFloat(Object.get("tongTien").toString());
            if (tongTien < khuyenMai.getDieuKienApDung()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Đơn hàng của bạn không đủ điều kiện"));
            }
            List<Integer> lstIdProductsChoice = (List<Integer>) Object.get("selectedIds");
            float tongTienSanPhamDaChon = 0;
            for (Integer id : lstIdProductsChoice) {
                GioHangChiTiet gioHangChiTiet = gioHangChiTietService.findById(id);
                tongTienSanPhamDaChon += gioHangChiTiet.getTongTien();
            }
            if (tongTienSanPhamDaChon < khuyenMai.getDieuKienApDung()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Đơn hàng của bạn không đủ điểu kiện"));
            }
            float tongTienGiam;
            if (khuyenMai.getLoaiKhuyenMai()) {
                tongTienGiam = khuyenMai.getGiaTriGiam();
            } else {
                double t1 = khuyenMai.getMucGiamGiaToiDa();
                double t2 = tongTien * khuyenMai.getGiaTriGiam() / 100;
                tongTienGiam = (float) Math.min(t1, t2);
            }
            GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangService.findByEmail(getCurrentUserEmail()).getId());
            gioHang.setIdKhuyenMai(khuyenMai);
            gioHangService.save(gioHang);
            return ResponseEntity.ok(Map.of("tongTienGiam", tongTienGiam));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi dùng voucher"));
    }

    @GetMapping()
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getGioHang(Model model) {
        KhachHang khachHangSession = khachHangService.findByEmail(getCurrentUserEmail());
        if (khachHangSession == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Bạn chưa đăng nhập!"));
        }
        GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangSession.getId());
        if (gioHang == null) {
            return ResponseEntity.ok(Map.of("items", new ArrayList<>(), "totalPrice", 0));
        }

        List<GioHangChiTiet> lstGioHangChiTiet = gioHangChiTietService.findByIdGioHang(gioHang.getId());
        if (lstGioHangChiTiet == null || lstGioHangChiTiet.isEmpty()) {
            return ResponseEntity.ok(Map.of("items", new ArrayList<>(), "totalPrice", 0));
        }

        List<Map<String, Object>> items = lstGioHangChiTiet.stream()
                .filter(Objects::nonNull) // Bỏ qua các phần tử null
                .map(ghct -> {
                    Map<String, Object> map = new HashMap<>();
                    ChiTietSanPham chiTietSanPham = ghct.getIdChiTietSanPham();
                    if (chiTietSanPham == null) return map; // Trả về Map rỗng nếu dữ liệu bị thiếu
                    boolean isOutOfStock = chiTietSanPham.getSoLuong() < ghct.getSoLuong() && chiTietSanPham.getSoLuong() > 0;
                    if (isOutOfStock) {
                        ghct.setSoLuong(chiTietSanPham.getSoLuong());
                        gioHangChiTietService.saveGioHangChitiet(ghct);
                    }
                    boolean hetHang = chiTietSanPham.getSoLuong() < 0;
                    map.put("tenSanPham", Optional.ofNullable(ghct.getIdChiTietSanPham().getIdSanPham())
                            .map(SanPham::getTenSanPham).orElse("N/A"));
                    map.put("mauSac", Optional.ofNullable(ghct.getIdChiTietSanPham().getIdMauSac())
                            .map(MauSac::getTenMauSac).orElse("N/A"));
                    map.put("size", Optional.ofNullable(ghct.getIdChiTietSanPham().getIdSize())
                            .map(Size::getTenSize).orElse("N/A"));
                    map.put("gia", Optional.ofNullable(ghct.getIdChiTietSanPham().getGiaBan()).orElse(0f));
                    map.put("idGioHangChiTiet", ghct.getId());
                    map.put("soLuong", Optional.ofNullable(ghct.getSoLuong()).orElse(0));
                    map.put("tongTien", Optional.ofNullable(ghct.getTongTien()).orElse(0f));
                    map.put("hinhAnh", Optional.ofNullable(ghct.getIdChiTietSanPham().getHinhAnh())
                            .orElse("Không có ảnh"));
                    map.put("isOutOfStock", hetHang); // Cờ báo hết hàng
                    return map;
                })
                .collect(Collectors.toList());

        double totalPrice = items.stream()
                .filter(i -> !(boolean) i.get("isOutOfStock")) // Chỉ tính tiền cho sản phẩm còn hàng
                .mapToDouble(i -> ((Number) i.get("gia")).doubleValue() * ((Number) i.get("soLuong")).intValue())
                .sum();

        return ResponseEntity.ok(Map.of("items", items, "totalPrice", totalPrice));
    }

    @GetMapping("/so-luong")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getSoLuongGioHang() {
        KhachHang khachHang = khachHangService.findByEmail(getCurrentUserEmail());
        int soLuongSanPham = 0;
        if (khachHang != null) {
            GioHang gh = gioHangService.findGioHangByIDKH(khachHang.getId());
            if (gh != null) {
                List<GioHangChiTiet> lstGHCT = gioHangChiTietService.findByIdGioHang(gh.getId());
                for (GioHangChiTiet ghct : lstGHCT) {
                    soLuongSanPham += ghct.getSoLuong();
                }
            }
        }
        Map<String, Integer> response = new HashMap<>();
        response.put("soLuong", soLuongSanPham);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON) // Đảm bảo trả về JSON
                .body(response);
    }

    @PostMapping("/them-vao-gio-hang")
    public ResponseEntity<?> themSanPhamVaoGioHang(@RequestBody Map<String, Object> sanPhamChon) {
        if (getCurrentUserEmail().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Bạn cần đăng nhập để mua sắm"));
        }
        KhachHang khachHang = khachHangService.findByEmail(getCurrentUserEmail());
        if (khachHang != null) {
            try {
                int soLuong = converToInt(sanPhamChon.get("soLuong"));
                int idSanPham = converToInt(sanPhamChon.get("idSanPham"));
                int idSize = converToInt(sanPhamChon.get("idSize"));
                int idMauSac = converToInt(sanPhamChon.get("idMauSac"));
                ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findCTSPByIdSPAndIdMauSacAndIdSize(idSanPham,
                        idSize, idMauSac);

                if (chiTietSanPham == null) {
                    return ResponseEntity.badRequest()
                            .body(Collections.singletonMap("message", "Sản phẩm không còn hoạt động"));
                } else if (chiTietSanPham.getSoLuong() <= 0) {
                    return ResponseEntity.badRequest()
                            .body(Collections.singletonMap("message", "Sản phẩm đã hết hàng"));
                } else if (chiTietSanPham.getSoLuong() < soLuong) {
                    return ResponseEntity.badRequest()
                            .body(Collections.singletonMap("message", "Sản phẩm trong kho không đủ"));
                }
                GioHangChiTiet ghct = gioHangChiTietService.findByIdGioHangAndIDCTSP(
                        gioHangService.findGioHangByIDKH(khachHang.getId()).getId(), chiTietSanPham.getId());
                if (ghct != null) {
                    int tongSoLuongSanPhamTrongGioHang = ghct.getSoLuong() + soLuong;
                    if (tongSoLuongSanPhamTrongGioHang > chiTietSanPham.getSoLuong()) {
                        return ResponseEntity.badRequest()
                                .body(Collections.singletonMap("message", "Số lượng sản phẩm trong kho không đủ"));
                    }
                    float tongTien = 0;
                    ArrayList<GioHangChiTiet> lstGHCT = gioHangChiTietService.findByIdGioHang(ghct.getIdGioHang().getId());
                    for (GioHangChiTiet gh : lstGHCT
                    ) {
                        tongTien += gh.getTongTien();
                    }

                    if (tongTien + chiTietSanPham.getGiaBan() * soLuong > 20000000) {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Đơn hàng vượt quá 20 triệu vui lòng liên hệ để được hỗ trợ"));
                    }
                    ghct.setSoLuong(ghct.getSoLuong() + soLuong);
                    ghct.setTongTrongLuong((int) chiTietSanPham.getTrongLuong() * ghct.getSoLuong());
                    ghct.setTongTien(ghct.getDonGia() * ghct.getSoLuong());
                    gioHangChiTietService.saveGioHangChitiet(ghct);
                    return ResponseEntity.ok()
                            .body(Collections.singletonMap("Thêm sản phẩm vào giỏ hàng thành công", true));
                }
                float tongTien = chiTietSanPham.getGiaBan() * soLuong;
                ArrayList<GioHangChiTiet> lstGHCT = gioHangChiTietService.findByIdGioHang(gioHangService.findGioHangByIDKH(khachHang.getId()).getId());
                if (lstGHCT != null) {
                    for (GioHangChiTiet gh : lstGHCT
                    ) {
                        tongTien += gh.getTongTien();
                    }
                    if (tongTien > 20000000) {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Đơn hàng vượt quá 20 triệu vui lòng liên hệ để được hỗ trợ"));
                    }
                }
                GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();
                gioHangChiTiet.setIdGioHang(gioHangService.findGioHangByIDKH(khachHang.getId()));
                gioHangChiTiet.setIdChiTietSanPham(chiTietSanPham);
                gioHangChiTiet.setSoLuong(soLuong);
                gioHangChiTiet.setDonGia(chiTietSanPham.getGiaBan());
                gioHangChiTiet.setTongTien(gioHangChiTiet.getDonGia() * soLuong);
                gioHangChiTiet.setTongTrongLuong(chiTietSanPham.getSoLuong() * chiTietSanPham.getSoLuong());
                gioHangChiTiet.setNgayTao(new Date());
                gioHangChiTiet.setTrangThai(true);
                gioHangChiTietService.saveGioHangChitiet(gioHangChiTiet);
                return ResponseEntity.ok(Collections.singletonMap("Thêm sản phẩm vào giỏ hàng thành công", true));

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("message", "Lỗi khi cập nhật sản phẩm!"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Bạn cần đăng nhập để mua sắm"));
        }

    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeSanPham(@PathVariable int id) {

        gioHangChiTietService.deleteGioHangChitiet(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Xóa sản phẩm thành công"));
    }

    @PutMapping("/capNhatSoLuongGioHang/{idGHCT}")
    public ResponseEntity<?> capNhatSoLuongGioHang(@PathVariable int idGHCT, @RequestParam int soLuong) {
        GioHangChiTiet gioHangChiTiet = gioHangChiTietService.findById(idGHCT);
        ArrayList<GioHangChiTiet> lstGHCT = gioHangChiTietService.findByIdGioHang(gioHangChiTiet.getIdGioHang().getId());
        int tongTienGioHang = 0;
        for (GioHangChiTiet gh : lstGHCT) {
            if (gh == gioHangChiTiet) {
                tongTienGioHang += gh.getDonGia() * soLuong;
            }
            tongTienGioHang += gh.getTongTien();
        }
        System.out.println("" + tongTienGioHang);
        if (tongTienGioHang > 20000000) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Đơn hàng vượt quá 20 triệu"));
        }
        if (gioHangChiTiet != null) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService
                    .findById(gioHangChiTiet.getIdChiTietSanPham().getId());

            if (soLuong < 1) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "Số lượng sản phẩm phải lớn hơn 0"));
            }
            if (chiTietSanPham.getSoLuong() < soLuong) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "Số lượng sản phẩm trong kho không đủ"));
            } else if (gioHangChiTiet.getDonGia() * soLuong > 20000000) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Đơn hàng vượt quá 20 triệu, liên hệ để được hỗ trợ"));
            } else {

                gioHangChiTiet.setSoLuong(soLuong);
                gioHangChiTiet.setTongTien(gioHangChiTiet.getDonGia() * soLuong);
                gioHangChiTietService.saveGioHangChitiet(gioHangChiTiet);
                return ResponseEntity.ok(Collections.singletonMap("success", true));
            }
        }
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Cập nhật sản phẩm thất bại"));
    }


    private int converToInt(Object object) {
        if (object instanceof Integer) {
            return (Integer) object;
        }
        return Integer.parseInt(object.toString());
    }
}

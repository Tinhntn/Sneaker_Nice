package poly.edu.sneaker.Controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import poly.edu.sneaker.Response.GiaoHangTietKiemResponse;
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
import java.time.LocalDate;
import java.time.ZoneId;
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
    @Autowired
    GiaoHangService giaoHangService;
    @Autowired
    HttpSession session;
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }

    @PostMapping("/tinhPhiShip")
    public ResponseEntity<?> calculateFee(@RequestBody Map<String, Object> payload) {
        try {
            GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangService.findByEmail(getCurrentUserEmail()).getId());

            String pickProvince = (String) payload.get("pickProvince");
            String pickDistrict = (String) payload.get("pickDistrict");
            String toProvince = (String) payload.get("toProvince");
            String toDistrict = (String) payload.get("toDistrict");


            // Xử lý weight
            int weight = 0;
            if (payload.containsKey("weight")) {
                try {
                    weight = Integer.parseInt(payload.get("weight").toString());
                } catch (NumberFormatException e) {
                    // Nếu parse weight thất bại, sẽ tính lại từ selectedIds
                }
            }

            // Xử lý selectedIds
            List<Integer> idGHCT = new ArrayList<>();
            Object selectedIdsRaw = payload.get("selectedIds");
            if (selectedIdsRaw instanceof List<?>) {
                for (Object idObj : (List<?>) selectedIdsRaw) {
                    try {
                        idGHCT.add(Integer.parseInt(idObj.toString()));
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body(Map.of("message", "ID sản phẩm không hợp lệ"));
                    }
                }
            }

            // Nếu có selectedIds và weight = 0 hoặc không parse được từ payload
            if (!idGHCT.isEmpty() && weight == 0) {
                List<GioHangChiTiet> lstGHCT = gioHangChiTietService.findByIdGioHang(gioHang.getId())
                        .stream()
                        .filter(item -> idGHCT.contains(item.getId()))
                        .collect(Collectors.toList());

                if (!lstGHCT.isEmpty()) {
                    weight = lstGHCT.stream().mapToInt(GioHangChiTiet::getTongTrongLuong).sum();
                }
            }
            GiaoHangTietKiemResponse response = giaoHangService.getGiaoHangTietKiem(
                    pickProvince, pickDistrict, toProvince, toDistrict, weight);
            session.setAttribute("phiVanChuyen",response.getFee());
            session.setAttribute("thoiGianDuKien",response.getEstimatedDeliveryTime() );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi tính phí: " + e.getMessage()));
        }
    }
    @GetMapping("/tim-giam-gia")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> timGiamGia(@RequestParam("maKhuyenMai") String maKhuyenMai) {
        Map<String, Object> response = new HashMap<>();
        KhuyenMai khuyenMai = khuyenMaiService.findByMaKhuyenMai(maKhuyenMai);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(khuyenMai.getNgayKetThuc());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date ngayKetThuc = calendar.getTime();
        if (khuyenMai != null) {
            if (new Date().after(ngayKetThuc)) {
                khuyenMai.setTrangThai(false);
                khuyenMaiService.updateKhuyenMai(khuyenMai, khuyenMai.getId());
                response.put("message", "Mã khuyến mãi không còn hoạt động");
                return ResponseEntity.badRequest().body(response);
            }
            if (khuyenMai.getTrangThai() || khuyenMai.getSoLuong() - khuyenMai.getDaSuDung() > 0) {
                response.put("khuyenMai", khuyenMai);
                return ResponseEntity.ok(response);
            }
            response.put("message", "Mã giảm giá không còn hoạt động");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "Mã giảm giá không tồn tại");
        return ResponseEntity.badRequest().body(response);
    }

    //Thanh toán khi nhận hàng
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
            if (idGHCT.size() == 0) {
                return ResponseEntity.badRequest().body((Map.of("message", "Sản phẩm không còn trong giỏ hàng")));
            }
            List<GioHangChiTiet> lstGioHangChiTiet = new ArrayList<>();
            for (Integer id : idGHCT) {
                lstGioHangChiTiet.add(gioHangChiTietService.findById(id));
            }
            if (lstGioHangChiTiet.size() == 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Chưa chọn sản phẩm", "size", lstGioHangChiTiet.size()));
            }
            //Kiểm tra giá của sản phẩm có thay đổi không
            boolean isChaged = false;
            //kiểm tra trạng thái giỏ hàng có còn giỏ hàng true không

            StringBuilder thayDoi = new StringBuilder();
            for (GioHangChiTiet gh : lstGioHangChiTiet) {
                if (Math.abs(gh.getIdChiTietSanPham().getGiaBan() - gh.getDonGia()) > 0.001f) {
                    thayDoi.append("").append(gh.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                    isChaged = true;
                } else if (gh.getSoLuong() > gh.getIdChiTietSanPham().getSoLuong()) {
                    thayDoi.append("").append(gh.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                    isChaged = true;
                } else if (!gh.getTrangThai() || !gh.getIdChiTietSanPham().getTrangThai() || !gh.getIdChiTietSanPham().getIdSanPham().getTrangThai()) {
                    thayDoi.append("").append(gh.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                }
            }
            if (isChaged) {
                tinhLaiGiaTien(gioHang);
            }
            if (!thayDoi.toString().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sản phẩm " + thayDoi.toString() + "có thay đổi thay đổi"));
            }
            float tongTien = (float) lstGioHangChiTiet.stream().filter(item -> item.getTrangThai() && item.getIdChiTietSanPham().getTrangThai() && item.getIdChiTietSanPham().getIdSanPham().getTrangThai()).mapToDouble(GioHangChiTiet::getTongTien).sum();
            if (tongTien > 20000000) {
                return ResponseEntity.badRequest().body(Map.of("message", "Đơn hàng vượt quá 20 triệu"));
            }
            float tongTienGiam = 0;
            KhuyenMai khuyenMai = null;
            // Tạo hóa đơn mới
            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaHoaDon(hoaDonService.taoMaHoaDon());
            if (gioHang.getIdKhuyenMai() != null) {
                khuyenMai = khuyenMaiService.findById(gioHang.getIdKhuyenMai().getId());
                if (khuyenMai != null) {
                    int checkKM = checkKhuyenMai(khuyenMai);
                    if (checkKM == 1) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Mã khuyến mãi không còn hoạt động"));
                    } else if (checkKM == 2) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Mã khuyến mãi đã được sử dụng hết"));
                    }
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
            String thoiGianDuKien = (String) sanPhamThanhToan.get("thoiGianDuKien")+"ngày";
            hoaDon.setPhuongXa((String) sanPhamThanhToan.get("ward"));
            hoaDon.setDiaChiChiTiet((String) sanPhamThanhToan.get("diaChiCuThe"));
            hoaDon.setGhiChu((String) sanPhamThanhToan.get("ghiChu")+" "+thoiGianDuKien);
            hoaDon.setTienKhachDua(0);
            hoaDon.setLoaiHoaDon(true);
            hoaDon.setTienThua(0);
            hoaDon.setTongTienGiam(tongTienGiam);
            Object feeObj = session.getAttribute("phiVanChuyen");
            float shippingFee = 0f;

            if (feeObj != null) {
                if (feeObj instanceof Number) {
                    shippingFee = ((Number) feeObj).floatValue(); // Hỗ trợ Integer, Double, Float...
                } else if (feeObj instanceof String) {
                    try {
                        shippingFee = Float.parseFloat((String) feeObj);
                    } catch (NumberFormatException e) {
                        System.out.println("Không thể chuyển String sang float: " + feeObj);
                    }
                }
            }

            // Kiểm tra và parse giá trị float an toàn
            try {
                hoaDon.setPhiShip(shippingFee);
                hoaDon.setTongTien(tongTien);
                hoaDon.setThanhTien(tongTien + hoaDon.getPhiShip() - tongTienGiam);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Lỗi định dạng số tiền");
            }
            hoaDon.setNgayTao(new Date());
            hoaDon.setDonViGiaoHang("Giao hàng tiết kiệm");
            hoaDon.setLoaiHoaDon(true);
            //thanh toán khi nhan hang de loai thanh toan la false
            hoaDon.setLoaiThanhToan(false);
            hoaDon.setTrangThai(2);
            hoaDon.setGhiChu("Mua hàng ngày " + new Date() + " thanh toán khi nhận hàng"+" dự kiến giao hàng trong vòng"+(String) session.getAttribute("thoiGianDuKien"+" ngày"));
            // Lưu hóa đơn vào database
            hoaDonService.save(hoaDon);
            //Cập nhật số lượng khuyến mại sau khi khách hàng thanh toán
            if (khuyenMai != null) {
                khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() + 1);
                khuyenMaiService.updateKhuyenMai(khuyenMai, khuyenMai.getId());

            }
            //Cập nhật thông tin vao lich su don hang
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
            //gửi mail thông báo đặt hàng thành cống
            try{
                boolean guiMail = khachHangService.ThongBao(hoaDon,2,hoaDon.getGhiChu());
                System.out.println(guiMail);
            }catch (Exception e){
                e.printStackTrace();
            }
            session.removeAttribute("phiVanChuyen");
            session.removeAttribute("thoiGianDuKien");
            return ResponseEntity.ok(Map.of("message", "Đặt hàng thành công", "redirectUrl", "/gio-hang/checkout-success"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Thanh toán thất bại");
        }
    }


    @GetMapping("/vnpay-return")
    public RedirectView vnpayReturn(HttpServletRequest request, RedirectAttributes redirectAttribute) {
        // Kiểm tra tính hợp lệ của giao dịch
        // Lấy thông tin khách hàng
        KhachHang khachHang = khachHangService.findByEmail(getCurrentUserEmail());
        GioHang gioHang = gioHangService.findGioHangByIDKH(khachHang.getId());
        Map<String, Object> giaoDich = (Map<String, Object>) httpSession.getAttribute("giaoDichTam");
        if (giaoDich == null) {
            // Xóa session sau khi giao dịch hoàn tất
            httpSession.removeAttribute("giaoDichTam");
            httpSession.removeAttribute("khachHangSession");
            httpSession.setAttribute("paymentStatus", "success");
            return new RedirectView("/gio-hang/checkout-success");
        }
        //Toan bo gio hang chi tiet
        List<GioHangChiTiet> lstGioHangThucTe = gioHangChiTietService.findByIdGioHang(gioHang.getId());
        //Gio hang chi tiet duoc lay de thanh toan
        List<GioHangChiTiet> lstGioHangChiTiet = (List<GioHangChiTiet>) giaoDich.get("lstGioHangChiTiet");
        if (lstGioHangChiTiet == null || lstGioHangChiTiet.isEmpty()) {
            request.setAttribute("paymentStatus", "failed");
            redirectAttribute.addFlashAttribute("error", "Lỗi xử lý đơn hàng");
            return new RedirectView("/gio-hang/checkout-failed");
        }
        boolean isChaged = false;
        StringBuilder thayDoi = new StringBuilder();
        for (GioHangChiTiet ghtt : lstGioHangThucTe) {
            for (GioHangChiTiet gh : lstGioHangChiTiet) {
                if (ghtt.getId() == gh.getId()) {
                    if (Math.abs(ghtt.getIdChiTietSanPham().getGiaBan() - ghtt.getDonGia()) > 0.001f) {
                        thayDoi.append("").append(ghtt.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                        isChaged = true;
                    } else if (ghtt.getSoLuong() > ghtt.getIdChiTietSanPham().getSoLuong()) {
                        thayDoi.append(" ").append(ghtt.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                        isChaged = true;
                    }else  if(!ghtt.getTrangThai()||!ghtt.getIdChiTietSanPham().getTrangThai()||!ghtt.getIdChiTietSanPham().getIdSanPham().getTrangThai()){
                        thayDoi.append("").append(ghtt.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                    }
                }
            }
        }
        if (isChaged) {
            // Cập nhật lại thông tin giỏ hàng và thông báo người dùng
            tinhLaiGiaTien(gioHang);
            // Xóa giao dịch tạm nếu muốn reset
            httpSession.removeAttribute("giaoDichTam");
            httpSession.removeAttribute("khachHangSession");
            // Có thể thêm flash attribute hoặc query param để thông báo lý do
            request.setAttribute("paymentStatus", "failed");
            redirectAttribute.addFlashAttribute("error", "Sản phẩm " + thayDoi + "thay đổi giá hoặc không còn hoạt động");
            return new RedirectView("/gio-hang/checkout-failed");
        }

        int paymentStatus = vnPay.orderReturn(request);
        if (paymentStatus == 1) {
            // Lấy danh sách sản phẩm
            Integer idKhuyenMai = (gioHang.getIdKhuyenMai() != null) ? gioHang.getIdKhuyenMai().getId() : null;
            KhuyenMai khuyenMai = (idKhuyenMai != null) ? khuyenMaiService.findById(idKhuyenMai) : null;
            int checkKM = checkKhuyenMai(khuyenMai);
            if (checkKM == 1 ) {
                //khuyen mai khong con hoat dong
                request.setAttribute("paymentStatus", "failed");
                redirectAttribute.addFlashAttribute("error", "Mã khuyến mãi không còn hoạt động");
                return new RedirectView("/gio-hang/checkout-failed");
            } else if (checkKM == 2) {
                //khuyen mai da duoc su dung het
                request.setAttribute("paymentStatus", "failed");
                redirectAttribute.addFlashAttribute("error", "Mã khuyến mãi đã được sử dụng hết");
                return new RedirectView("/gio-hang/checkout-failed");
            }
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
            hoaDon.setGhiChu((String) giaoDich.get("ghiChu")+" dự kiến giao hàng trong vòng"+(String) session.getAttribute("thoiGianDuKien"+" ngày"));
            try {
                Object feeObj = session.getAttribute("phiVanChuyen");
                float shippingFee = 0f;

                if (feeObj != null) {
                    if (feeObj instanceof Number) {
                        shippingFee = ((Number) feeObj).floatValue(); // Hỗ trợ Integer, Double, Float...
                    } else if (feeObj instanceof String) {
                        try {
                            shippingFee = Float.parseFloat((String) feeObj);
                        } catch (NumberFormatException e) {
                            System.out.println("Không thể chuyển String sang float: " + feeObj);
                        }
                    }
                }

                hoaDon.setTienKhachDua(Float.parseFloat(giaoDich.get("thanhTien").toString()));
                hoaDon.setPhiShip(shippingFee);
                hoaDon.setTongTien(Float.parseFloat(giaoDich.get("tongTien").toString()));
                hoaDon.setTongTienGiam(Float.parseFloat(giaoDich.get("tongTienGiam").toString()));
                hoaDon.setThanhTien(Float.parseFloat(giaoDich.get("thanhTien").toString()));
            } catch (NumberFormatException e) {
                httpSession.setAttribute("paymentStatus", "failed");
                redirectAttribute.addFlashAttribute("error", "Lỗi xử lý đơn hàng");
                return new RedirectView("/gio-hang/checkout-failed");
            }
            hoaDon.setNgayTao(new Date());
            hoaDon.setDonViGiaoHang("Giao hàng tiết kiệm");
            hoaDon.setLoaiHoaDon(true);
            //Thanh toán bằng vnpay để trạng thái là true
            hoaDon.setLoaiThanhToan(true);
            hoaDon.setTrangThai(2);
            hoaDon.setGhiChu("Đơn hàng ngày " + new Date() + "đã thanh toán vnpay");
            hoaDonService.save(hoaDon);
            //cap nhat so luong khuyen mai da su dung sau khi lưu hóa đơn thành công
            if(khuyenMai!=null){
                khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() + 1);
                khuyenMaiService.updateKhuyenMai(khuyenMai, khuyenMai.getId());

            }
                        // Lưu lịch sử trạng thái đơn hàng
            TrangThaiDonHang trangThaiDonHang = new TrangThaiDonHang();
            trangThaiDonHang.setTrangThai(2);
            trangThaiDonHang.setGhiChu("Đã thanh toán VNPay - chờ xác nhận");
            trangThaiDonHang.setIdHoaDon(hoaDon);
            trangThaiDonHang.setNgayCapNhat(new Date());
            lichSuTrnngThaiService.saveLichSuTrangThai(trangThaiDonHang);
            for (GioHangChiTiet idGioHangChiTiet : lstGioHangChiTiet) {
                // Kiểm tra số lượng sản phẩm trong kho
                ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(idGioHangChiTiet.getIdChiTietSanPham().getId());
                if (chiTietSanPham == null) {
                    request.setAttribute("paymentStatus", "failed");
                    redirectAttribute.addFlashAttribute("error", "Chi tiết sản phẩm trong giỏ hàng không tồn tại");
                    return new RedirectView("/gio-hang/checkout-failed");
                }
                if (chiTietSanPham.getSoLuong() < idGioHangChiTiet.getSoLuong()) {
                    request.setAttribute("paymentStatus", "failed");
                    redirectAttribute.addFlashAttribute("error", "Số lượng sản phẩm trong kho không đủ");
                    return new RedirectView("/gio-hang/checkout-failed");
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
            session.removeAttribute("phiVanChuyen");
            session.removeAttribute("thoiGianDuKien");
            httpSession.setAttribute("paymentStatus", "success");
            try{
                boolean guiMail = khachHangService.ThongBao(hoaDon,2,hoaDon.getGhiChu());
                System.out.println(guiMail);
            }catch (Exception e){
                e.printStackTrace();
            }
            return new RedirectView("/gio-hang/checkout-success");
        } else if (paymentStatus == 0) {
            //giao dich that bai
            request.setAttribute("paymentStatus", "failed");
            redirectAttribute.addFlashAttribute("error", "Giao dịch thất bại");
            return new RedirectView("/gio-hang/checkout-failed");
        } else {
            //Giao dich khong hop le
            request.setAttribute("paymentStatus", "failed");
            redirectAttribute.addFlashAttribute("error", "Giao dịch không hợp lệ");
            return new RedirectView("/gio-hang/checkout-failed");
        }
    }

    @GetMapping("/checkout-success")
    public String checkoutSuccess(Model model) {
        if (model.containsAttribute("error")) {
            model.addAttribute("errorMessage", model.getAttribute("error"));
        }
        return "/user/sanpham/checkoutSuccess";
    }

    @GetMapping("/checkout-failed")
    public String checkoutFailed(Model model, RedirectAttributes redirectAttributes) {
        // Lấy thông báo lỗi từ flash attribute
        if (model.containsAttribute("error")) {
            model.addAttribute("errorMessage", model.getAttribute("error"));
        }
        return "redirect:/gio-hang/thanh-toan"; // Hoặc return trực tiếp template nếu không muốn redirect
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
            boolean isChaged = false;
            StringBuilder thayDoi = new StringBuilder();
            for (GioHangChiTiet gh : lstGioHangChiTiet) {
                if (Math.abs(gh.getIdChiTietSanPham().getGiaBan() - gh.getDonGia()) > 0.001f) {
                    thayDoi.append("").append(gh.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                    isChaged = true;
                } else if (gh.getSoLuong() > gh.getIdChiTietSanPham().getSoLuong()) {
                    thayDoi.append(" ").append(gh.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                    isChaged = true;
                }else  if(!gh.getTrangThai()||!gh.getIdChiTietSanPham().getTrangThai()||!gh.getIdChiTietSanPham().getIdSanPham().getTrangThai()){
                    thayDoi.append("").append(gh.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                }
            }
            if (isChaged) {
                tinhLaiGiaTien(gioHang);
            }
            if (!thayDoi.toString().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Giá của " + thayDoi.toString() + " thay đổi", "load", true));
            }
            //call api để tính phí ship
            Object feeObj = session.getAttribute("phiVanChuyen");
            float shippingFee = 0f;

            if (feeObj != null) {
                if (feeObj instanceof Number) {
                    shippingFee = ((Number) feeObj).floatValue(); // Hỗ trợ Integer, Double, Float...
                } else if (feeObj instanceof String) {
                    try {
                        shippingFee = Float.parseFloat((String) feeObj);
                    } catch (NumberFormatException e) {
                        System.out.println("Không thể chuyển String sang float: " + feeObj);
                    }
                }
            }

            String maHoaDon = hoaDonService.taoMaHoaDon();
            float tongTien = (float) lstGioHangChiTiet.stream().filter(item -> item.getTrangThai()).mapToDouble(GioHangChiTiet::getTongTien).sum();
            if (tongTien > 20000000) {
                return ResponseEntity.badRequest().body(Map.of("message", "Đơn hàng vượt quá 20 triệu"));
            }
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
            int thanhTien = (int) ((int) tongTien +shippingFee - tongTienGiam);
            // Lưu tạm thông tin giao dịch vào session (hoặc Redis, DB nếu cần)
            Map<String, Object> giaoDichTam = new HashMap<>(sanPhamThanhToan);
            giaoDichTam.put("idKhachHang", khachHangSession.getId());
            giaoDichTam.put("maHoaDon", maHoaDon);
            giaoDichTam.put("thanhTien", thanhTien);
            giaoDichTam.put("tongTien", tongTien);
            giaoDichTam.put("tongTienGiam", tongTienGiam);
            giaoDichTam.put("tienShip", shippingFee);
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
    public String thanhToan(Model model, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Expires", "0"); // Proxies
        if (model.containsAttribute("error")) {
            model.addAttribute("errorMessage", model.getAttribute("error"));
        }
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
        tinhLaiGiaTien(gioHang);
        ArrayList<GioHangChiTiet> lstGioHangChiTiet = new ArrayList<>();
        ArrayList<GioHangChiTiet> lstGHCTALL = gioHangChiTietService.findByIdGioHang(gioHang.getId());
        for (GioHangChiTiet gh : lstGHCTALL) {
            if (gh.getTrangThai()) {
                lstGioHangChiTiet.add(gh);
            }
        }
        if (lstGioHangChiTiet == null || lstGioHangChiTiet.isEmpty() || lstGioHangChiTiet.size() == 0) {
            return "redirect:/Sneakers_Nice/hienthi";
        }
        double tien = lstGioHangChiTiet.stream().mapToDouble(GioHangChiTiet::getTongTien).sum();
        double shippingFee = 0; // Giả sử phí ship cố định
        List<KhuyenMai> lstKhuyenMai = khuyenMaiService.getKhuyenMaiByDieuKienGiam((float) tien);
        List<KhuyenMai> filterKhuyenMai = lstKhuyenMai.stream()
                .filter(Objects::nonNull) // Lọc các phần tử null
                .sorted(Comparator.comparingDouble((KhuyenMai km) -> {
                    if (km.getLoaiKhuyenMai()) {
                        return km.getGiaTriGiam();
                    } else {
                        double giamTheoPhanTram = km.getGiaTriGiam() * tien / 100;
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
                    double t2 = tien * km.getGiaTriGiam() / 100;
                    tongTienGiam = (long) Math.min(t1, t2);
                }
            } else {
                tongTienGiam = 0;
            }
        }

        double tongTien = tien + shippingFee;
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
        } else if (khuyenMai.getNgayKetThuc().before(new Date())) {
            khuyenMai.setTrangThai(false);
            khuyenMaiService.updateKhuyenMai(khuyenMai, khuyenMai.getId());
            return ResponseEntity.ok(Map.of("message", "Khuyến mãi không còn hoạt động"));
        } else if (!khuyenMai.getTrangThai() || khuyenMai.getSoLuong() - khuyenMai.getDaSuDung() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Khuyến mãi không còn hoạt động"));
        }
        GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangService.findByEmail(getCurrentUserEmail()).getId());
        // Lấy danh sách sản phẩm
        List<GioHangChiTiet> lstGioHangChiTiet = gioHangChiTietService.findByIdGioHang(gioHang.getId())
                .stream()
                .filter(item -> item.getTrangThai())
                .collect(Collectors.toList());
        boolean isChaged = false;
        StringBuilder thayDoi = new StringBuilder();
        for (GioHangChiTiet gh : lstGioHangChiTiet) {
            if (Math.abs(gh.getIdChiTietSanPham().getGiaBan() - gh.getDonGia()) > 0.001f) {
                thayDoi.append("").append(gh.getIdChiTietSanPham().getIdSanPham().getTenSanPham());
                isChaged = true;
            } else if (!gh.getTrangThai() || !gh.getIdChiTietSanPham().getTrangThai() || !gh.getIdChiTietSanPham().getIdSanPham().getTrangThai()) {
                thayDoi.append("").append(gh.getIdChiTietSanPham().getIdSanPham().getTenSanPham()
                );
            }
        }
        if (isChaged) {
            tinhLaiGiaTien(gioHang);
        }
        if (!thayDoi.toString().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Giá" + thayDoi.toString() + " thay đổi hoặc sản phẩm ngừng bán", "load", true));
        }
        try {
            List<?> rawList = (List<?>) Object.get("selectedIds");
            List<Integer> lstIdProductsChoice = rawList.stream()
                    .map(id -> Integer.parseInt(String.valueOf(id)))
                    .collect(Collectors.toList());
            float tongTienSanPhamDaChon = 0;
            for (Integer id : lstIdProductsChoice) {
                GioHangChiTiet gioHangChiTiet = gioHangChiTietService.findById(id);
                tongTienSanPhamDaChon += gioHangChiTiet.getTongTien();
            }
            if (tongTienSanPhamDaChon < khuyenMai.getDieuKienApDung()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Đơn hàng của bạn không đủ điểu kiện"));
            }
            if (gioHang.getIdKhuyenMai()!=null&&gioHang.getIdKhuyenMai().getId() == khuyenMai.getId()) {
                return ResponseEntity.ok().body(Map.of("message", "Đã áp dụng mã khuyến mãi"));
            }
            float tongTienGiam;
            if (khuyenMai.getLoaiKhuyenMai()) {
                tongTienGiam = khuyenMai.getGiaTriGiam();
            } else {
                double t1 = khuyenMai.getMucGiamGiaToiDa();
                //bổ sung thêm phí ship
                double t2 = (tongTienSanPhamDaChon) * khuyenMai.getGiaTriGiam() / 100;
                tongTienGiam = (float) Math.min(t1, t2);
            }
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
        tinhLaiGiaTien(gioHang);
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
                    boolean hetHang = chiTietSanPham.getSoLuong() <= 0;
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
                    map.put("checkSP", !ghct.getTrangThai()||!chiTietSanPham.getTrangThai() || !chiTietSanPham.getIdSanPham().getTrangThai());
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
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Sản phẩm trong kho không đủ")
                    );
                } else if (!chiTietSanPham.getTrangThai() || !chiTietSanPham.getIdSanPham().getTrangThai()) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Sản phẩm đã ngừng bán")
                    );
                }
                GioHangChiTiet ghct = gioHangChiTietService.findByIdGioHangAndIDCTSP(
                        gioHangService.findGioHangByIDKH(khachHang.getId()).getId(), chiTietSanPham.getId());
                if (ghct != null) {
                    int tongSoLuongSanPhamTrongGioHang = ghct.getSoLuong() + soLuong;
                    if (tongSoLuongSanPhamTrongGioHang > chiTietSanPham.getSoLuong()) {
                        boolean sameProduct = chiTietSanPham.getId() == ghct.getIdChiTietSanPham().getId();
                        boolean isAvailable = chiTietSanPham.getTrangThai() && ghct.getTrangThai();
                        boolean quantityMatch = ghct.getSoLuong() == chiTietSanPham.getSoLuong() && ghct.getSoLuong() > 0;
                        if (sameProduct && isAvailable && quantityMatch) {
                            return ResponseEntity.ok().body(
                                    Map.of("message", "Đã toàn bộ sản phẩm trong giỏ hàng", "check", true)
                            );
                        }
                        return ResponseEntity.badRequest()
                                .body(Collections.singletonMap("message", "Số lượng sản phẩm trong kho không đủ"));
                    }
                    float tongTien = 0;
                    ArrayList<GioHangChiTiet> lstGHCT = gioHangChiTietService.findByIdGioHang(ghct.getIdGioHang().getId());
                    for (GioHangChiTiet gh : lstGHCT
                    ) {
                        if (gh.getTrangThai()) {
                            tongTien += gh.getTongTien();
                        }
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
                        if (gh.getTrangThai()) {
                            tongTien += gh.getTongTien();
                        }
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
        if (gioHangChiTiet != null) {
            ArrayList<GioHangChiTiet> lstGHCT = new ArrayList<>();
            ArrayList<GioHangChiTiet> lstGHALL = gioHangChiTietService.findByIdGioHang(gioHangChiTiet.getIdGioHang().getId());
            for (GioHangChiTiet gh : lstGHALL) {
                if (gh.getTrangThai()) {
                    lstGHCT.add(gh);
                }
            }
            if (soLuong < 1) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "Số lượng sản phẩm phải lớn hơn 0"));
            }
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService
                    .findById(gioHangChiTiet.getIdChiTietSanPham().getId());
            if (!chiTietSanPham.getTrangThai() || !chiTietSanPham.getIdSanPham().getTrangThai()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Sản phẩm không còn hoạt động"));
            } else if (chiTietSanPham.getSoLuong() < soLuong) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "Số lượng sản phẩm trong kho không đủ"));
            } else if (gioHangChiTiet.getDonGia() * soLuong > 20000000) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Đơn hàng vượt quá 20 triệu, liên hệ để được hỗ trợ"));
            } else {
                int tongTienGioHang = 0;
                for (GioHangChiTiet gh : lstGHCT) {
                    if (gh.getIdChiTietSanPham().getGiaBan() != gh.getDonGia()) {
                        String sanPhamThayDoiGia = "Giá bán của " + gh.getIdChiTietSanPham().getIdSanPham().getTenSanPham() + " thay đổi";
                        gh.setDonGia(gh.getIdChiTietSanPham().getGiaBan());
                        gioHangChiTietService.saveGioHangChitiet(gh);
                        return ResponseEntity.badRequest().body(Map.of("message", sanPhamThayDoiGia));
                    }
                    if (gh.getId() == idGHCT && gh.getTrangThai()) {
                        tongTienGioHang += gh.getDonGia() * soLuong;
                    } else {
                        if (gh.getTrangThai()) {
                            tongTienGioHang += gh.getTongTien();
                        }
                    }
                }
                if (tongTienGioHang > 20000000) {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Đơn hàng vượt quá 20 triệu"));
                }
                gioHangChiTiet.setSoLuong(soLuong);
                gioHangChiTiet.setTongTien(gioHangChiTiet.getIdChiTietSanPham().getGiaBan() * soLuong);
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

    private void tinhLaiGiaTien(GioHang gioHang) {
        if (gioHang != null) {
            List<GioHangChiTiet> lstGHCT = gioHangChiTietService.findByIdGioHang(gioHang.getId());

            for (GioHangChiTiet ghct : lstGHCT) {
                ChiTietSanPham ctsp = ghct.getIdChiTietSanPham();

                // Xóa nếu số lượng không hợp lệ
                if (ghct.getSoLuong() <= 0) {
                    gioHangChiTietService.deleteGioHangChitiet(ghct.getId());
                    continue;
                }

                boolean spConHang = ctsp.getSoLuong() > 0;
                boolean ctspHoatDong = ctsp.getTrangThai();
                boolean spHoatDong = ctsp.getIdSanPham().getTrangThai();

                // Nếu sản phẩm không hoạt động hoặc hết hàng
                if (!ctspHoatDong || !spHoatDong || !spConHang) {
                    ghct.setTrangThai(false);
                } else {
                    ghct.setTrangThai(true);
                    // Cập nhật số lượng nếu vượt quá tồn kho
                    ghct.setSoLuong(Math.min(ghct.getSoLuong(), ctsp.getSoLuong()));
                }

                // Cập nhật trọng lượng nếu sai
                int trongLuongDung = (int) ctsp.getTrongLuong() * ghct.getSoLuong();
                if (ghct.getTongTrongLuong() != trongLuongDung) {
                    ghct.setTongTrongLuong(trongLuongDung);
                }

                // Cập nhật đơn giá & tổng tiền
                ghct.setDonGia(ctsp.getGiaBan());
                ghct.setTongTien(ghct.getDonGia() * ghct.getSoLuong());
                ghct.setNgaySua(new Date());

                gioHangChiTietService.saveGioHangChitiet(ghct);
            }

        }


    }

    //kiểm tra xem khuyến mãi còn hoạt động không
    private int checkKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai != null) {
            if (!khuyenMai.getTrangThai() || khuyenMai.getNgayKetThuc().before(new Date())) {
                if (khuyenMai.getNgayKetThuc().before(new Date())) {
                    //nếu khuyến mãi đã hết hạn sử dụng sẽ chuyeern trạng thái
                    khuyenMai.setTrangThai(false);
                    khuyenMai.setNgaySua(new Date());
                    khuyenMaiService.updateKhuyenMai(khuyenMai, khuyenMai.getId());
                }
                return 1;
            } else if (khuyenMai.getSoLuong() - khuyenMai.getDaSuDung() <= 0) {
                return 2;
            }
        }
        return 3;
    }
}
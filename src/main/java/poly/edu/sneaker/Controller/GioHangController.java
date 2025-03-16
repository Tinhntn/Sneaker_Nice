package poly.edu.sneaker.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import poly.edu.sneaker.Service.GioHangChiTietService;
import poly.edu.sneaker.Service.GioHangService;
import poly.edu.sneaker.Service.HoaDonService;

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
    HttpSession httpSession;


//    @PostMapping("/thanh-toan")
//    @ResponseBody
//    public ResponseEntity<?> thanhToanOnline(@ResponseBody Map<String,Object> sanPhamThanhToan){
//        HoaDon hoaDon = new  HoaDon();
//        hoaDon.setMaHoaDon(hoaDonService.taoMaHoaDon());
//        hoaDon.setThanhTien(sanPhamThanhToan.);
//    }
    @GetMapping("/thanh-toan")
    public String thanhToan(Model model) {
        KhachHang khachHangSessiong = (KhachHang) httpSession.getAttribute("khachHangSession");
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
        for ( GioHangChiTiet ghct : lstGioHangChiTiet
             ) {
            tien= tien+ghct.getTongTien();
        }
        double shippingFee = 65000; // Giả sử phí ship cố định
        double tongTien = tien+shippingFee;
        model.addAttribute("shippingFee",shippingFee);
        model.addAttribute("totalPrice",tongTien);
        model.addAttribute("lstGioHangChiTiet", lstGioHangChiTiet);
        return "/user/sanpham/checkout";
    }

    @GetMapping()
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getGioHang(Model model) {
        KhachHang khachHangSession = (KhachHang) httpSession.getAttribute("khachHangSession");

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
                    if (ghct.getIdChiTietSanPham() == null) return map; // Trả về Map rỗng nếu dữ liệu bị thiếu

                    map.put("tenSanPham", Optional.ofNullable(ghct.getIdChiTietSanPham().getIdSanPham())
                            .map(SanPham::getTenSanPham).orElse("N/A"));
                    map.put("mauSac", Optional.ofNullable(ghct.getIdChiTietSanPham().getIdMauSac())
                            .map(MauSac::getTenMauSac).orElse("N/A"));
                    map.put("size", Optional.ofNullable(ghct.getIdChiTietSanPham().getIdSize())
                            .map(Size::getTenSize).orElse("N/A"));
                    map.put("gia", Optional.ofNullable(ghct.getIdChiTietSanPham().getGiaBan()).orElse(0f));
                    map.put("idGioHangChiTiet", ghct.getId());
                    map.put("soLuong", Optional.ofNullable(ghct.getSoLuong()).orElse(0));
                    map.put("hinhAnh", Optional.ofNullable(ghct.getIdChiTietSanPham().getHinhAnh())
                            .orElse("Không có ảnh"));
                    map.put("idGioHang", ghct.getIdGioHang());
                    return map;
                })
                .collect(Collectors.toList());

        double totalPrice = items.stream()
                .mapToDouble(i -> ((Number) i.get("gia")).doubleValue() * ((Number) i.get("soLuong")).intValue())
                .sum();

        return ResponseEntity.ok(Map.of("items", items, "totalPrice", totalPrice));
    }


    @GetMapping("/so-luong")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getSoLuongGioHang() {
        KhachHang khachHang = (KhachHang) httpSession.getAttribute("khachHangSession");
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

        KhachHang khachHang = (KhachHang) httpSession.getAttribute("khachHangSession");
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
                    ghct.setSoLuong(ghct.getSoLuong() + soLuong);
                    gioHangChiTietService.saveGioHangChitiet(ghct);
                    return ResponseEntity.ok()
                            .body(Collections.singletonMap("Thêm sản phẩm vào giỏ hàng thành công", true));
                }
                GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();
                gioHangChiTiet.setIdGioHang(gioHangService.findGioHangByIDKH(khachHang.getId()));
                gioHangChiTiet.setIdChiTietSanPham(chiTietSanPham);
                gioHangChiTiet.setSoLuong(soLuong);
                gioHangChiTiet.setDonGia(chiTietSanPham.getGiaBan());
                gioHangChiTiet.setTongTien(gioHangChiTiet.getDonGia() * soLuong);
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
            return ResponseEntity.badRequest().body(Map.of("message", "Bạn cần đăng nhập để mua sắm"));
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
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService
                    .findById(gioHangChiTiet.getIdChiTietSanPham().getId());
            if (chiTietSanPham.getSoLuong() < soLuong) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "Số lượng sản phẩm trong kho không đủ"));
            } else {
                gioHangChiTiet.setSoLuong(soLuong);
                gioHangChiTietService.saveGioHangChitiet(gioHangChiTiet);
                return ResponseEntity.ok(Collections.singletonMap("success", true));
            }
        }
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Cập nhật sản phẩm thất bại"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSoLuongSanPham(@PathVariable int id, @RequestParam int soLuong) {
        GioHangChiTiet ghct = gioHangChiTietService.findById(id);
        if (ghct == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Sản phẩm không tồn tại"));
        }
        if (soLuong <= 0) {
            gioHangChiTietService.deleteGioHangChitiet(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Sản phẩm đã bị xóa khỏi giỏ hàng"));
        }
        ghct.setSoLuong(soLuong);
        ghct.setTongTien(ghct.getDonGia() * soLuong);
        gioHangChiTietService.saveGioHangChitiet(ghct);
        return ResponseEntity.ok(Collections.singletonMap("message", "Cập nhật số lượng thành công"));
    }

    private int converToInt(Object object) {
        if (object instanceof Integer) {
            return (Integer) object;
        }
        return Integer.parseInt(object.toString());
    }
}

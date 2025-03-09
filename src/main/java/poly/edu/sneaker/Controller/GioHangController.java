package poly.edu.sneaker.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.GioHang;
import poly.edu.sneaker.Model.GioHangChiTiet;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import poly.edu.sneaker.Service.GioHangChiTietService;
import poly.edu.sneaker.Service.GioHangService;

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
    HttpSession httpSession;

    @GetMapping("/thanh-toan")
    public String thanhToan(Model model){
        KhachHang khachHangSessiong = (KhachHang) httpSession.getAttribute("khachHangSession");
        GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangSessiong.getId());
        if (gioHang == null) {
            model.addAttribute("message", "Giỏ hàng của bạn đang trống!");
            return "redirect:/Sneakers_Nice/hienthi";
        }
        ArrayList<GioHangChiTiet> lstGioHangChiTiet = gioHangChiTietService.findByIdGioHang(gioHang.getId());
        model.addAttribute("lstGioHandChiTiet",lstGioHangChiTiet);
        model.addAttribute("gioHang",gioHang);
        return "user/sanpham/trangchu";
    }
    @GetMapping()
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getGioHang(Model model){
        int soLuongSanPhamTrongGioHang = 0;
        KhachHang khachHangSession = (KhachHang) httpSession.getAttribute("khachHangSession");
        if (khachHangSession != null) {
            model.addAttribute("khachHang", khachHangSession);
            GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangSession.getId());
            model.addAttribute("gioHang",gioHang);
            ArrayList<GioHangChiTiet> lstGioHangChiTiet = gioHangChiTietService.findByIdGioHang(gioHang.getId());
            List<Map<String, Object>> items = lstGioHangChiTiet.stream()
                    .map(ghct -> {
                        Map<String, Object> map = new HashMap<>();
                        if (ghct == null || ghct.getIdChiTietSanPham() == null) {
                            return map; // Trả về Map rỗng nếu dữ liệu null
                        }
                        map.put("tenSanPham", ghct.getIdChiTietSanPham().getIdSanPham() != null ? ghct.getIdChiTietSanPham().getIdSanPham().getTenSanPham() : "N/A");
                        map.put("mauSac", ghct.getIdChiTietSanPham().getIdMauSac() != null ? ghct.getIdChiTietSanPham().getIdMauSac().getTenMauSac() : "N/A");
                        map.put("size", ghct.getIdChiTietSanPham().getIdSize() != null ? ghct.getIdChiTietSanPham().getIdSize().getTenSize() : "N/A");
                        map.put("gia", ghct.getIdChiTietSanPham().getGiaBan());
                        map.put("idGioHangChiTiet", ghct.getId());
                        map.put("soLuong", ghct.getSoLuong());
                        map.put("hinhAnh", ghct.getIdChiTietSanPham().getHinhAnh()!=null ?ghct.getIdChiTietSanPham().getHinhAnh():"Không có ảnh"); // Ảnh sản phẩm
                        map.put("idGioHang",ghct.getIdGioHang());
                        return map;
                    })
                    .collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("items", items);
            response.put("totalPrice", items.stream().mapToDouble(i -> (float) i.get("gia") * (int) i.get("soLuong")).sum());

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(Map.of("message","Lỗi không xác định"));
    }
    @GetMapping("/so-luong")
    @ResponseBody
    public Map<String, Integer> getSoLuongGioHang() {
        KhachHang khachHang = (KhachHang) httpSession.getAttribute("khachHangSession");
        int soLuongSanPham = 0;

        if(khachHang!=null){
            GioHang gh = gioHangService.findGioHangByIDKH(khachHang.getId());
            ArrayList<GioHangChiTiet> lstGHCT = gioHangChiTietService.findByIdGioHang(gh.getId());
            if(lstGHCT!=null){
                for ( GioHangChiTiet ghct : lstGHCT
                     ) {
                    soLuongSanPham = soLuongSanPham+ghct.getSoLuong();
                }
            }
        }
        Map<String, Integer> response = new HashMap<>();
        response.put("soLuong", soLuongSanPham);
        return response;
    }

    @PostMapping("/them-vao-gio-hang")
    public ResponseEntity<?> themSanPhamVaoGioHang(@RequestBody Map<String,Object> sanPhamChon){

        KhachHang khachHang = (KhachHang) httpSession.getAttribute("khachHangSession");
        if(khachHang!=null){
            try {
                int soLuong = converToInt(sanPhamChon.get("soLuong"));
                int idSanPham = converToInt(sanPhamChon.get("idSanPham"));
                int idSize = converToInt(sanPhamChon.get("idSize"));
                int idMauSac = converToInt(sanPhamChon.get("idMauSac"));
                ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findCTSPByIdSPAndIdMauSacAndIdSize(idSanPham,idSize,idMauSac);
                if(chiTietSanPham==null){
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message","Sản phẩm không còn hoạt động"));
                }else if(chiTietSanPham.getSoLuong()<=0){
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message","Sản phẩm đã hết hàng"));
                }
                GioHangChiTiet ghct = gioHangChiTietService.findByIdGioHangAndIDCTSP(gioHangService.findGioHangByIDKH(khachHang.getId()).getId(),chiTietSanPham.getId());
                if(ghct!=null){
                    int tongSoLuongSanPhamTrongGioHang = ghct.getSoLuong()+soLuong;
                    if(tongSoLuongSanPhamTrongGioHang>chiTietSanPham.getSoLuong()){
                        return ResponseEntity.badRequest().body(Collections.singletonMap("message","Số lượng sản phẩm trong kho không đủ"));
                    }
                    ghct.setSoLuong(ghct.getSoLuong()+soLuong);
                    gioHangChiTietService.saveGioHangChitiet(ghct);
                    return ResponseEntity.ok().body(Collections.singletonMap("Thêm sản phẩm vào giỏ hàng thành công",true));
                }
                GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();
                gioHangChiTiet.setIdGioHang(gioHangService.findGioHangByIDKH(khachHang.getId()));
                gioHangChiTiet.setIdChiTietSanPham(chiTietSanPham);
                gioHangChiTiet.setSoLuong(soLuong);
                gioHangChiTiet.setDonGia(chiTietSanPham.getGiaBan());
                gioHangChiTiet.setTongTien(chiTietSanPham.getGiaBan()*soLuong);
                gioHangChiTiet.setNgayTao(new Date());
                gioHangChiTiet.setTrangThai(true);
                gioHangChiTietService.saveGioHangChitiet(gioHangChiTiet);
                return ResponseEntity.ok(Collections.singletonMap("Thêm sản phẩm vào giỏ hàng thành công", true));

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("message", "Lỗi khi cập nhật sản phẩm!"));
            }
        }else{
            return ResponseEntity.badRequest().body(Map.of("mesage","Bạn cần đăng nhập để mua sắm"));
        }

    }
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeSanPham(@PathVariable int id) {
        gioHangChiTietService.deleteGioHangChitiet(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Xóa sản phẩm thành công"));
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

    private int converToInt(Object object){
        if(object instanceof Integer){
            return (Integer)object;
        }
        return Integer.parseInt(object.toString());
    }
}

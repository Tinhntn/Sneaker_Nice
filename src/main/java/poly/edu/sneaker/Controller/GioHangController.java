package poly.edu.sneaker.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Model.GioHangChiTiet;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Service.ChiTietSanPhamService;
import poly.edu.sneaker.Service.GioHangService;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Controller
public class GioHangController {
    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    GioHangService gioHangService;
    @Autowired
    HttpSession httpSession;
    @PostMapping("/themvaogiohang")
    public ResponseEntity<?> themSanPhamVaoGioHang(@RequestBody Map<String,Object> sanPhamChon){
//        KhachHang khachHang = (KhachHang) httpSession.getAttribute("khachHang");
       try {
           int soLuong = converToInt(sanPhamChon.get("soLuong"));
           int idSanPham = converToInt(sanPhamChon.get("idSanPham"));
           int idSize = converToInt(sanPhamChon.get("idSize"));
           int idMauSac = converToInt(sanPhamChon.get("idMauSac"));
           ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findCTSPByIdSPAndIdMauSacAndIdSize(idSanPham,idMauSac,idSize);
           GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();
           gioHangChiTiet.setIdGioHang(gioHangService.findById(1));
           gioHangChiTiet.setIdChiTietSanPham(chiTietSanPham);
           gioHangChiTiet.setSoLuong(soLuong);
           gioHangChiTiet.setDonGia(chiTietSanPham.getGiaBan());
           gioHangChiTiet.setTongTien(chiTietSanPham.getGiaBan()*soLuong);
           gioHangChiTiet.setNgaySua(new Date());
           gioHangChiTiet.setTrangThai(true);
           return ResponseEntity.ok(Collections.singletonMap("success", true));

       } catch (Exception e) {
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Collections.singletonMap("message", "Lỗi khi cập nhật sản phẩm!"));
       }
    }

    private int converToInt(Object object){
        if(object instanceof Integer){
            return (Integer)object;
        }
        return Integer.parseInt(object.toString());
    }
}

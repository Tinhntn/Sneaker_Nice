package poly.edu.sneaker.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Model.Interface.SanPhamInterface;
import poly.edu.sneaker.Service.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Sneakers_Nice")
public class Home {
    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private SizeService sizeService;
    @Autowired
    private MauSacService mauSacService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private GioHangChiTietService gioHangChiTietService;

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page) {
        KhachHang khachHangSession = (KhachHang) httpSession.getAttribute("khachHangSession");

        int size = 12;
        Page<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findChiTietSanPhamJustOne(PageRequest.of(page, size));
        for (ChiTietSanPham ctsp : lstCTSP
             ) {
            if(ctsp.getSoLuong()<=0){
                ctsp.setTrangThai(false);
                chiTietSanPhamService.saveChiTietSanPham(ctsp);
            }
        }
        int soLuongSanPhamTrongGioHang = 0;

        if (khachHangSession != null) {
            model.addAttribute("khachHang", khachHangSession);
            GioHang gioHang = gioHangService.findGioHangByIDKH(khachHangSession.getId());
            ArrayList<GioHangChiTiet> lstGioHangChiTiet = gioHangChiTietService.findByIdGioHang(gioHang.getId());
            for ( GioHangChiTiet ghct : lstGioHangChiTiet
                 ) {
                ChiTietSanPham chiTietSanPham =ghct.getIdChiTietSanPham();
                boolean checkSoLuong = chiTietSanPham.getSoLuong()>0&&chiTietSanPham.getSoLuong()<ghct.getSoLuong();
                if(checkSoLuong){
                    ghct.setSoLuong(chiTietSanPham.getSoLuong());
                    gioHangChiTietService.saveGioHangChitiet(ghct);
                }
            }
            model.addAttribute("lstGioHangChiTiet", lstGioHangChiTiet);
            for (GioHangChiTiet ghct
                    : lstGioHangChiTiet
            ) {
                soLuongSanPhamTrongGioHang = soLuongSanPhamTrongGioHang+ghct.getSoLuong();
            }

            model.addAttribute("soLuongSanPhamTrongGioHang",soLuongSanPhamTrongGioHang);
        }
        model.addAttribute("listSanPham", lstCTSP);
        model.addAttribute("currentPage", lstCTSP.getNumber());
        model.addAttribute("totalPages", lstCTSP.getTotalPages());
        return "user/sanpham/trangchu";
    }

    @GetMapping("/chitietsanpham/{id}")
    public String chiTietSanPham(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        ChiTietSanPham chiTietSanPhams = chiTietSanPhamService.findById(id);
        if (chiTietSanPhams == null) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
            return "redirect:/Sneakers/hienthi"; // Điều hướng về trang chủ hoặc trang danh sách sản phẩm
        }
        ArrayList<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findByIdSanPham(chiTietSanPhams.getIdSanPham().getId());

        Set<MauSac> lstMauSacs = new HashSet<>();
        Set<Size> lstSize = new HashSet<>();

        for (ChiTietSanPham ctsp : lstCTSP) {
            lstMauSacs.add(ctsp.getIdMauSac());
            lstSize.add(ctsp.getIdSize());
        }

        model.addAttribute("chiTietSanPham", chiTietSanPhams);
        model.addAttribute("lstCTSP", lstCTSP);
        model.addAttribute("lstSize", lstSize);
        model.addAttribute("lstMauSac", lstMauSacs);
        return "user/sanpham/detailSanPham";
    }

    @GetMapping("/chitietsanpham")
    public ResponseEntity<ChiTietSanPham> getChiTietSanPhamByIdMauSac(@RequestParam int idCTSP, @RequestParam int idMauSac) {
        ChiTietSanPham ct = chiTietSanPhamService.findCTSPByIDMauSac(idCTSP, idMauSac);
        if (ct != null) {
            ct.getHinhAnh();
            return ResponseEntity.ok(ct);
        }
        return ResponseEntity.notFound().build();
    }

}

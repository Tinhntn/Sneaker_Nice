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

import java.util.*;
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
    @Autowired
    GioHangService gioHangService;
    @Autowired
    GioHangChiTietService gioHangChiTietService;

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }

    @PostMapping("/MuaLai")
    public ResponseEntity<?> muaLaiDonHang(@RequestParam int idHoaDon) {
        // Tìm hóa đơn
        HoaDon hoaDon = hoaDonService.findById(idHoaDon);
        if (hoaDon == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy hóa đơn"));
        }

        // Tìm khách hàng từ session
        KhachHang khachHang = khachHangService.findByEmail(getCurrentUserEmail());
        if (khachHang == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Không xác định được khách hàng"));
        }

        // Tìm giỏ hàng
        GioHang gioHang = gioHangService.findGioHangByIDKH(khachHang.getId());
        List<GioHangChiTiet> lstGioHangDangCo = gioHangChiTietService.findByIdGioHang(gioHang.getId());

        if (gioHang == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy giỏ hàng"));
        }

        // Lấy chi tiết hóa đơn
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(hoaDon.getId());

        // Gộp sản phẩm theo chi tiết sản phẩm
        Map<Integer, HoaDonChiTiet> gopChiTietMap = new HashMap<>();
        for (HoaDonChiTiet hd : hoaDonChiTiets) {
            int idCTSP = hd.getIdChiTietSanPham().getId();
            gopChiTietMap.compute(idCTSP, (key, existing) -> {
                if (existing == null) {
                    HoaDonChiTiet moi = new HoaDonChiTiet();
                    moi.setIdChiTietSanPham(hd.getIdChiTietSanPham());
                    moi.setSoLuong(hd.getSoLuong());
                    return moi;
                } else {
                    existing.setSoLuong(existing.getSoLuong() + hd.getSoLuong());
                    return existing;
                }
            });
        }

        List<String> spKhongThem = new ArrayList<>();
        boolean daThemSanPham = false;

        float tongTien = 0;
        for (GioHangChiTiet gh : lstGioHangDangCo) {
            tongTien += gh.getTongTien();
        }
        for (HoaDonChiTiet hdGop : gopChiTietMap.values()) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(hdGop.getIdChiTietSanPham().getId());
            if (chiTietSanPham == null) {
                spKhongThem.add("Sản phẩm ID " + hdGop.getIdChiTietSanPham().getId() + " không tồn tại");
                continue;
            }
            boolean sanPhamConKinhDoanh = chiTietSanPham.getTrangThai() && chiTietSanPham.getIdSanPham().getTrangThai();
            boolean conHang = chiTietSanPham.getSoLuong() > 0;

            if (!sanPhamConKinhDoanh || !conHang) {
                spKhongThem.add("Sản phẩm " + chiTietSanPham.getIdSanPham().getTenSanPham() + " (Size: " +
                        chiTietSanPham.getIdSize().getTenSize() + ", Màu: " + chiTietSanPham.getIdMauSac().getTenMauSac() + ") hiện không còn bán hoặc đã hết hàng.");
                continue;
            }
            int soLuongThem = Math.min(hdGop.getSoLuong(), chiTietSanPham.getSoLuong());
            GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();
            gioHangChiTiet.setIdGioHang(gioHang);
            gioHangChiTiet.setIdChiTietSanPham(chiTietSanPham);
            gioHangChiTiet.setSoLuong(soLuongThem);
            gioHangChiTiet.setTongTrongLuong((int) chiTietSanPham.getTrongLuong() * soLuongThem);
            gioHangChiTiet.setDonGia(chiTietSanPham.getGiaBan());
            gioHangChiTiet.setNgayTao(new Date());
            gioHangChiTiet.setTrangThai(true);
            gioHangChiTiet.setTongTien(gioHangChiTiet.getDonGia() * soLuongThem);
            if (tongTien + gioHangChiTiet.getTongTien() > 20000000) {
                return ResponseEntity.ok().body(Map.of(
                        "message", "Tổng đơn hàng đã đạt mức tối đa",
                        "SPKhongThem", spKhongThem,
                        "URL", "/gio-hang/thanh-toan"
                ));
            }
            gioHangChiTietService.saveGioHangChitiet(gioHangChiTiet);
            daThemSanPham = true;
        }

        if (!daThemSanPham) {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Không có sản phẩm nào khả dụng để mua lại",
                    "SPKhongThem", spKhongThem
            ));
        }

        return ResponseEntity.ok().body(Map.of(
                "message", "Mua lại thành công",
                "SPKhongThem", spKhongThem,
                "URL", "/gio-hang/thanh-toan"
        ));
    }


    @GetMapping("/hienthi")
    public String hienthi(
            Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword) {
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

        model.addAttribute("listHoaDonTatCaKH", listHoaDonTatCaKH);
        model.addAttribute("currentPage", listHoaDonTatCaKH.getNumber());
        model.addAttribute("totalPages", listHoaDonTatCaKH.getTotalPages());

        model.addAttribute("listHoaDonDHKH", listHoaDonDHKH);
        model.addAttribute("currentPage", listHoaDonDHKH.getNumber());
        model.addAttribute("totalPages", listHoaDonDHKH.getTotalPages());

        model.addAttribute("listHoaDonCXNKH", listHoaDonCXNKH);
        model.addAttribute("currentPage", listHoaDonCXNKH.getNumber());
        model.addAttribute("totalPages", listHoaDonCXNKH.getTotalPages());

        model.addAttribute("listHoaDonCLHKH", listHoaDonCLHKH);
        model.addAttribute("currentPage", listHoaDonCLHKH.getNumber());
        model.addAttribute("totalPages", listHoaDonCLHKH.getTotalPages());

        model.addAttribute("listHoaDonDGKH", listHoaDonDGKH);
        model.addAttribute("currentPage", listHoaDonDGKH.getNumber());
        model.addAttribute("totalPages", listHoaDonDGKH.getTotalPages());

        model.addAttribute("listHoaDonHTKH", listHoaDonHTKH);
        model.addAttribute("currentPage", listHoaDonHTKH.getNumber());
        model.addAttribute("totalPages", listHoaDonHTKH.getTotalPages());

        return "user/hoadon/listHoaDonKhachHang";
    }


    @PostMapping("/huyhoadon")
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
            dt.setSoLuong(dt.getSoLuong() + ct.getSoLuong());
            chiTietSanPhamService.update(dt);
        }
        redirectAttributes.addFlashAttribute("thanhcong", "Hủy thành công");
        return "redirect:/hoadononlinekhachhang/hienthi";
    }

    @GetMapping("/detailhoadononlinect/{id}")
    public String chitiethoadononline(@PathVariable int id, Model model, @RequestParam(defaultValue = "0") int page) {
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        HoaDon hoaDon = hoaDonOnlService.detailHD(id);
        if (hoaDon == null) {
            return "redirect:/hoadononlinekhachhang/hienthi";
        }
        List<HoaDonChiTiet> chiTietHoaDon = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(id);
        if (chiTietHoaDon == null || chiTietHoaDon.isEmpty()) {
            return "redirect:/hoadononlinekhachhang/hienthi";
        }
        List<TrangThaiDonHang> lstTrangThaiDonHang = lichSuTrnngThaiService.getAllByIdHoaDon(id);
        boolean daGuiYeuCauHuy = lstTrangThaiDonHang.stream().anyMatch(item -> item.getTrangThai() == 12);
        boolean daNhanHang = lstTrangThaiDonHang.stream().anyMatch(item -> item.getTrangThai() == 13);

        model.addAttribute("daGuiYeuCauHuy", daGuiYeuCauHuy); // <-- Đúng kiểu boolean
        model.addAttribute("daNhanHang", daNhanHang);
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

    @PostMapping("/guimaildonhang")
    @ResponseBody
    public ResponseEntity<?> DoiTrangThai(@RequestBody Map<String, Object> formData) {
        try {
            int idhoadon = Integer.parseInt(formData.get("idHoaDon").toString());
            String ghichu = formData.get("ghiChu").toString();
            HoaDon hoaDon = hoaDonService.findById(idhoadon);
            if (hoaDon == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Hóa đơn không tồn tại"));
            }


            if (hoaDon.getTrangThai() != 2 && hoaDon.getTrangThai() != 5) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Trạng thái đơn hàng không thể hủy"));
            }
            if (hoaDon.getTrangThai() == 2) {
                List<TrangThaiDonHang> lstTrangThaiDonHang = lichSuTrnngThaiService.getAllByIdHoaDon(hoaDon.getId());
                if (lstTrangThaiDonHang != null) {
                    for (TrangThaiDonHang tt : lstTrangThaiDonHang) {
                        if (tt.getTrangThai() == 12) {
                            return ResponseEntity.ok().body(Map.of("success", true, "message", "Đơn hàng đã yêu cầu hủy "));
                        }
                    }
                }
            }
            // Sửa điều kiện kiểm tra khách hàng
            if (hoaDon.getIdKhachHang() == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy thông tin khách hàng"));
            }

            KhachHang khachHang = hoaDon.getIdKhachHang();
            boolean guiMailYeuCauHuy = khachHangService.guiMailDonHang(khachHang, hoaDon, ghichu);
            System.out.println(guiMailYeuCauHuy);
            if (guiMailYeuCauHuy) {
                TrangThaiDonHang trangThaiDonHang = new TrangThaiDonHang();
                trangThaiDonHang.setIdHoaDon(hoaDon);
                trangThaiDonHang.setNgayCapNhat(new Date());
                if (hoaDon.getTrangThai() == 2) {
                    trangThaiDonHang.setTrangThai(12);
                } else {
                    trangThaiDonHang.setTrangThai(13);
                }
                trangThaiDonHang.setGhiChu(ghichu);
                lichSuTrnngThaiService.saveLichSuTrangThai(trangThaiDonHang);
                return ResponseEntity.ok().body(Map.of("success", true, "message", hoaDon.getTrangThai() == 2
                        ? "Gửi yêu cầu hủy đơn hàng thành công"
                        : "Gửi yêu cầu thành công"));
            }

            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Gửi yêu cầu thất bại"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi hệ thống"));
        }
    }


}

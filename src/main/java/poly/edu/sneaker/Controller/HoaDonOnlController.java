package poly.edu.sneaker.Controller;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
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
    KhuyenMaiService khuyenMaiService;
    @Autowired
    LichSuTrnngThaiService lichSuTrnngThaiService;

    @Autowired
    BanHangTaiQuayService banHangTaiQuayService;
    @Autowired
    private HoaDonChiTietService hoaDonChiTietService;
    @Autowired
    private MauSacService mauSacService;
    @Autowired
    private ChatLieuService chatLieuService;
    @Autowired
    private DanhMucService danhMucService;
    @Autowired
    private HangService hangService;


    @PostMapping("/MuaLai")
    public ResponseEntity<?> muaLaiDonHang(@RequestBody int idHoaDon) {

        HoaDon hoaDon = hoaDonService.findById(idHoaDon);
        if (hoaDon == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy hóa đơn"));
        }
        HoaDon hoaDon1 = new HoaDon();
        if (hoaDon.getIdNhanVien() != null) {
            hoaDon1.setIdNhanVien(hoaDon.getIdNhanVien());
        }
        if (hoaDon.getIdKhachHang() != null) {
            hoaDon1.setIdKhachHang(hoaDon.getIdKhachHang());
        }
        List<ChiTietSanPham> chiTietSanPhamHetHang = new ArrayList<>();
        List<ChiTietSanPham> chiTietSanPhamKhongDu = new ArrayList<>();
        float tongTien = 0;
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(hoaDon.getId());
        for (HoaDonChiTiet hd : hoaDonChiTiets) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(hd.getIdChiTietSanPham().getId());
            int soLuongTon = chiTietSanPham.getSoLuong();
            int soLuongCan = hd.getSoLuong();
            if (soLuongTon <= 0) {
                chiTietSanPhamHetHang.add(hd.getIdChiTietSanPham());
                continue;
            }
            HoaDonChiTiet hd1 = new HoaDonChiTiet();
            hd1.setIdHoaDon(hoaDon1);
            if (soLuongCan > soLuongTon) {
                hd1.setSoLuong(Math.min(soLuongCan, soLuongTon));
                chiTietSanPhamKhongDu.add(hd1.getIdChiTietSanPham());
            } else {
                hd1.setSoLuong(soLuongCan);
            }
            hd1.setIdChiTietSanPham(hd.getIdChiTietSanPham());
            hd1.setTongTrongLuong(chiTietSanPham.getTrongLuong() * chiTietSanPham.getSoLuong());
            hd1.setDonGia(chiTietSanPham.getGiaBan());
            hd1.setGhiChu("Đơn hàng mua lại từ hóa đơn" + hd1.getIdHoaDon().getMaHoaDon());
            hd1.setNgayTao(new Date());
            hd1.setNgaySua(new Date());
            hd1.setTrangThai(1);
            hoaDonChiTietService.saveHoaDonChiTiet(hd1);
            tongTien += hd1.getSoLuong() * hd1.getDonGia();
        }
        hoaDon1.setMaHoaDon(hoaDonService.taoMaHoaDon());
        hoaDon1.setThanhTien(0);
        hoaDon1.setGhiChu("Mua lại hoa đơn" + hoaDon.getMaHoaDon());
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
        hoaDon1.setTongTien(tongTien);
        hoaDon1.setThanhTien(tongTien + hoaDon1.getPhiShip());
        hoaDonService.save(hoaDon1);
        return ResponseEntity.ok().body(Map.of("message", "Mua lại thành công", "URL", "hoadononline/detailhoadononlinect/" + hoaDon1.getId(), "spHet " + chiTietSanPhamHetHang.size(), "spKhongDu" + chiTietSanPhamKhongDu.size()));
    }

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page) {
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));

        Page<HoaDonOnlCustom> listHoaDonTatCa = hoaDonOnlService.getHoaDonCustomTatCa(pageable);
        Integer sizeTatCa = listHoaDonTatCa.getContent().size();
        model.addAttribute("sizeTatCa", sizeTatCa);

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


        model.addAttribute("listHoaDonTatCa", listHoaDonTatCa.getContent());
        model.addAttribute("currentPage", listHoaDonTatCa.getNumber());
        model.addAttribute("totalPages", listHoaDonTatCa.getTotalPages());

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
        hd.setTrangThai(3);
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
        hd.setTrangThai(4);
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
        hd.setTrangThai(5);
        hoaDonOnlService.updateHoaDon(hd, id);
        redirectAttributes.addFlashAttribute("thanhcong", "thành công");
        return "redirect:/hoadononline/hienthi";
    }


    @GetMapping("/detailhoadononlinect/{id}")
    public String chitiethoadononline(@PathVariable int id, Model model, @RequestParam(defaultValue = "0") int page
                                     ) {

        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        HoaDon hoaDon = hoaDonOnlService.detailHD(id);
        if (hoaDon == null) {
            return "redirect:/hoadononline/hienthi";
        }
        List<HoaDonChiTiet> chiTietHoaDon = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(id);
        if (chiTietHoaDon == null || chiTietHoaDon.isEmpty()) {
            return "redirect:/hoadononline/hienthi";
        }
        List<TrangThaiDonHang> lstTrangThaiDonHang = lichSuTrnngThaiService.getAllByIdHoaDon(id);
        HoaDon hoaDon1 = hoaDonService.findById(id);

        model.addAttribute("hoaDon", hoaDon1);
        model.addAttribute("lichSuTrangThai", lstTrangThaiDonHang);
        model.addAttribute("chiTietHoaDon", chiTietHoaDon);
        model.addAttribute("hoaDon", hoaDon);
        List<MauSac> lstMauSac = mauSacService.findAll().stream()
                .sorted(Comparator.comparing(MauSac::getTenMauSac, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        List<Size> lstSize = sizeService.findAll().stream().sorted((s1, s2) -> Integer.compare(
                Integer.parseInt(s1.getTenSize()),
                Integer.parseInt(s2.getTenSize())
        )).collect(Collectors.toList());
        List<ChatLieu> lstChatLieu = chatLieuService.getAllChatLieus().stream().sorted(Comparator.comparing(ChatLieu::getTenChatLieu, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
        List<DanhMuc> lstDanhMuc = danhMucService.getAllDanhMucs().stream().sorted(Comparator.comparing(DanhMuc::getTenDanhMuc, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
        List<Hang> lstHang = hangService.getAllHangs().stream().sorted(Comparator.comparing(Hang::getTenHang, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
        model.addAttribute("lstSize", lstSize);
        model.addAttribute("lstMauSac", lstMauSac);
        model.addAttribute("lstChatLieu", lstChatLieu);
        model.addAttribute("lstDanhMuc", lstDanhMuc);
        model.addAttribute("lstHang", lstHang);
//        Page<ChiTietSanPham> sanPhamChiTiet =  chiTietSanPhamService.findChiTietSanPhamJustOne(keyword,idHang,idDanhMuc,idChatLieu,idMauSac,idSize,PageRequest.of(page, size));
//        model.addAttribute("sanPhamChiTiet", sanPhamChiTiet);
//        model.addAttribute("currentPageCTSP", sanPhamChiTiet.getNumber());
//        model.addAttribute("totalPagesCTSP", sanPhamChiTiet.getTotalPages());
        return "admin/hoa-don/detailhoadononline";
    }

    @GetMapping("/detailhoadononlinect/{id}/products")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductsForModal(
            @PathVariable int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer locTheoMauSac,
            @RequestParam(required = false) Integer locTheoSize,
            @RequestParam(required = false) Integer locTheoDanhMuc,
            @RequestParam(required = false) Integer locTheoChatLieu,
            @RequestParam(required = false) Integer locTheoHang,
            @RequestParam(defaultValue = "5") int size) {
        Page<ChiTietSanPham> sanPhamChiTiet = chiTietSanPhamService.findChiTietSanPhamJustOne(
                keyword, locTheoHang, locTheoDanhMuc, locTheoChatLieu, locTheoMauSac, locTheoSize,
                PageRequest.of(page, size)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("products", sanPhamChiTiet.getContent());
        response.put("currentPage", sanPhamChiTiet.getNumber());
        response.put("totalPages", sanPhamChiTiet.getTotalPages());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/huydh/{id}")
    public String huydonhang(@PathVariable int id, @RequestParam(value = "ghichu", defaultValue = "trong") String ghichu,
                             Model model, RedirectAttributes redirectAttributes) {
        HoaDon hd = hoaDonOnlService.detailHD(id);
        if (hd == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn.");
            return "redirect:/hoadononline/hienthi";
        }
        hd.setGhiChu(ghichu);
        hd.setTrangThai(6);  // Đặt trạng thái hủy
        hoaDonOnlService.updateHoaDon(hd, id);

        // Cập nhật lại số lượng sản phẩm
        List<HoaDonChiTietOnlCustom> chiTietHoaDonList = hoaDonChiTietOnlService.findByHoaDonId(hd);
        for (HoaDonChiTietOnlCustom ct : chiTietHoaDonList) {
            ChiTietSanPham dt = chiTietSanPhamService.findById(ct.getId());
            dt.setSoLuong(dt.getSoLuong() + ct.getSoLuong());
            chiTietSanPhamService.update(dt);
        }

        redirectAttributes.addFlashAttribute("thanhcong", "Hủy thành công");
        return "redirect:/hoadononline/hienthi";
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
            int idHoaDon = Integer.parseInt(payload.get("idHoaDon").toString());
            HoaDon hoaDon = hoaDonService.findById(idHoaDon);
            if (hoaDon.getTrangThai() != 2 && hoaDon.getTrangThai() != 3) {
                return ResponseEntity.badRequest().body(Map.of("message", "Hóa đơn  không thể cập nhật"));
            }
            int idHoaDonChiTiet = Integer.parseInt(payload.get("idHoaDonChiTiet").toString());
            int soLuongMoi = Integer.parseInt(payload.get("soLuongMoi").toString());
            // Gọi service cập nhật số lượng

            HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.findHoaDonChiTietByID(idHoaDonChiTiet);
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(hoaDonChiTiet.getIdChiTietSanPham().getId());
            // xử lí khi số lượng sản phảm trong kho không đủ
            if(hoaDonChiTiet.getDonGia()!=chiTietSanPham.getGiaBan()){
                return ResponseEntity.badRequest().body(Map.of("message","Giá của "+chiTietSanPham.getIdSanPham().getTenSanPham()+" đã thay đổi."));
            }
            // xử lí khi sản phẩm không còn hoạt động
            if (!chiTietSanPham.getTrangThai() || !chiTietSanPham.getIdSanPham().getTrangThai()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sản phẩm " + chiTietSanPham.getIdSanPham().getTenSanPham() + " ngừng không còn hoạt động"));
            }
            List<HoaDonChiTiet> lstHdct = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonIdAndIdChiTietSanPham(idHoaDon,chiTietSanPham.getId());
            int soLuongSPTrongHoaDon = lstHdct.stream().mapToInt(HoaDonChiTiet::getSoLuong).sum();
            int soLuongTruHoaDonDuocChon = lstHdct.stream().filter(hd->hd.getId()!=hoaDonChiTiet.getId())
                    .mapToInt(HoaDonChiTiet::getSoLuong).sum();
            if(hoaDon.getTrangThai()==3){
                if (chiTietSanPham.getSoLuong()+soLuongSPTrongHoaDon < soLuongMoi+soLuongTruHoaDonDuocChon) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Số lượng sản phẩm trong kho không đủ"));
                }
            }else{
                if(chiTietSanPham.getSoLuong()<soLuongMoi+soLuongTruHoaDonDuocChon){
                    System.out.println(soLuongMoi);
                    System.out.println(soLuongTruHoaDonDuocChon);
                    return ResponseEntity.badRequest().body(Map.of("message", "Số lượng sản phẩm trong kho không đủ"));
                }
            }


            //Cập nhật lại số lương ctsp nếu đang ở trang thái chờ lấy hàng
            if (hoaDon.getTrangThai() == 3) {
                chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() + (hoaDonChiTiet.getSoLuong() - soLuongMoi));
                chiTietSanPhamService.saveChiTietSanPham(chiTietSanPham);
            }
            hoaDonChiTiet.setSoLuong(soLuongMoi);
            hoaDonChiTietService.saveHoaDonChiTiet(hoaDonChiTiet);

            //Tính lại tổng tiền trong hoá đơn của khách
            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(idHoaDon);
            float tongTienMoi = 0;
            for (HoaDonChiTiet hdcts : hoaDonChiTiets) {
                tongTienMoi += hdcts.getSoLuong() * hdcts.getDonGia();
            }
            //xử lí tiền chênh lệch cho khách hàng với trường hợp đã thanh toán bằng vnpay
            hoaDon.setTongTien(tongTienMoi);
            hoaDonService.tinhLaiKhuyenMai(hoaDon);
            HoaDon hoaDon2 = hoaDonService.findById(idHoaDon);
            hoaDon2.setThanhTien(tongTienMoi + hoaDon.getPhiShip() - hoaDon.getTongTienGiam());
            float tienChenhLech = 0;
            if (hoaDon2.getLoaiThanhToan()) {
                //tinh so tien chenh lech neu khach đã thanh toan bang vnpay
                tienChenhLech = hoaDon2.getTienKhachDua() - hoaDon2.getThanhTien();
                if (tienChenhLech > 0) {
                    //nếu lớn khách trả nhiều tiền ho thì cần trả la khách tiền
                    hoaDon2.setTienThua(tienChenhLech);
                } else {
                    //nguược lại thì không cần trả tiền thừa cho khách
                    hoaDon2.setTienThua(0);
                }
            }
            hoaDonService.save(hoaDon2);
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
            int trangThai = 1; // Mặc định trạng thái = 1

            try {
                idHoaDon = Integer.parseInt(payload.get("idHoaDon").toString());
                idChiTietSanPham = Integer.parseInt(payload.get("idChiTietSanPham").toString());
                soLuong = Integer.parseInt(payload.get("soLuong").toString());
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
            HoaDon hoaDon = hoaDonService.findById(idHoaDon);
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(idChiTietSanPham);
            // Tạo đối tượng HoaDonChiTiet
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setIdHoaDon(hoaDon);
            hoaDonChiTiet.setIdChiTietSanPham(chiTietSanPham);
            //số lượng mặc định là 1
            hoaDonChiTiet.setSoLuong(soLuong);
            //giá bán mới nhất của sản phẩm
            hoaDonChiTiet.setDonGia(chiTietSanPham.getGiaBan());
            hoaDonChiTiet.setNgayTao(new Date());
            hoaDonChiTiet.setNgaySua(new Date());
            hoaDonChiTiet.setTrangThai(trangThai);
            // Gọi Service để thêm hoặc cập nhật hóa đơn chi tiết
            boolean result = hoaDonChiTietOnlService.themSPCTVaoHDCT(hoaDonChiTiet);
            if (result) {
                response.put("success", true);
                response.put("message", "Cập nhật hóa đơn chi tiết thành công.");
                List<HoaDonChiTiet> lstHDCT = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(hoaDon.getId());
                float tongTienMoi = 0;
                for (HoaDonChiTiet hoaDonChiTiet1 : lstHDCT) {
                    tongTienMoi += hoaDonChiTiet1.getDonGia() * hoaDonChiTiet1.getSoLuong();
                }
                hoaDon.setTongTien(tongTienMoi);
                hoaDon.setNgaySua(new Date());
                //tinh lai khuyen mai khi thay doi so luong san pham
                hoaDonService.tinhLaiKhuyenMai(hoaDon);
                HoaDon hoaDon2 = hoaDonService.findById(idHoaDon);
                hoaDon2.setThanhTien(tongTienMoi - hoaDon2.getTongTienGiam() + hoaDon2.getPhiShip());
                float tienChenhLech = 0;
                if (hoaDon2.getLoaiThanhToan()) {
                    //tinh so tien chenh lech neu khach đã thanh toan bang vnpay
                    tienChenhLech = hoaDon2.getTienKhachDua() - hoaDon2.getThanhTien();
                    if (tienChenhLech > 0) {
                        //nếu lớn khách trả nhiều tiền ho thì cần trả la khách tiền
                        hoaDon2.setTienThua(tienChenhLech);
                    } else {
                        //nguược lại thì không cần trả tiền thừa cho khách
                        hoaDon2.setTienThua(0);
                    }
                }

                hoaDonService.save(hoaDon2);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Số lượng trong kho không đủ");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi xử lý backend: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @DeleteMapping("/xoa-chi-tiet/{idHoaDon}/{idHoaDonChiTiet}")
    public ResponseEntity<Map<String, Object>> xoaChiTiet(@PathVariable int idHoaDon, @PathVariable int idHoaDonChiTiet) {
        Map<String, Object> response = new HashMap<>();
        HoaDon hoaDon = hoaDonService.findById(idHoaDon);
        if (hoaDon == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy thông tin hóa đơn"));
        }
        if (hoaDon.getTrangThai() != 2 && hoaDon.getTrangThai() != 3) {
            return ResponseEntity.badRequest().body(Map.of("message", "Hóa đơn không thể cập nhật"));
        }
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(idHoaDon);
        if (hoaDonChiTiets == null) {
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn chi tiết nào");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (hoaDonChiTiets.size() <= 1) {
            response.put("success", false);
            response.put("message", "Phải có tối thiểu 1 sản phẩm trong đơn hàng");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.findHoaDonChiTietByID(idHoaDonChiTiet);
        try {
            boolean deteteHDCT = hoaDonChiTietService.deleteHoaDonChiTiet(idHoaDonChiTiet);
            if (deteteHDCT == true) {
                if (hoaDon.getTrangThai() == 3) {
                    ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getIdChiTietSanPham();
                    chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() + hoaDonChiTiet.getSoLuong());
                    chiTietSanPham.setNgaySua(new Date());
                    chiTietSanPhamService.saveChiTietSanPham(chiTietSanPham);
                }
                hoaDon.setTongTien(hoaDon.getTongTien() - hoaDonChiTiet.getSoLuong() * hoaDonChiTiet.getDonGia());
                hoaDon.setNgaySua(new Date());
                hoaDonService.tinhLaiKhuyenMai(hoaDon);
                HoaDon hoaDon2 = hoaDonService.findById(idHoaDon);
                hoaDon2.setThanhTien(hoaDon2.getTongTien() - hoaDon2.getTongTienGiam() + hoaDon2.getPhiShip());
                if (hoaDon2.getLoaiThanhToan()) {
                    float tienChenhLech = 0;
                    //tinh so tien chenh lech neu khach đã thanh toan bang vnpay
                    tienChenhLech = hoaDon2.getTienKhachDua() - hoaDon2.getThanhTien();
                    if (tienChenhLech > 0) {
                        //nếu lớn khách trả nhiều tiền ho thì cần trả la khách tiền
                        hoaDon2.setTienThua(tienChenhLech);
                    } else {
                        //nguược lại thì không cần trả tiền thừa cho khách
                        hoaDon2.setTienThua(0);
                    }
                }
                hoaDonService.save(hoaDon2);
                response.put("success", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                return ResponseEntity.ok(response);
            }
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
                List<HoaDonChiTiet> lstHoaDonChiTiet = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(idhoadon);
                for (HoaDonChiTiet hd : lstHoaDonChiTiet) {
                    if (hd.getSoLuong() > hd.getIdChiTietSanPham().getSoLuong()) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Số lượng sản phẩm " + hd.getIdChiTietSanPham().getIdSanPham().getTenSanPham() + " màu " + hd.getIdChiTietSanPham().getIdMauSac().getTenMauSac() + " trong kho không đủ: Còn " + hd.getIdChiTietSanPham().getSoLuong() + " sản phẩm"));
                    } else if (!hd.getIdChiTietSanPham().getTrangThai()||!hd.getIdChiTietSanPham().getIdSanPham().getTrangThai()) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Sản phẩm " + hd.getIdChiTietSanPham().getIdSanPham().getTenSanPham() + " màu " + hd.getIdChiTietSanPham().getIdMauSac().getTenMauSac() +" size "+hd.getIdChiTietSanPham().getIdSize().getTenSize() + " không còn hoạt động "));
                    }
                }
                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
                if (!doiTT) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
                }
                return ResponseEntity.ok(Map.of("message", "Chờ lấy hàng"));
            } else if (trangthai == 4) {
                if (hoaDon.getTenNguoiGiao() == null || hoaDon.getTenNguoiGiao().isEmpty() || hoaDon.getSdtNguoiGiao() == null || hoaDon.getSdtNguoiGiao().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng cập nhật thông tin người giao hàng"));
                }
                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
                if (!doiTT) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
                }
                return ResponseEntity.ok(Map.of("message", "Đang giao"));

            } else if (trangthai == 5) {
                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
                if (!doiTT) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
                }
                return ResponseEntity.ok(Map.of("message", "Đã giao"));
            } else if (trangthai == 6) {
                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
                if (!doiTT) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
                }
                return ResponseEntity.ok(Map.of("message", "Đã hủy hóa đơn"));
            } else if (trangthai == 11) {
                boolean doiTT = lichSuTrnngThaiService.doiTrangThaiDonHang(idhoadon, ghichu, trangthai);
                if (!doiTT) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
                }
                return ResponseEntity.ok(Map.of("message", "Giao hàng thất bại"));
            }
            return ResponseEntity.ok(Map.of("message", "Đổi trạng thái thất bại"));

        } catch (Exception e) {
            // Ghi lại log lỗi để kiểm tra sau
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Đổi trạng thái thất bại"));
        }

    }

    @PostMapping("/updatehoadon")
    @ResponseBody
    public ResponseEntity<?> capNhatThongTinHoaDon(@RequestBody Map<String, Object> hoaDon) {
        try {
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
            return ResponseEntity.ok(Map.of("message", "Cập nhật thông tin giao hàng thành công", "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi cập nhật thông tin đơn hàng"));
        }

    }

    // tìm kiếm hóa đơn
    @GetMapping("/hoa-don")
    public String viewHoaDon(Model model,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "startDate", required = false)
                             @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                             @RequestParam(value = "endDate", required = false)
                             @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                             @RequestParam(value = "page", defaultValue = "0") int page) {
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        // Nếu chỉ có startDate mà không có endDate thì gán endDate là hôm nay
        if (startDate != null && endDate == null) {
            endDate = new Date();
        }

        // Nếu chỉ có endDate mà không có startDate thì gán startDate là ngày rất xa
        if (startDate == null && endDate != null) {
            startDate = new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime();
        }

        Page<HoaDonOnlCustom> listHoaDonTatCa = hoaDonOnlService.searchHoaDon(keyword, startDate, endDate, page, size);
        Integer sizeTatCa = listHoaDonTatCa.getContent().size();
        model.addAttribute("sizeTatCa", sizeTatCa);
        System.out.println("timkiem: " + listHoaDonTatCa.getContent().size());

        model.addAttribute("listHoaDonTatCa", listHoaDonTatCa.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listHoaDonTatCa.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

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

        return "admin/hoa-don/hoadononline"; // view tương ứng
    }


    //end tìm kiếm hóa đơn


    //Xác nhận hóa đơn chi tiết
    @PostMapping("/xacnhan/{id}")
    @ResponseBody
    public ResponseEntity<?> xacNhanHoaDon(@PathVariable int id) {
//        try {
//            hoaDonChiTietOnlService.xacNhanHoaDon(id);
//            return ResponseEntity.ok(Map.of("success", true));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//        }
        return null;
    }


    @GetMapping("/in-hoadon/{id}")
    public String inHoaDon(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        HoaDon hoaDon = banHangTaiQuayService.getHoaDonByID(id);
        List<HoaDonChiTiet> hdct = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(id);

        if (hoaDon == null) {
            redirectAttributes.addFlashAttribute("error", "Hóa đơn không tồn tại!");
            return "redirect:/hoadononline/hienthi"; // Quay về trang bán hàng nếu không tìm thấy
        }

        System.out.println("list hoa don: " + hdct.size());

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("hdct", hdct);
        return "admin/hoa-don/inhoadononline";
    }


    @GetMapping("/export/pdf/{id}")
    public ResponseEntity<byte[]> exportHoaDonPDF(@PathVariable Integer id) throws IOException {
        HoaDon hoaDon = hoaDonService.findById(id);
        List<HoaDonChiTiet> hdct = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(id);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(50, 50, 50, 50);

        // Font setup
        PdfFont boldFont = PdfFontFactory.createFont(ResourceUtils.getFile("classpath:static/fonts/Roboto-Regular.ttf").getAbsolutePath(), PdfEncodings.IDENTITY_H, true);

        // Header
        Paragraph header = new Paragraph("HÓA ĐƠN BÁN HÀNG")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(header);

        // Company info
        Paragraph companyInfo = new Paragraph()
                .add(new Text("Tiệm giày Sneakers_Nice\n").setFont(boldFont).setFontSize(12))
                .add("Địa chỉ: Phú Đô, Mỹ Đình, Từ Liêm, Hà Nội\n")
                .add("Điện thoại: 0123 456 789\n")
                .add("Email: Sneakers_Nice@gmail.com")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(companyInfo);

        // Invoice info - 2 columns
        float[] columnWidths = {1, 1};
        Table invoiceInfoTable = new Table(columnWidths);
        invoiceInfoTable.setWidth(UnitValue.createPercentValue(100));

        Cell leftCell = new Cell()
                .add(new Paragraph("Mã hóa đơn: ").setFont(boldFont))
                .add(new Paragraph(hoaDon.getMaHoaDon()))
                .add(new Paragraph("Ngày tạo: ").setFont(boldFont))
                .add(new Paragraph(sdf.format(hoaDon.getNgayTao())))
                .add(new Paragraph("Nhân viên: ").setFont(boldFont))
                .add(new Paragraph(hoaDon.getIdNhanVien() != null ? hoaDon.getIdNhanVien().getHoVaTen() : "Không có"))
                .setBorder(Border.NO_BORDER);

        Cell rightCell = new Cell()
                .add(new Paragraph("Khách hàng: ").setFont(boldFont))
                .add(new Paragraph(hoaDon.getTenNguoiNhan()))
                .add(new Paragraph("Điện thoại: ").setFont(boldFont))
                .add(new Paragraph(hoaDon.getSdtNguoiNhan()))
                .add(new Paragraph("Địa chỉ: ").setFont(boldFont))
                .add(new Paragraph(hoaDon.getDiaChiChiTiet()))
                .setBorder(Border.NO_BORDER);

        invoiceInfoTable.addCell(leftCell);
        invoiceInfoTable.addCell(rightCell);
        document.add(invoiceInfoTable);

        // Line separator
        document.add(new LineSeparator(new SolidLine()).setMarginTop(10).setMarginBottom(10));

        // Products table
        Table productsTable = new Table(new float[]{3, 1, 1, 1});
        productsTable.setWidth(UnitValue.createPercentValue(100));

        productsTable.addHeaderCell(new Cell().add(new Paragraph("Sản phẩm").setFont(boldFont)));
        productsTable.addHeaderCell(new Cell().add(new Paragraph("SL").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER));
        productsTable.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setFont(boldFont)).setTextAlignment(TextAlignment.RIGHT));
        productsTable.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setFont(boldFont)).setTextAlignment(TextAlignment.RIGHT));

        DecimalFormat currencyFormat = new DecimalFormat("#,##0");

        for (HoaDonChiTiet ct : hdct) {
            productsTable.addCell(new Cell().add(new Paragraph(ct.getIdChiTietSanPham().getIdSanPham().getTenSanPham())));
            productsTable.addCell(new Cell().add(new Paragraph(String.valueOf(ct.getSoLuong()))).setTextAlignment(TextAlignment.CENTER));
            productsTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(ct.getDonGia()) + " đ")).setTextAlignment(TextAlignment.RIGHT));
            productsTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(ct.getSoLuong() * ct.getDonGia()) + " đ")).setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(productsTable);

        // Summary
        Table summaryTable = new Table(new float[]{3, 1});
        summaryTable.setWidth(UnitValue.createPercentValue(50));
        summaryTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        summaryTable.setMarginTop(20);

        summaryTable.addCell(new Cell().add(new Paragraph("Tổng tiền hàng:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(hoaDon.getTongTien()) + " đ")).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        summaryTable.addCell(new Cell().add(new Paragraph("Giảm giá:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(new Paragraph("-" + currencyFormat.format(hoaDon.getTongTienGiam()) + " đ")).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        summaryTable.addCell(new Cell().add(new Paragraph("Phí vận chuyển:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(hoaDon.getPhiShip()) + " đ")).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        summaryTable.addCell(new Cell().add(new Paragraph("Tổng thanh toán:").setFont(boldFont).setFontSize(14)).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(hoaDon.getThanhTien()) + " đ").setFont(boldFont).setFontSize(14)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        document.add(summaryTable);

        // Footer
        Paragraph footer = new Paragraph()
                .add("\n\nCảm ơn quý khách đã sử dụng dịch vụ!\n")
                .add("Hóa đơn có giá trị từ ngày " +
                        (hoaDon.getNgayTao() != null ? sdf.format(hoaDon.getNgayTao()) : "N/A"))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10)
                .setMarginTop(30);
        document.add(footer);

        document.close();

        byte[] pdfBytes = out.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("hoadon_" + hoaDon.getMaHoaDon() + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }


}

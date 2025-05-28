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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.*;
import poly.edu.sneaker.Service.Implement.TrangThaiHelper;

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
import java.util.function.Function;
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
    @Autowired
    private NhanVienService nhanVienService;

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }
    @PostMapping("/MuaLai")
    public ResponseEntity<?> muaLaiDonHang(@RequestParam int idHoaDon) {

        HoaDon hoaDon = hoaDonService.findById(idHoaDon);
        if (hoaDon == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy hóa đơn"));
        }
        HoaDon hoaDon1 = new HoaDon();
        hoaDon1.setMaHoaDon(hoaDonService.taoMaHoaDon());
        hoaDon1.setGhiChu("Mua lại hoa đơn" + hoaDon.getMaHoaDon());
        hoaDon1.setNgayTao(new Date());
        hoaDon1.setNgaySua(new Date());
        hoaDon1.setTienKhachDua(0);
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
        hoaDon1.setLoaiHoaDon(true);
        hoaDon1.setLoaiThanhToan(false);
        hoaDon1.setTrangThai(2);
        hoaDonService.save(hoaDon1);

        NhanVien nhanVien = nhanVienService.getNhanVienByEmail(getCurrentUserEmail());
        if(nhanVien!=null){
            hoaDon1.setIdNhanVien(nhanVien);
        }
        if (hoaDon.getIdKhachHang() != null) {
            hoaDon1.setIdKhachHang(hoaDon.getIdKhachHang());
        }

        float tongTien = 0;
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(hoaDon.getId());
        // Gộp các HoaDonChiTiet theo idChiTietSanPham
        Map<Integer, HoaDonChiTiet> gopChiTietMap = new HashMap<>();

        for (HoaDonChiTiet hd : hoaDonChiTiets) {
            int idCTSP = hd.getIdChiTietSanPham().getId();
            HoaDonChiTiet daCo = gopChiTietMap.get(idCTSP);
            if (daCo == null) {
                // Clone đối tượng ban đầu (chỉ lấy những gì cần)
                HoaDonChiTiet moi = new HoaDonChiTiet();
                moi.setIdChiTietSanPham(hd.getIdChiTietSanPham());
                moi.setSoLuong(hd.getSoLuong());
                gopChiTietMap.put(idCTSP, moi);
            } else {
                daCo.setSoLuong(daCo.getSoLuong() + hd.getSoLuong());
            }
        }
        for (HoaDonChiTiet hdGop : gopChiTietMap.values()) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(hdGop.getIdChiTietSanPham().getId());
            HoaDonChiTiet hd1 = new HoaDonChiTiet();
            hd1.setIdHoaDon(hoaDon1);
            hd1.setIdChiTietSanPham(chiTietSanPham);
            hd1.setSoLuong(hdGop.getSoLuong());
            hd1.setTongTrongLuong(chiTietSanPham.getTrongLuong() * hd1.getSoLuong());
            hd1.setDonGia(chiTietSanPham.getGiaBan());
            hd1.setGhiChu("Đơn hàng mua lại từ hóa đơn " + hoaDon.getMaHoaDon());
            hd1.setNgayTao(new Date());
            hd1.setNgaySua(new Date());
            hd1.setTrangThai(1);
            hoaDonChiTietService.saveHoaDonChiTiet(hd1);
            tongTien += hd1.getSoLuong() * hd1.getDonGia();
        }
        hoaDon1.setTongTien(tongTien);
        hoaDon1.setThanhTien(hoaDon1.getTongTien()+hoaDon.getPhiShip());

        hoaDonService.save(hoaDon1);
        TrangThaiDonHang trangThaiDonHang = new TrangThaiDonHang();
        trangThaiDonHang.setIdHoaDon(hoaDon1);
        trangThaiDonHang.setTrangThai(hoaDon1.getTrangThai());
        trangThaiDonHang.setNgayCapNhat(new Date());
        trangThaiDonHang.setGhiChu("Đơn hàng mua lại từ hóa đơn: "+hoaDon.getMaHoaDon()+" ngày "+hoaDon.getNgayTao());
        lichSuTrnngThaiService.saveLichSuTrangThai(trangThaiDonHang);
        return ResponseEntity.ok().body(Map.of("message", "Mua lại thành công", "URL", "hoadononline/detailhoadononlinect/" + hoaDon1.getId()));
    }

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page) {
<<<<<<< HEAD
        if (page < 0) {
            page = 0;
        }
        int size = 1;

=======
        int size = 5;
>>>>>>> origin/master
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
        model.addAttribute("currentPageTatCa", listHoaDonTatCa.getNumber());
        model.addAttribute("totalPagesTatCa", listHoaDonTatCa.getTotalPages());

        model.addAttribute("listHoaDonDH", listHoaDonDH.getContent());
        model.addAttribute("currentPageDH", listHoaDonDH.getNumber());
        model.addAttribute("totalPagesDH", listHoaDonDH.getTotalPages());

        model.addAttribute("listHoaDonCXN", listHoaDonCXN.getContent());
        model.addAttribute("currentPageCXN", listHoaDonCXN.getNumber());
        model.addAttribute("totalPagesCXN", listHoaDonCXN.getTotalPages());

        model.addAttribute("listHoaDonCLH", listHoaDonCLH.getContent());
        model.addAttribute("currentPageCLH", listHoaDonCLH.getNumber());
        model.addAttribute("totalPagesCLH", listHoaDonCLH.getTotalPages());

        model.addAttribute("listHoaDonDG", listHoaDonDG.getContent());
        model.addAttribute("currentPageDG", listHoaDonDG.getNumber());
        model.addAttribute("totalPagesDG", listHoaDonDG.getTotalPages());

        model.addAttribute("listHoaDonHT", listHoaDonHT.getContent());
        model.addAttribute("currentPageHT", listHoaDonHT.getNumber());
        model.addAttribute("totalPagesHT", listHoaDonHT.getTotalPages());

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
            int idHoaDonChiTiet = Integer.parseInt(payload.get("idHoaDonChiTiet").toString());
            int soLuongMoi = Integer.parseInt(payload.get("soLuongMoi").toString());

            HoaDon hoaDon = hoaDonService.findById(idHoaDon);
            if (hoaDon.getTrangThai() != 2 && hoaDon.getTrangThai() != 3) {
                return ResponseEntity.badRequest().body(Map.of("message", "Hóa đơn  không thể cập nhật"));
            }

            HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.findHoaDonChiTietByID(idHoaDonChiTiet);
            ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(hoaDonChiTiet.getIdChiTietSanPham().getId());
            // xử lí khi số lượng sản phảm trong kho không đủ
            if (hoaDonChiTiet.getDonGia() != chiTietSanPham.getGiaBan()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Giá của " + chiTietSanPham.getIdSanPham().getTenSanPham() + " đã thay đổi."));
            }
            // xử lí khi sản phẩm không còn hoạt động
            if (!chiTietSanPham.getTrangThai() || !chiTietSanPham.getIdSanPham().getTrangThai()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sản phẩm " + chiTietSanPham.getIdSanPham().getTenSanPham() + " ngừng không còn hoạt động"));
            }
            List<HoaDonChiTiet> lstHdct = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonIdAndIdChiTietSanPham(idHoaDon, chiTietSanPham.getId());
            int soLuongKhac = lstHdct.stream()
                    .filter(hd -> hd.getId() != hoaDonChiTiet.getId())
                    .mapToInt(HoaDonChiTiet::getSoLuong).sum();
            int tongSoLuongMongMuon = soLuongMoi + soLuongKhac;

            int soLuongSPTrongHoaDon = lstHdct.stream().mapToInt(HoaDonChiTiet::getSoLuong).sum();
            // Tính tồn kho thực tế
            int soLuongTon = hoaDon.getTrangThai() == 3
                    ? chiTietSanPham.getSoLuong() + soLuongSPTrongHoaDon
                    : chiTietSanPham.getSoLuong();
            System.out.println("số lợng tồn tính được" + soLuongTon);
            if (soLuongTon < tongSoLuongMongMuon) {
                return ResponseEntity.badRequest().body(Map.of("message", "Số lượng sản phẩm trong kho không đủ"));
            }

            //Cập nhật lại số lương ctsp nếu đang ở trang thái chờ lấy hàng
            if (hoaDon.getTrangThai() == 3) {
                int chenhlech = hoaDonChiTiet.getSoLuong() - soLuongMoi;
                chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() + chenhlech);
                chiTietSanPhamService.saveChiTietSanPham(chiTietSanPham);
            }
            // Cập nhật số lượng mới cho hóa đơn chi tiết
            hoaDonChiTiet.setSoLuong(soLuongMoi);
            hoaDonChiTietService.saveHoaDonChiTiet(hoaDonChiTiet);

            //Tính lại tổng tiền trong hoá đơn của khách
            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(idHoaDon);
            double tongTienMoi = hoaDonChiTiets.stream()
                    .mapToDouble(hdct -> hdct.getSoLuong() * hdct.getDonGia())
                    .sum();

            //xử lí tiền chênh lệch cho khách hàng với trường hợp đã thanh toán bằng vnpay
            hoaDon.setTongTien((float) tongTienMoi);
            hoaDonService.tinhLaiKhuyenMai(hoaDon);
            HoaDon hoaDon2 = hoaDonService.findById(idHoaDon);
            hoaDon2.setThanhTien((float) (tongTienMoi + hoaDon.getPhiShip() - hoaDon.getTongTienGiam()));
            // Xử lý tiền chênh lệch khi thanh toán qua VNPay
            if (hoaDon.getLoaiThanhToan()) {
                float tienChenhLech = hoaDon.getTienKhachDua() - hoaDon.getThanhTien();
                hoaDon.setTienThua(Math.max(tienChenhLech, 0));
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
            boolean isUndo = Boolean.parseBoolean(formData.getOrDefault("isUndo", "false").toString());
            int trangthaiMoi = Integer.parseInt(formData.get("trangThai").toString());

            HoaDon hoaDon = hoaDonService.findById(idhoadon);
            if (hoaDon == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Hóa đơn không tồn tại"));
            }

            // Validate theo trạng thái
            String validationError = validateStateChange(hoaDon, trangthaiMoi, isUndo);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(Map.of("message", validationError));
            }

            boolean success = lichSuTrnngThaiService.doiTrangThaiDonHang(
                    idhoadon, ghichu, trangthaiMoi, isUndo);

            return success
                    ? ResponseEntity.ok(Map.of("message", isUndo ? "Hoàn tác thành công" : "Đổi trạng thái thành công"))
                    : ResponseEntity.badRequest().body(Map.of("message", "Thao tác thất bại"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi hệ thống"));
        }
    }

    private String validateStateChange(HoaDon hoaDon, int trangThaiMoi, boolean isUndo) {
        int trangThaiHienTai = hoaDon.getTrangThai();
        List<HoaDonChiTiet> lstHoaDonChiTiet = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(hoaDon.getId());
        // Kiểm tra hoàn tác
        if (isUndo) {
            if (trangThaiHienTai == 1) return "Không thể hoàn tác từ trạng thái ban đầu";
            if (trangThaiMoi == 6 || trangThaiMoi == 11) return "Không thể hoàn tác về trạng thái hủy/giao thất bại";
            return null;
        }

        // Kiểm tra chuyển trạng thái bình thường
        switch (trangThaiMoi) {
            case 3: // Chờ lấy hàng
                for (HoaDonChiTiet hd : lstHoaDonChiTiet) {
                    if (hd.getSoLuong() > hd.getIdChiTietSanPham().getSoLuong()) {
                        return "Sản phẩm " + hd.getIdChiTietSanPham().getIdSanPham().getTenSanPham() + " không đủ, chỉ còn " + hd.getIdChiTietSanPham().getSoLuong() + " sản phẩm";
                    }
                }
                if (trangThaiHienTai != 2 && trangThaiHienTai != 10) {
                    return "Chỉ có thể xác nhận từ trạng thái chờ xác nhận";
                }
                break;
            case 4: // Đang giao
                if (trangThaiHienTai != 3) return "Chỉ có thể chuyển sang đang giao từ trạng thái chờ lấy hàng";
                if (StringUtils.isEmpty(hoaDon.getTenNguoiGiao()) || StringUtils.isEmpty(hoaDon.getSdtNguoiGiao())) {
                    return "Vui lòng cập nhật thông tin người giao";
                }
                break;
            case 6: // Hủy
                if (trangThaiHienTai == 5 || trangThaiHienTai == 6) {
                    return "Không thể hủy đơn ở trạng thái hiện tại";
                }
                break;
        }
        return null;
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

    @PostMapping("/quay-lai-trang-thai-truoc")
    public ResponseEntity<?> quayLaiTrangThaiTruoc(@RequestParam("idHoaDon") int idHoaDon) {
        // Lấy danh sách lịch sử trạng thái và chi tiết hóa đơn
        List<TrangThaiDonHang> lichSu = lichSuTrnngThaiService.findByIdHoaDon_IdOrderByNgayCapNhatDesc(idHoaDon);
        List<HoaDonChiTiet> lstHoaDonChiTiet = hoaDonChiTietOnlService.findHoaDonChiTietByHoaDonId(idHoaDon);

        if (lichSu.size() < 2) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không thể quay lại trạng thái trước"));
        }

        TrangThaiDonHang trangThaiHienTai = lichSu.get(0);
        TrangThaiDonHang trangThaiTruoc = lichSu.get(1);
        String tenTT=null;
        switch (trangThaiTruoc.getTrangThai()) {
            case 3:
                 tenTT = "Chờ lấy hàng";
                break;
            case 4:
                 tenTT = "Đang giao";
                break;
            case 5:
                 tenTT = "Đã giao";
                break;
            case 11:
                 tenTT = "Giao thất bại";
                break;
            case 6:
                 tenTT = "Đã hủy";
                break;
            case 2:
                 tenTT = "Chờ xác nhận";
                break;

        }
        // Nhóm các chi tiết hóa đơn theo ID chi tiết sản phẩm
        Map<Integer, Integer> tongSoLuongTheoCTSP = lstHoaDonChiTiet.stream()
                .collect(Collectors.groupingBy(
                        hd -> hd.getIdChiTietSanPham().getId(),
                        Collectors.summingInt(HoaDonChiTiet::getSoLuong)
                ));

        // Xử lý cập nhật số lượng
        if (trangThaiHienTai.getTrangThai() == 3 && trangThaiTruoc.getTrangThai() == 2 ||
                trangThaiHienTai.getTrangThai() == 4 && trangThaiTruoc.getTrangThai() == 11||
                trangThaiTruoc.getTrangThai()==6&&(trangThaiHienTai.getTrangThai()==3||trangThaiHienTai.getTrangThai()==4)) {

            // Cộng số lượng trở lại kho
            for (Map.Entry<Integer, Integer> entry : tongSoLuongTheoCTSP.entrySet()) {
                ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(entry.getKey());
                chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() + entry.getValue());
                chiTietSanPhamService.saveChiTietSanPham(chiTietSanPham);
            }
        } else if (trangThaiHienTai.getTrangThai() == 2 && trangThaiTruoc.getTrangThai() == 3 ||
                trangThaiHienTai.getTrangThai() == 11 && trangThaiTruoc.getTrangThai() == 4||
                trangThaiHienTai.getTrangThai()==6&&(trangThaiTruoc.getTrangThai()==3||trangThaiTruoc.getTrangThai()==4)) {

            // Kiểm tra số lượng tồn kho trước khi trừ
            for (Map.Entry<Integer, Integer> entry : tongSoLuongTheoCTSP.entrySet()) {
                ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(entry.getKey());
                if (chiTietSanPham.getSoLuong() < entry.getValue()) {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Sản phẩm " + chiTietSanPham.getIdSanPham().getTenSanPham() +
                            " hết hàng. Chỉ còn " + chiTietSanPham.getSoLuong() + " sản phẩm"));
                }
            }

            // Trừ số lượng từ kho sau khi đã kiểm tra đủ
            for (Map.Entry<Integer, Integer> entry : tongSoLuongTheoCTSP.entrySet()) {
                ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(entry.getKey());
                chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() - entry.getValue());
                chiTietSanPhamService.saveChiTietSanPham(chiTietSanPham);
            }
        }

        // Tạo bản ghi lịch sử mới
        TrangThaiDonHang lichSuMoi = new TrangThaiDonHang();
        lichSuMoi.setIdHoaDon(trangThaiHienTai.getIdHoaDon());
        lichSuMoi.setTrangThai(trangThaiTruoc.getTrangThai());
        lichSuMoi.setNgayCapNhat(new Date());
        lichSuMoi.setGhiChu("Quay lại trạng thái trước");

        HoaDon hoaDon = hoaDonService.findById(idHoaDon);
        lichSuTrnngThaiService.saveLichSuTrangThai(lichSuMoi);
        hoaDon.setTrangThai(lichSuMoi.getTrangThai());
        hoaDonService.save(hoaDon);

        return ResponseEntity.ok().body(Collections.singletonMap("message", "Đã qua lại trạng thái "+tenTT));
    }
}

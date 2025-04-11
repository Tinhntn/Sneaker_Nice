package poly.edu.sneaker.Controller;


import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.BanHangTaiQuayService;
import poly.edu.sneaker.Service.HoaDonService;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import poly.edu.sneaker.Service.NhanVienService;

import java.io.*;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/banhangtaiquay")
public class BanHangTaiQuayController {
    @Autowired
    BanHangTaiQuayService banHangTaiQuayService ;
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    NhanVienService nhanVienService;
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }
    @GetMapping("/hienthi")
    public String bhtq(Model model, @RequestParam(defaultValue = "0") int page) {
        int size = 5;
        //list chitietsanpham phan trang


        Page<ChiTietSanPham> CTSP = banHangTaiQuayService.DanhSachSanPhamPhanTrang(page,size);
        model.addAttribute("CTSP", CTSP.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", CTSP.getTotalPages());
        List<HoaDon> getAllhd = banHangTaiQuayService.getAllHoaDon();
        model.addAttribute("listHoaDon", getAllhd);

        List<ChatLieu> lstChatLieu = banHangTaiQuayService.getAllChatLieuTimKiem();
        model.addAttribute("lstChatLieu", lstChatLieu);
        List<DanhMuc> lstDanhMuc = banHangTaiQuayService.getAllDanhMucTimKiem();
        model.addAttribute("lstDanhMuc", lstDanhMuc);
        List<Hang> lstHang = banHangTaiQuayService.getAllHangTimKiem();
        model.addAttribute("lstHang", lstHang);
        List<Size> lstSize = banHangTaiQuayService.getAllSizeTimKiem();
        model.addAttribute("lstSize", lstSize);
        List<MauSac> lstMauSac = banHangTaiQuayService.getAllMauSacTimKiem();
        model.addAttribute("lstMauSac", lstMauSac);

        return "admin/banhangtaiquay/banhangtaiquay";
    }

    @GetMapping("/timkiemctsp")
    public String timkiemctsp(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String tenSanPham,
                              @RequestParam(required = false) Integer idhd,
                              @RequestParam(required = false) Integer idSize,
                              @RequestParam(required = false) Integer idMauSac,
                              @RequestParam(required = false) Integer idDanhMuc,
                              @RequestParam(required = false) Integer idHang,
                              @RequestParam(required = false) Integer idChatLieu) {
        int size = 5;

        // Lấy danh sách sản phẩm theo bộ lọc
        Page<ChiTietSanPham> CTSP = banHangTaiQuayService.timKiemSanPham(page, size, tenSanPham, idSize, idMauSac, idDanhMuc, idHang, idChatLieu);

        // Truy vấn danh sách hóa đơn chờ
        List<HoaDon> getAllhd = banHangTaiQuayService.getAllHoaDon();
        model.addAttribute("listHoaDon", getAllhd);

        // Truy vấn thông tin hóa đơn được chọn nếu có
        if (idhd != null) {
            HoaDon hd = hoaDonService.findById(idhd);
            model.addAttribute("hoadonduocchon", hd);
            List<HoaDonChiTiet> listHDCT = banHangTaiQuayService.danhSachChiTietHoaDonByIDHD(idhd);
            model.addAttribute("listHDCT", listHDCT);
            Double tongtiencthd = banHangTaiQuayService.tongTienCTHD(idhd);
            model.addAttribute("tongtiencthd", tongtiencthd);
        }

        // Truyền danh sách sản phẩm tìm kiếm vào model
        model.addAttribute("CTSP", CTSP.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", CTSP.getTotalPages());

        // Truyền các danh sách danh mục lọc
        model.addAttribute("lstSize", banHangTaiQuayService.getAllSizeTimKiem());
        model.addAttribute("lstMauSac", banHangTaiQuayService.getAllMauSacTimKiem());
        model.addAttribute("lstDanhMuc", banHangTaiQuayService.getAllDanhMucTimKiem());
        model.addAttribute("lstHang", banHangTaiQuayService.getAllHangTimKiem());
        model.addAttribute("lstChatLieu", banHangTaiQuayService.getAllChatLieuTimKiem());

        return "admin/banhangtaiquay/banhangtaiquay";
    }




    @PostMapping("/taohoadoncho")
    public String taoHoaDon(@ModelAttribute("hoadon") HoaDon hd, Model model,
                            RedirectAttributes redirectAttributes,
                            @RequestParam(defaultValue = "0") int page) {
        NhanVien nhanVien = nhanVienService.getNhanVienByEmail(getCurrentUserEmail());

        List<HoaDon> list = banHangTaiQuayService.getAllHoaDon();
        if (list.size() >= 5) {
            redirectAttributes.addFlashAttribute("error", "Chỉ được tạo tối đa 5 hóa đơn chờ.");
            return "redirect:/banhangtaiquay/hienthi";
        } else {

            // Tạo mã hóa đơn gồm 5 chữ số ngẫu nhiên
            Random random = new Random();
            int soNgauNhien = 10000 + random.nextInt(90000); // Sinh số từ 10000 đến 99999
            String maHoaDon = "HD" + soNgauNhien; // Kết hợp tiền tố "HD"
            hd.setIdNhanVien(nhanVien);
            hd.setMaHoaDon(maHoaDon); // Gán mã hóa đơn cho đối tượng
            hd.setLoaiHoaDon(false);
            hd.setThanhTien(0);
            hd.setPhiShip(0);
            hd.setTienThua(0);
            hd.setTongTien(0);
            hd.setTongTienGiam(0);
            hd.setTrangThai(0);
            banHangTaiQuayService.taoHoaDonCho(hd); // Lưu hóa đơn
            return "redirect:/banhangtaiquay/hienthi";
        }
    }

    @GetMapping("/showhoadoncho/{id}")
    public String detailHD(@PathVariable Integer id, Model model, @RequestParam(defaultValue = "0") int page
                           ) {
        int size = 5;
        Page<ChiTietSanPham> CTSP = banHangTaiQuayService.DanhSachSanPhamPhanTrang(page, size);
        model.addAttribute("CTSP", CTSP.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", CTSP.getTotalPages());
        HoaDon hd = hoaDonService.findById(id);
        model.addAttribute("listHoaDon", hd);
        HoaDon hd1 = hoaDonService.findById(id);
        model.addAttribute("hoadonduocchon", hd1); // Truyền hóa đơn đang chọn
        List<HoaDon> list = banHangTaiQuayService.getAllHoaDon();
        model.addAttribute("listHoaDon", list);
        List<HoaDonChiTiet> listHDCT = banHangTaiQuayService.danhSachChiTietHoaDonByIDHD(id);
        model.addAttribute("listHDCT",listHDCT);
        Double tongtiencthd = banHangTaiQuayService.tongTienCTHD(id);
        model.addAttribute("tongtiencthd",tongtiencthd);

        List<ChatLieu> lstChatLieu = banHangTaiQuayService.getAllChatLieuTimKiem();
        model.addAttribute("lstChatLieu", lstChatLieu);
        List<DanhMuc> lstDanhMuc = banHangTaiQuayService.getAllDanhMucTimKiem();
        model.addAttribute("lstDanhMuc", lstDanhMuc);
        List<Hang> lstHang = banHangTaiQuayService.getAllHangTimKiem();
        model.addAttribute("lstHang", lstHang);
        List<Size> lstSize = banHangTaiQuayService.getAllSizeTimKiem();
        model.addAttribute("lstSize", lstSize);
        List<MauSac> lstMauSac = banHangTaiQuayService.getAllMauSacTimKiem();
        model.addAttribute("lstMauSac", lstMauSac);
        return "admin/banhangtaiquay/banhangtaiquay";
    }

    @PostMapping("/addCTHD/{ctspid}")
    public String addCTHD(
            @PathVariable("ctspid") Integer ctspid,   // Lấy id của chi tiết sản phẩm từ URL
            @RequestParam("soluong1") Integer soluong,   // Lấy số lượng từ form
            @RequestParam("idhd") Integer idhd,              // Lấy id của hóa đơn từ form
            RedirectAttributes redirectAttributes){
        try {
            ChiTietSanPham chiTietSanPham = banHangTaiQuayService.danhSachChiTietSPID(ctspid);
            HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
            // Gọi service để thêm sản phẩm vào hóa đơn
            HoaDonChiTiet hdct = new HoaDonChiTiet();
            List<HoaDonChiTiet> listcthd = banHangTaiQuayService.ktratrunghdct(chiTietSanPham.getId(), idhd);
            if (listcthd.size() >= 1) {
                redirectAttributes.addFlashAttribute("warning", "Sản phẩm đã tồn tại trong giỏ hàng.");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            hdct.setTrangThai(0);
            hdct.setIdChiTietSanPham(chiTietSanPham);
            hdct.setDonGia(chiTietSanPham.getGiaBan());
            hdct.setIdHoaDon(hd);
            hdct.setSoLuong(1);
            chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong()-1);
            banHangTaiQuayService.saveCTSP(chiTietSanPham);
            banHangTaiQuayService.addHoaDonCT(hdct);
            if(chiTietSanPham.getSoLuong()==0){
                chiTietSanPham.setTrangThai(false);
                banHangTaiQuayService.saveCTSP(chiTietSanPham);
            }
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("warning", "Có lỗi xảy ra khi thêm sản phẩm: " + e.getMessage());
        }
        // Chuyển hướng về trang hiển thị hóa đơn đang chọn
        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
    }
    @GetMapping("/deletehdc/{id}")
    public String deletehdc(Model model, @PathVariable Integer id,RedirectAttributes redirectAttributes) {
        List<HoaDonChiTiet> listHDCT = banHangTaiQuayService.danhSachChiTietHoaDonByIDHD(id);
        for (HoaDonChiTiet ct : listHDCT){
            ChiTietSanPham idctsp = ct.getIdChiTietSanPham();
            Integer slCTHD = ct.getSoLuong();
           ChiTietSanPham chiTietSanPham = banHangTaiQuayService.danhSachChiTietSPID(idctsp.getId());
            if (chiTietSanPham != null) {
                int soLuongBanDau = chiTietSanPham.getSoLuong();
                // Tính toán và cập nhật số lượng mới
                int soLuongMoi = soLuongBanDau + slCTHD;
                chiTietSanPham.setSoLuong(soLuongMoi);
                if (soLuongMoi>0){
                    chiTietSanPham.setTrangThai(true);
                }
                // Lưu thay đổi vào cơ sở dữ liệu
               banHangTaiQuayService.saveCTSP(chiTietSanPham);
            }
        }
        banHangTaiQuayService.xoaHD(id);
        redirectAttributes.addFlashAttribute("success", "Xóa hóa đơn thành công!");
        return "redirect:/banhangtaiquay/hienthi";
    }

    @GetMapping("/deletehdct/{id}")
    public String deletehdct(Model model, @PathVariable Integer id,
                             @RequestParam("idhd") Integer idhd,
                             RedirectAttributes redirectAttributes) {
        List<HoaDonChiTiet> listHDCT = banHangTaiQuayService.danhSachChiTietHoaDonByID(id);
        System.out.println("Danh sách HDCT: " + listHDCT.size());

        for (HoaDonChiTiet ct : listHDCT){
            ChiTietSanPham idctsp = ct.getIdChiTietSanPham();
            Integer slCTHD = ct.getSoLuong();
            ChiTietSanPham chiTietSanPham = banHangTaiQuayService.danhSachChiTietSPID(idctsp.getId());
            System.out.println(chiTietSanPham.getSoLuong());
            if (chiTietSanPham != null) {
                int soLuongBanDau = chiTietSanPham.getSoLuong();
                int soLuongMoi = soLuongBanDau + slCTHD;
                chiTietSanPham.setSoLuong(soLuongMoi);
                if (soLuongMoi>0){
                    chiTietSanPham.setTrangThai(true);
                }
                banHangTaiQuayService.saveCTSP(chiTietSanPham);
            }
        }
        banHangTaiQuayService.xoaHDCT(id);
        redirectAttributes.addFlashAttribute("success", "Xóa hóa đơn thành công!");
        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
    }
    @PostMapping("/updatehdct/{id}")
    public String updateSoLuong(@PathVariable("id") Integer id,
                                RedirectAttributes redirectAttributes,
                                @RequestParam("idhd") Integer idhd,
                                @RequestParam(value = "suahdct", required = false) String suahdct,
                                @RequestParam("soluong") String soLuongMoiStr) {
        // Kiểm tra nếu số lượng không hợp lệ (chứa chữ hoặc số âm)
        if (!soLuongMoiStr.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("error", "Số lượng không hợp lệ! Vui lòng nhập số nguyên dương.");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }

        Integer soLuongMoi = Integer.parseInt(soLuongMoiStr);
        if (soLuongMoi <= 0) {
            redirectAttributes.addFlashAttribute("error", "Số lượng phải là số nguyên dương!");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }

        // Tìm hóa đơn chi tiết theo ID
        HoaDonChiTiet hdct = banHangTaiQuayService.ChiTietHoaDonByID(id);
        if (hdct == null) {
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }

        ChiTietSanPham ctsp = banHangTaiQuayService.danhSachChiTietSPID(hdct.getIdChiTietSanPham().getId());

        // Xử lý tăng, giảm số lượng hoặc nhập số lượng trực tiếp
        if ("tang".equals(suahdct)) {
            if (hdct.getSoLuong() >= ctsp.getSoLuong() + hdct.getSoLuong()) {
                redirectAttributes.addFlashAttribute("error", "Hàng trong kho không đủ!");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            hdct.setSoLuong(hdct.getSoLuong() + 1);
            ctsp.setSoLuong(ctsp.getSoLuong() - 1);
        } else if ("giam".equals(suahdct)) {
            if (hdct.getSoLuong() <= 1) {
                redirectAttributes.addFlashAttribute("error", "Số lượng tối thiểu là 1!");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            hdct.setSoLuong(hdct.getSoLuong() - 1);
            ctsp.setSoLuong(ctsp.getSoLuong() + 1);
        } else {
            if (soLuongMoi > ctsp.getSoLuong() + hdct.getSoLuong()) {
                redirectAttributes.addFlashAttribute("error", "Số lượng trong kho không đủ!");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            ctsp.setSoLuong(ctsp.getSoLuong() + hdct.getSoLuong() - soLuongMoi);
            hdct.setSoLuong(soLuongMoi);
        }
        // Cập nhật trạng thái sản phẩm
        if (ctsp.getSoLuong() <= 0) {
            ctsp.setTrangThai(false);
        } else {
            ctsp.setTrangThai(true);
        }

        // Lưu dữ liệu vào database
        banHangTaiQuayService.saveCTSP(ctsp);
        banHangTaiQuayService.saveHDCT(hdct);

        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
    }
    @PostMapping("/thanhtoan")
    public String thanhtoan(@RequestParam("idHoaDon") Integer idhd,
                                RedirectAttributes redirectAttributes,
                            @RequestParam(value = "tienKhachDua", defaultValue = "0") float tienKhachDua,
                            @RequestParam(value = "tienThua", defaultValue = "0") float tienThua,
                                 @RequestParam("tongtiencthd") Float tongtiencthd,
                                 @RequestParam("tongtiencthddatru") Float tongtiencthddatru,
                                 @RequestParam("sotiengiam") Float sotiengiam) {
        System.out.println(tongtiencthd);
        System.out.println(tongtiencthddatru);
        System.out.println(sotiengiam);
        // Kiểm tra nếu số lượng không hợp lệ (chứa chữ hoặc số âm)
        if (tienKhachDua <= 0) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập tiền khách đưa!");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }

        // Kiểm tra nếu số tiền khách đưa nhỏ hơn tổng tiền cần thanh toán

        // Tìm hóa đơn chi tiết theo ID

        HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
        if(hd.getIdKhuyenMai()!= null){
            KhuyenMai km = banHangTaiQuayService.timKhuyenMaiQuaMa(hd.getIdKhuyenMai().getMaKhuyenMai());
            if(km.getDaSuDung()>= km.getSoLuong()){
                redirectAttributes.addFlashAttribute("error", "Khuyến mãi này không đủ số lượng");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            km.setDaSuDung(km.getDaSuDung()+1);
            banHangTaiQuayService.saveKM(km);
        }
        hd.setTrangThai(1);
        hd.setThanhTien(tongtiencthddatru);
        hd.setTongTienGiam(sotiengiam);
        hd.setTongTien(tongtiencthd); // Cập nhật tổng doanh thu cho hóa đơn
        if (tongtiencthd<=0){
            redirectAttributes.addFlashAttribute("error", "Chưa có sản phẩm!");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }
        banHangTaiQuayService.saveHoaDon(hd);
        redirectAttributes.addFlashAttribute("success", "Thanh Toán Thành Công");
        return "redirect:/banhangtaiquay/in-hoadon/" + idhd;
    }
    @PostMapping("/timkiemidquasdtkh")
    public String timsdtkh( Model model,@RequestParam("idHoaDon") Integer idhd,
                                    @RequestParam("sdt") String sdt,
                            RedirectAttributes redirectAttributes) {
        KhachHang khachHang = banHangTaiQuayService.timIDQuaSDTKH(sdt);
        HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
        if (khachHang != null) {
            hd.setIdKhachHang(khachHang);
            System.out.println(khachHang);
            banHangTaiQuayService.saveHoaDon(hd);
            redirectAttributes.addFlashAttribute("success", "Thêm khách hàng " + khachHang.getTenKhachHang() + " vào hóa đơn thành công");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng với số điện thoại: " + sdt);
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }
        }
        @PostMapping("/xoakhachhangkhoihoadon")
    public String xoakh( Model model,@RequestParam("idHoaDon") Integer idhd,
                            RedirectAttributes redirectAttributes) {
        HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
            hd.setIdKhachHang(null);
            banHangTaiQuayService.saveHoaDon(hd);
            redirectAttributes.addFlashAttribute("success", "Bạn đã hủy khách hàng khỏi hóa đơn");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }
    @PostMapping("/timkiemKhuyenMaiQuaMaKM")
    public String timkm( Model model,@RequestParam("idHoaDon") Integer idhd,
                            @RequestParam("makm") String makm,
                            RedirectAttributes redirectAttributes) {
        KhuyenMai km = banHangTaiQuayService.timKhuyenMaiQuaMa(makm);
        HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
        Double tongtiencthd = banHangTaiQuayService.tongTienCTHD(idhd);

        if (km != null) {
            if (km.getSoLuong()<1){
                redirectAttributes.addFlashAttribute("error", "Số lượng mã khuyến mãi không đủ bạn hãy chọn mã khác");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }else if (km.getDieuKienApDung()>tongtiencthd){
                redirectAttributes.addFlashAttribute("error", "Hóa đơn của bạn chưa đủ điều kiện để dùng");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            hd.setIdKhuyenMai(km);
            banHangTaiQuayService.saveHoaDon(hd);
            redirectAttributes.addFlashAttribute("success", "Thêm" + km.getMaKhuyenMai() + " vào hóa đơn thành công");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy mã khuyến mãi này");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }
    }
    @PostMapping("/xoakhuyenmaikhoihoadon")
    public String xoakm( Model model,@RequestParam("idHoaDon") Integer idhd,
                         RedirectAttributes redirectAttributes) {
        HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
        hd.setIdKhuyenMai(null);
        banHangTaiQuayService.saveHoaDon(hd);
        redirectAttributes.addFlashAttribute("success", "Bạn đã hủy mã khuyến mãi khỏi hóa đơn");
        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
    }

    @PostMapping("/tienkhachdua")
    public String xuLyTienKhachDua(
            @RequestParam("idHoaDon") Integer idhd,
            @RequestParam("tongtiencthd") float tongTienCTHD,
            @RequestParam(value = "tienKhachDua", defaultValue = "0") float tienKhachDua,
            RedirectAttributes redirectAttributes) {
        System.out.println("id hoa don"+idhd);
        System.out.println("tien cthd "+tongTienCTHD);
        System.out.println("tien khach dua "+tienKhachDua);

        // Tính tiền thừa
        float tienThua = tienKhachDua - tongTienCTHD;
        System.out.println("tien thừa " + tienThua);

        if (tienThua<0){
            redirectAttributes.addFlashAttribute("error", "tiền khách đưa đang nhỏ hơn tiền cần thanh toán");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }

        // Đưa dữ liệu vào redirectAttributes để hiển thị lại trên giao diện
        redirectAttributes.addFlashAttribute("idHoaDon", idhd);
        redirectAttributes.addFlashAttribute("tongTienCTHD", tongTienCTHD);
        redirectAttributes.addFlashAttribute("tienKhachDua", tienKhachDua);
        redirectAttributes.addFlashAttribute("tienThua", tienThua);

        // Điều hướng về trang bán hàng tại quầy
        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
    }

    @GetMapping("/in-hoadon/{id}")
    public String inHoaDon(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        HoaDon hoaDon = banHangTaiQuayService.getHoaDonByID(id);
        List<HoaDonChiTiet> hdct = banHangTaiQuayService.danhSachChiTietHoaDonByIDHD(id);

        if (hoaDon == null) {
            redirectAttributes.addFlashAttribute("error", "Hóa đơn không tồn tại!");
            return "redirect:/banhangtaiquay/hienthi"; // Quay về trang bán hàng nếu không tìm thấy
        }

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("hdct", hdct);
        return "admin/banhangtaiquay/inhoadon";
    }


    @GetMapping("/export/pdf/{id}")
    public ResponseEntity<byte[]> exportHoaDonPDF(@PathVariable Integer id) throws IOException {
        HoaDon hoaDon = banHangTaiQuayService.getHoaDonByID(id);
        List<HoaDonChiTiet> hdct = banHangTaiQuayService.danhSachChiTietHoaDonByIDHD(id);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(50, 50, 50, 50);

        // Font setup
        PdfFont boldFont = PdfFontFactory.createFont(ResourceUtils.getFile("classpath:static/fonts/Flaticon.ttf").getAbsolutePath(), PdfEncodings.IDENTITY_H, true);

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

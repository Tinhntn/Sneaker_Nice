package poly.edu.sneaker.Controller;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.element.LineSeparator;

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


import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import poly.edu.sneaker.Service.NhanVienService;

import java.io.*;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/banhangtaiquay")
public class BanHangTaiQuayController {
    @Autowired
    BanHangTaiQuayService banHangTaiQuayService;
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
            redirectAttributes.addFlashAttribute("warning", "Chỉ được tạo tối đa 5 hóa đơn chờ.");
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
            hd.setTrangThai(10);
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
        model.addAttribute("listHDCT", listHDCT);
        Double tongtiencthd = banHangTaiQuayService.tongTienCTHD(id);
        model.addAttribute("tongtiencthd", tongtiencthd);

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
            RedirectAttributes redirectAttributes) {
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
            if(chiTietSanPham.getSoLuong()<=0){
                redirectAttributes.addFlashAttribute("warning", "Sản phẩm trong kho không đủ.");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            hdct.setTrangThai(0);
            hdct.setIdChiTietSanPham(chiTietSanPham);
            hdct.setDonGia(chiTietSanPham.getGiaBan());
            hdct.setIdHoaDon(hd);
            hdct.setSoLuong(1);
            chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() - 1);
            banHangTaiQuayService.saveCTSP(chiTietSanPham);
            banHangTaiQuayService.addHoaDonCT(hdct);
            if (chiTietSanPham.getSoLuong() == 0) {
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
    public String deletehdc(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        List<HoaDonChiTiet> listHDCT = banHangTaiQuayService.danhSachChiTietHoaDonByIDHD(id);
        for (HoaDonChiTiet ct : listHDCT) {
            ChiTietSanPham idctsp = ct.getIdChiTietSanPham();
            Integer slCTHD = ct.getSoLuong();
            ChiTietSanPham chiTietSanPham = banHangTaiQuayService.danhSachChiTietSPID(idctsp.getId());
            if (chiTietSanPham != null) {
                int soLuongBanDau = chiTietSanPham.getSoLuong();
                // Tính toán và cập nhật số lượng mới
                int soLuongMoi = soLuongBanDau + slCTHD;
                chiTietSanPham.setSoLuong(soLuongMoi);
                if (soLuongMoi > 0) {
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

        for (HoaDonChiTiet ct : listHDCT) {
            ChiTietSanPham idctsp = ct.getIdChiTietSanPham();
            Integer slCTHD = ct.getSoLuong();
            ChiTietSanPham chiTietSanPham = banHangTaiQuayService.danhSachChiTietSPID(idctsp.getId());
            if (chiTietSanPham != null) {
                int soLuongBanDau = chiTietSanPham.getSoLuong();
                int soLuongMoi = soLuongBanDau + slCTHD;
                chiTietSanPham.setSoLuong(soLuongMoi);
                if (soLuongMoi > 0) {
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
            redirectAttributes.addFlashAttribute("warning", "Số lượng không hợp lệ! Vui lòng nhập số nguyên dương.");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }

        Integer soLuongMoi = Integer.parseInt(soLuongMoiStr);
        if (soLuongMoi <= 0) {
            redirectAttributes.addFlashAttribute("warning", "Số lượng phải là số nguyên dương!");
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
                redirectAttributes.addFlashAttribute("warning", "Hàng trong kho không đủ!");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            hdct.setSoLuong(hdct.getSoLuong() + 1);
            ctsp.setSoLuong(ctsp.getSoLuong() - 1);
        } else if ("giam".equals(suahdct)) {
            if (hdct.getSoLuong() <= 1) {
                redirectAttributes.addFlashAttribute("warning", "Số lượng tối thiểu là 1!");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            hdct.setSoLuong(hdct.getSoLuong() - 1);
            ctsp.setSoLuong(ctsp.getSoLuong() + 1);
        } else {
            if (soLuongMoi > ctsp.getSoLuong() + hdct.getSoLuong()) {
                redirectAttributes.addFlashAttribute("warning", "Số lượng trong kho không đủ!");
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
                            @RequestParam(value = "tongtiencthd", defaultValue = "0") Float tongtiencthd,
                            @RequestParam(value = "tongtiencthddatru", defaultValue = "0") Float tongtiencthddatru,
                            @RequestParam(value = "sotiengiam",defaultValue = "0") Float sotiengiam
    ) {

        try {
            if (tongtiencthd <= 0) {
                redirectAttributes.addFlashAttribute("warning", "Chưa có sản phẩm trong hóa đơn!");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }

            if (tienKhachDua <= 0) {
                redirectAttributes.addFlashAttribute("warning", "Vui lòng nhập số tiền khách đưa!");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }

            if (tienKhachDua < tongtiencthddatru) {
                redirectAttributes.addFlashAttribute("warning", "Số tiền khách đưa không đủ để thanh toán!");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }

            HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
            if (hd == null) {
                redirectAttributes.addFlashAttribute("warning", "Không tìm thấy hóa đơn!");
                return "redirect:/banhangtaiquay";
            }

            // Kiểm tra khuyến mãi (nếu có)
            if (hd.getIdKhuyenMai() != null) {
                KhuyenMai km = banHangTaiQuayService.timKhuyenMaiQuaMa(hd.getIdKhuyenMai().getMaKhuyenMai());
                if (km != null) {
                    Date now = new Date();

                    if (Boolean.FALSE.equals(km.getTrangThai())) {
                        redirectAttributes.addFlashAttribute("warning", "Mã khuyến mãi không còn hoạt động!");
                        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
                    }

                    if (km.getNgayBatDau() != null && km.getNgayBatDau().after(now)) {
                        redirectAttributes.addFlashAttribute("warning", "Mã khuyến mãi chưa được áp dụng!");
                        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
                    }

                    if (km.getNgayKetThuc() != null && km.getNgayKetThuc().before(now)) {
                        redirectAttributes.addFlashAttribute("warning", "Mã khuyến mãi đã hết hạn!");
                        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
                    }

                    if (km.getDaSuDung() >= km.getSoLuong()) {
                        redirectAttributes.addFlashAttribute("warning", "Mã khuyến mãi đã được sử dụng hết!");
                        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
                    }

                    // Cập nhật số lần sử dụng khuyến mãi
                    km.setDaSuDung(km.getDaSuDung() + 1);
                    banHangTaiQuayService.saveKM(km);
                }
            }

            // Cập nhật thông tin hóa đơn
            hd.setTrangThai(1); // Đánh dấu là đã thanh toán
            hd.setThanhTien(tongtiencthddatru); // Số tiền sau khi giảm
            hd.setTongTienGiam(sotiengiam);
            hd.setTongTien(tongtiencthd); // Tổng tiền chưa giảm
            hd.setTienKhachDua(tienKhachDua);
            hd.setTienThua(tienThua);
            banHangTaiQuayService.saveHoaDon(hd);
            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công!");
            return "redirect:/banhangtaiquay/in-hoadon/" + idhd;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("warning", "Đã xảy ra lỗi khi thanh toán!");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }
    }


    @PostMapping("/timkiemidquasdtkh")
    public String timsdtkh(Model model, @RequestParam("idHoaDon") Integer idhd,
                           @RequestParam("sdt") String sdt,
                           RedirectAttributes redirectAttributes) {
        KhachHang khachHang = banHangTaiQuayService.timIDQuaSDTKH(sdt);
        HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
        if (khachHang != null) {
            hd.setIdKhachHang(khachHang);
            banHangTaiQuayService.saveHoaDon(hd);
            redirectAttributes.addFlashAttribute("success", "Thêm khách hàng " + khachHang.getTenKhachHang() + " vào hóa đơn thành công");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        } else {
            redirectAttributes.addFlashAttribute("warning", "Không tìm thấy khách hàng với số điện thoại: " + sdt);
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }
    }

    @PostMapping("/xoakhachhangkhoihoadon")
    public String xoakh(Model model, @RequestParam("idHoaDon") Integer idhd,
                        RedirectAttributes redirectAttributes) {
        HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
        hd.setIdKhachHang(null);
        banHangTaiQuayService.saveHoaDon(hd);
        redirectAttributes.addFlashAttribute("success", "Bạn đã hủy khách hàng khỏi hóa đơn");
        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
    }

    @PostMapping("/timkiemKhuyenMaiQuaMaKM")
    public String timkm(Model model,
                        @RequestParam("idHoaDon") Integer idhd,
                        @RequestParam("makm") String makm,
                        RedirectAttributes redirectAttributes) {
        try {
            KhuyenMai km = banHangTaiQuayService.timKhuyenMaiQuaMa(makm);
            HoaDon hd = banHangTaiQuayService.getHoaDonByID(idhd);
            Double tongtiencthd = banHangTaiQuayService.tongTienCTHD(idhd);
            if (hd == null) {
                redirectAttributes.addFlashAttribute("warning", "Không tìm thấy hóa đơn.");
                return "redirect:/banhangtaiquay";
            }
            if (km == null) {
                redirectAttributes.addFlashAttribute("warning", "Không tìm thấy mã khuyến mãi này.");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            // Kiểm tra trạng thái hoạt động
            if (Boolean.FALSE.equals(km.getTrangThai())) {
                redirectAttributes.addFlashAttribute("warning", "Mã khuyến mãi không hoạt động.");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            // Kiểm tra ngày bắt đầu và kết thúc
            Date now = new Date();
            if (km.getNgayBatDau() != null && km.getNgayBatDau().after(now)) {
                redirectAttributes.addFlashAttribute("warning", "Mã khuyến mãi chưa bắt đầu.");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            if (km.getNgayKetThuc() != null && km.getNgayKetThuc().before(now)) {
                redirectAttributes.addFlashAttribute("warning", "Mã khuyến mãi đã hết hạn.");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            // Kiểm tra số lượng mã khuyến mãi
            if (km.getSoLuong() < 1) {
                redirectAttributes.addFlashAttribute("warning", "Số lượng mã khuyến mãi không đủ. Hãy chọn mã khác.");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            // Kiểm tra điều kiện áp dụng
            if (km.getDieuKienApDung() > tongtiencthd) {
                redirectAttributes.addFlashAttribute("warning", "Hóa đơn của bạn chưa đủ điều kiện để áp dụng mã khuyến mãi.");
                return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
            }
            // Gán khuyến mãi vào hóa đơn
            hd.setIdKhuyenMai(km);
            banHangTaiQuayService.saveHoaDon(hd);
            redirectAttributes.addFlashAttribute("success", "Thêm mã " + km.getMaKhuyenMai() + " vào hóa đơn thành công.");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        } catch (Exception e) {
            e.printStackTrace(); // hoặc log lỗi bằng Logger
            redirectAttributes.addFlashAttribute("warning", "Đã xảy ra lỗi khi áp dụng mã khuyến mãi.");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }
    }


    @PostMapping("/xoakhuyenmaikhoihoadon")
    public String xoakm(Model model, @RequestParam("idHoaDon") Integer idhd,
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
            @RequestParam(value = "tienKhachDua", defaultValue = "0") float tienKhachDua,
            RedirectAttributes redirectAttributes) {

        // Lấy hóa đơn và tổng tiền ban đầu
        HoaDon hoaDon = banHangTaiQuayService.getHoaDonByID(idhd);
        Double tongTienGoc = banHangTaiQuayService.tongTienCTHD(idhd);
        if (hoaDon == null || tongTienGoc == null) {
            redirectAttributes.addFlashAttribute("warning", "Không tìm thấy hóa đơn hoặc dữ liệu không hợp lệ.");
            return "redirect:/banhangtaiquay";
        }

        float tongTienSauGiam = tongTienGoc.floatValue();

        // Áp dụng khuyến mãi nếu có
        if (hoaDon.getIdKhuyenMai() != null) {
            KhuyenMai km = hoaDon.getIdKhuyenMai();
            if (Boolean.TRUE.equals(km.getLoaiKhuyenMai())) {
                // Giảm theo số tiền
                tongTienSauGiam -= km.getGiaTriGiam();
            } else {
                // Giảm theo %
                float tienGiam = tongTienSauGiam * (km.getGiaTriGiam() / 100f);
                float mucGiamToiDa = km.getMucGiamGiaToiDa();
                tongTienSauGiam -= Math.min(tienGiam, mucGiamToiDa);
            }
        }

        // Đảm bảo không bị âm
        if (tongTienSauGiam < 0) tongTienSauGiam = 0;

        // Tính tiền thừa
        float tienThua = tienKhachDua - tongTienSauGiam;

        if (tienThua < 0) {
            redirectAttributes.addFlashAttribute("warning", "Tiền khách đưa nhỏ hơn số tiền cần thanh toán.");
            return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
        }

        // Truyền lại dữ liệu
        redirectAttributes.addFlashAttribute("idHoaDon", idhd);
        redirectAttributes.addFlashAttribute("tongTienCTHD", tongTienGoc);
        redirectAttributes.addFlashAttribute("tongTienSauGiam", tongTienSauGiam);
        redirectAttributes.addFlashAttribute("tienKhachDua", tienKhachDua);
        redirectAttributes.addFlashAttribute("tienThua", tienThua);

        return "redirect:/banhangtaiquay/showhoadoncho/" + idhd;
    }

    @GetMapping("/in-hoadon/{id}")
    public String inHoaDon(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        HoaDon hoaDon = banHangTaiQuayService.getHoaDonByID(id);
        List<HoaDonChiTiet> hdct = banHangTaiQuayService.danhSachChiTietHoaDonByIDHD(id);

        if (hoaDon == null) {
            redirectAttributes.addFlashAttribute("warning", "Hóa đơn không tồn tại!");
            return "redirect:/banhangtaiquay/hienthi"; // Quay về trang bán hàng nếu không tìm thấy
        }

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("hdct", hdct);
        return "admin/banhangtaiquay/inhoadon";
    }

    @GetMapping("/in/{id}")
    public ResponseEntity<byte[]> exportHoaDonPDF(@PathVariable Integer id) throws IOException {
        HoaDon hoaDon = banHangTaiQuayService.getHoaDonByID(id);
        List<HoaDonChiTiet> hdct = banHangTaiQuayService.danhSachChiTietHoaDonByIDHD(id);
        for (HoaDonChiTiet d: hdct){
            System.out.println(d.getSoLuong());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Sử dụng PdfWriter cho iText 7
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(50, 50, 50, 50);

        // Font
        PdfFont boldFont = PdfFontFactory.createFont(
                ResourceUtils.getFile("classpath:static/fonts/Roboto-Regular.ttf").getAbsolutePath(),
                PdfEncodings.IDENTITY_H, true
        );

        DecimalFormat currencyFormat = new DecimalFormat("#,##0");


        // Tiêu đề
        Paragraph header = new Paragraph("HÓA ĐƠN BÁN HÀNG")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(header);



        // Thông tin công ty
        Paragraph companyInfo = new Paragraph()
                .add(new Text("Shop giày Sneakers_Nice\n").setFont(boldFont).setFontSize(12))
                .add("Email: Sneakers_Nice@gmail.com")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(companyInfo);



        float[] columnWidths = {1, 1};
        Table invoiceInfoTable = new Table(columnWidths);
        invoiceInfoTable.setWidth(UnitValue.createPercentValue(100));

        String maHD = hoaDon.getMaHoaDon() != null ? hoaDon.getMaHoaDon() : "";
        String ngayTao = hoaDon.getNgayTao() != null ? sdf.format(hoaDon.getNgayTao()) : "";
        String nhanVien = (hoaDon.getIdNhanVien() != null && hoaDon.getIdNhanVien().getHoVaTen() != null)
                ? hoaDon.getIdNhanVien().getHoVaTen() : "Không có";

        String tenKH = (hoaDon.getIdKhachHang() != null && hoaDon.getIdKhachHang().getTenKhachHang() != null)
                ? hoaDon.getIdKhachHang().getTenKhachHang()
                : "Khach Le";

        String sdtKH = (hoaDon.getIdKhachHang() != null && hoaDon.getIdKhachHang().getSdt() != null)
                ? hoaDon.getIdKhachHang().getSdt()
                : "Không có";

        String diaChiKH = hoaDon.getDiaChiChiTiet() != null ? hoaDon.getDiaChiChiTiet() : "Không có";


        Cell leftCell = new Cell()
                .add(new Paragraph().add(new Text("Mã hóa đơn: ").setFont(boldFont)).add(maHD))
                .add(new Paragraph().add(new Text("Ngày tạo: ").setFont(boldFont)).add(ngayTao))
                .add(new Paragraph().add(new Text("Nhân viên: ").setFont(boldFont)).add(nhanVien))
                .setBorder(Border.NO_BORDER);

// ======= Cột PHẢI: thông tin khách hàng =======
        Cell rightCell = new Cell()
                .add(new Paragraph().add(new Text("Khách hàng: ").setFont(boldFont)).add(tenKH))
                .add(new Paragraph().add(new Text("Điện thoại: ").setFont(boldFont)).add(sdtKH))
                .add(new Paragraph().add(new Text("Địa chỉ: ").setFont(boldFont)).add(diaChiKH))
                .setBorder(Border.NO_BORDER);



        invoiceInfoTable.addCell(leftCell);
        invoiceInfoTable.addCell(rightCell);
        document.add(invoiceInfoTable);



        // Dòng phân cách
        document.add(new LineSeparator(new SolidLine()).setMarginTop(10).setMarginBottom(10));

        Table productsTable = new Table(new float[]{3, 1, 1, 1});
        productsTable.setWidth(UnitValue.createPercentValue(100));

        productsTable.addHeaderCell(new Cell().add(new Paragraph("Sản phẩm").setFont(boldFont)));
        productsTable.addHeaderCell(new Cell().add(new Paragraph("Màu sắc").setFont(boldFont)));
        productsTable.addHeaderCell(new Cell().add(new Paragraph("Size").setFont(boldFont)));
        productsTable.addHeaderCell(new Cell().add(new Paragraph("SL").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER));
        productsTable.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setFont(boldFont)).setTextAlignment(TextAlignment.RIGHT));
        productsTable.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setFont(boldFont)).setTextAlignment(TextAlignment.RIGHT));

        for (HoaDonChiTiet ct : hdct) {
            String tenSP = ct.getIdChiTietSanPham() != null &&
                    ct.getIdChiTietSanPham().getIdSanPham() != null &&
                    ct.getIdChiTietSanPham().getIdSanPham().getTenSanPham() != null
                    ? ct.getIdChiTietSanPham().getIdSanPham().getTenSanPham()
                    : "Không rõ";
            String mauSac = ct.getIdChiTietSanPham().getIdMauSac().getTenMauSac();
            String size = ct.getIdChiTietSanPham().getIdSize().getTenSize();
            int soLuong = ct.getSoLuong();
            Float donGia = ct.getDonGia() != 0 ? ct.getDonGia() : 0;
            Float thanhTien = soLuong * donGia;

            productsTable.addCell(new Cell().add(new Paragraph(tenSP)));
            productsTable.addCell(new Cell().add(new Paragraph(mauSac)));
            productsTable.addCell(new Cell().add(new Paragraph(size)));
            productsTable.addCell(new Cell().add(new Paragraph(String.valueOf(soLuong))).setTextAlignment(TextAlignment.CENTER));
            productsTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(donGia) + " đ")).setTextAlignment(TextAlignment.RIGHT));
            productsTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(thanhTien) + " đ")).setTextAlignment(TextAlignment.RIGHT));

        }

        document.add(productsTable);


        Table summaryTable = new Table(new float[]{3, 1});
        summaryTable.setWidth(UnitValue.createPercentValue(50));
        summaryTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        summaryTable.setMarginTop(20);

        // Kiểm tra null trước khi so sánh
        // Kiểm tra null trước khi gán giá trị và sử dụng giá trị mặc định là 0.0f nếu null
        Float tongTien = hoaDon.getTongTien() != 0 ? hoaDon.getTongTien() : 0.0f;
        Float giamGia = hoaDon.getTongTienGiam() != 0 ? hoaDon.getTongTienGiam() : 0.0f;
        Float thanhToan = hoaDon.getThanhTien() != 0 ? hoaDon.getThanhTien() : 0.0f;
        summaryTable.addCell(new Cell().add(new Paragraph("Tổng tiền hàng:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(tongTien) + " đ")).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(new Paragraph("Giảm giá:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(new Paragraph("-" + currencyFormat.format(giamGia) + " đ")).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(new Paragraph("Tổng thanh toán:").setFont(boldFont).setFontSize(14)).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(thanhToan) + " đ").setFont(boldFont).setFontSize(14)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        document.add(summaryTable);

        // Footer

        String ngayHieuLuc = hoaDon.getNgayTao() != null ? sdf.format(hoaDon.getNgayTao()) : "N/A";
        Paragraph footer = new Paragraph()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10)
                .setMarginTop(30);
        document.add(footer);

        document.close();

        byte[] pdfBytes = out.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("hoadon_" + maHD + ".pdf")
                .build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }











}

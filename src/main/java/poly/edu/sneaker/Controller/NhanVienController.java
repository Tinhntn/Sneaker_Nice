package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.DAO.NhanVienCustom;
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.ChucVuService;
import poly.edu.sneaker.Service.NhanVienService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/nhanvien")
public class NhanVienController {
    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    ChucVuService chucVuService;

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9]).{6,}$";
    private static final String SDT_PATTERN = "^0[0-9]{9,10}$";

    @GetMapping("/hienthi")
    public String hienThiNhanVien(Model model, @RequestParam(defaultValue = "0") int page,  @RequestParam(required = false) String keyword){
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<NhanVienCustom> nhanVienCustomPageable;

        if (keyword != null && !keyword.isEmpty()) {
            nhanVienCustomPageable = nhanVienService.search(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            nhanVienCustomPageable = nhanVienService.getAll(pageable);
        }

        List<ChucVu> listChucVu = chucVuService.getAll();

        System.out.println(nhanVienCustomPageable.getContent().size());


        model.addAttribute("listChucVu", listChucVu);
        model.addAttribute("nhanVienCustomList", nhanVienCustomPageable.getContent());
        model.addAttribute("currentPage", nhanVienCustomPageable.getNumber());
        model.addAttribute("totalPages", nhanVienCustomPageable.getTotalPages());
        return "admin/nhanVien/ListNhanVien";
    }

    @GetMapping("/addshow")
    public String showAddNhanVienPage(Model model) {
        model.addAttribute("listChucVu", chucVuService.getAll());
        return "admin/nhanVien/AddNhanVien";
    }


    @PostMapping("/add")
    public String add(@RequestParam("manhanvien") String maNhanVien,
                      @RequestParam("hovaten") String hoVaTen,
                      @RequestParam("idcv") String idcv,
                      @RequestParam(value = "ngaysinh", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySinh,
                      @RequestParam("gioitinh") Boolean gioiTinh,
                      @RequestParam("diachi") String diaChi,
                      @RequestParam("sdt") String sdt,
                      @RequestParam("email") String email,
                      @RequestParam("matkhau") String matKhau,
                      RedirectAttributes redirectAttributes
    ){

        System.out.println("hoten: " + hoVaTen);
        System.out.println("idcv: " + idcv);

        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã nhân viên không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Họ và tên không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (ngaySinh == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày sinh không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (diaChi == null || diaChi.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Địa chỉ không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (sdt == null || sdt.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }
        if (!sdt.matches(SDT_PATTERN)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại không đúng định dạng.");
            return "redirect:/nhanvien/hienthi";
        }

        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (!email.matches(EMAIL_PATTERN)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email không đúng định dạng.");
            return "redirect:/nhanvien/hienthi";
        }

        if (matKhau == null || matKhau.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }
        if (!matKhau.matches(PASSWORD_PATTERN)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không đúng định dạng.");
            return "redirect:/nhanvien/hienthi";
        }

        Integer idchucVu = Integer.parseInt(idcv);

        ChucVu chucVu = new ChucVu();
        chucVu.setId(idchucVu);

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setHoVaTen(hoVaTen);
        nhanVien.setIdChucVu(chucVu);
        nhanVien.setNgaySinh(ngaySinh);
        nhanVien.setGioiTinh(gioiTinh);
        nhanVien.setDiaChi(diaChi);
        nhanVien.setSdt(sdt);
        nhanVien.setEmail(email);
        nhanVien.setMatKhau(matKhau);
        nhanVien.setNgayTao(new Date());
        nhanVien.setTrangThai(true);

        try {
            nhanVienService.saveNhanVien(nhanVien);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên được thêm thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/nhanvien/hienthi";
        }
        return "redirect:/nhanvien/hienthi";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") int id, Model model){

        System.out.println("idNhanvien: " + id);

        NhanVien detailNhanVien = nhanVienService.findNhanVienById(id);

        System.out.println("ten: " + detailNhanVien.getHoVaTen());

//        List<ChucVu> comBoBoxChucVu = chucVuService.getAll();
        model.addAttribute("detailNhanVien", detailNhanVien);
//        model.addAttribute("comBoBoxChucVu", comBoBoxChucVu);
        return "admin/nhanVien/UpdateNhanVien";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Integer idNV,
                         @RequestParam("manhanvien") String maNhanVien,
                         @RequestParam("hovaten") String hoVaTen,
                         @RequestParam("idcv") String idcv,
                         @RequestParam(value = "ngaysinh", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySinh,
                         @RequestParam("gioitinh") Boolean gioiTinh,
                         @RequestParam("diachi") String diaChi,
                         @RequestParam("sdt") String sdt,
                         @RequestParam("email") String email,
                         @RequestParam("matkhau") String matKhau,
                         @RequestParam("trangthai") Boolean trangThai,
                         RedirectAttributes redirectAttributes
    ){

        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã nhân viên không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Họ và tên không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (ngaySinh == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày sinh không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (diaChi == null || diaChi.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Địa chỉ không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (sdt == null || sdt.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }
        if (!sdt.matches(SDT_PATTERN)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại không đúng định dạng(tối thiểu 10 chữ số và bắt đầu từ 0).");
            return "redirect:/nhanvien/hienthi";
        }

        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }

        if (!email.matches(EMAIL_PATTERN)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email không đúng định dạng.");
            return "redirect:/nhanvien/hienthi";
        }

        if (matKhau == null || matKhau.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không được để trống.");
            return "redirect:/nhanvien/hienthi";
        }
        if (!matKhau.matches(PASSWORD_PATTERN)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không đúng định dạng(tối thiểu 6 ký tự gồm chũ, số và ký tự đặc biệt).");
            return "redirect:/nhanvien/hienthi";
        }

        Integer idchucVu = Integer.parseInt(idcv);

        ChucVu chucVu = new ChucVu();
        chucVu.setId(idchucVu);

        NhanVien nhanVien = nhanVienService.findNhanVienById(idNV);
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setHoVaTen(hoVaTen);
        nhanVien.setIdChucVu(chucVu);
        nhanVien.setNgaySinh(ngaySinh);
        nhanVien.setGioiTinh(gioiTinh);
        nhanVien.setDiaChi(diaChi);
        nhanVien.setSdt(sdt);
        nhanVien.setEmail(email);
        nhanVien.setMatKhau(matKhau);
        nhanVien.setNgaySua(new Date());
        nhanVien.setTrangThai(trangThai);

        try {
            nhanVienService.updateNhanVien(nhanVien, idNV);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã được cập nhật thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/nhanvien/hienthi";
        }
        return "redirect:/nhanvien/hienthi";
    }

}

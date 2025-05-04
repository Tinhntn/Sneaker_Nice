package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@RequestMapping("/sanpham")
public class SanPhamController {
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private HangService hangService;
    @Autowired
    private DanhMucService danhMucService;
    @Autowired
    private ChatLieuService chatLieuService;
    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    private SizeService sizeService;
    @Autowired
    private MauSacService mauSacService;


    @GetMapping("/hienthi")
    public String hienThi(Model model, RedirectAttributes redirectAttributes, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword) {
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));

        Page<SanPham> lstSanPham;
        if (keyword != null && !keyword.isEmpty()) {
            lstSanPham = sanPhamService.findByMaSanPhamOrTenSanPham(keyword, keyword, pageable);
            model.addAttribute("keyword", keyword);
            if (lstSanPham == null) {
                redirectAttributes.addFlashAttribute("successMessage", "Không tìm thấy sản phẩm");
                lstSanPham = sanPhamService.findAll(pageable);
            }
        } else {
            lstSanPham = sanPhamService.findAll(pageable);
        }
        model.addAttribute("lstSanPham", lstSanPham.getContent());
        model.addAttribute("currentPage", lstSanPham.getNumber());
        model.addAttribute("totalPages", lstSanPham.getTotalPages());
        return "admin/sanpham/listSanPham";
    }

    @GetMapping("/themsanpham")
    public String hienThiFormThem(Model model) {
        model.addAttribute("sanPham", new SanPham()); // Đối tượng rỗng để Thymeleaf bind dữ liệu
        List<Hang> lstHang = hangService.getAllHangs();
        List<DanhMuc> lstDanhMuc = danhMucService.getAllDanhMucs();
        List<ChatLieu> lstChatLieu = chatLieuService.getAllChatLieus();
        List<SanPham> lstSanPham = sanPhamService.getAllSanPhams();
        String maSanPham = sanPhamService.taoMaSanPham();
        for (SanPham sp : lstSanPham
        ) {
            if (sp.getMaSanPham().equals(maSanPham)) {
                maSanPham = sanPhamService.taoMaSanPham();
            }
        }
        model.addAttribute("lstHang", lstHang);
        model.addAttribute("lstDanhMuc", lstDanhMuc);
        model.addAttribute("lstChatlieu", lstChatLieu);
        model.addAttribute("maSanPham", maSanPham);

        return "admin/sanpham/addSanPham";
    }

    @PostMapping("/themsanpham")
    public String them(@ModelAttribute SanPham sanPham,
                       @RequestParam("hang") int idHang,
                       @RequestParam("danhMuc") int idDanhMuc,
                       @RequestParam("chatLieu") int idChatLieu,
                       RedirectAttributes redirectAttributes) {
        // Gán giá trị từ combobox vào sản phẩm
        sanPham.setIdHang(hangService.getHangById(idHang));
        sanPham.setIdDanhMuc(danhMucService.findDanhMucById(idDanhMuc));
        sanPham.setIdChatLieu(chatLieuService.getChatLieuById(idChatLieu));
        List<SanPham> lstSP = sanPhamService.getAllSanPhams();
        for (SanPham sp : lstSP
        ) {
            if (sp.getTenSanPham().equals(sanPham.getTenSanPham())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm đã tồn tại!");
                return "redirect:/sanpham/themsanpham"; // Điều hướng về trang thêm sản phẩm            }

            }
        }
        sanPham.setTrangThai(true);
        sanPham.setNgaySua(new Date());
        sanPham.setNgayTao(new Date());
        // Lưu sản phẩm vào database
        sanPhamService.save(sanPham);
        // Thông báo thành công
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
        return "redirect:/sanpham/hienthi";

    }

    @GetMapping("/chitietsanpham/{id}")
    public String chiTietSanPham(@PathVariable("id") int idSanPham, Model model, @RequestParam(defaultValue = "0") int page
    ) {
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findChiTietSanPhamByIDSanPham(idSanPham, pageable);
        model.addAttribute("lstCTSP", lstCTSP.getContent());
        model.addAttribute("currentPage", lstCTSP.getNumber());
        model.addAttribute("totalPages", lstCTSP.getTotalPages());
        SanPham sanPham = sanPhamService.findById(idSanPham);
        model.addAttribute("sanPham", sanPham);
        List<Hang> lstHang = hangService.getAllHangs();
        List<DanhMuc> lstDanhMuc = danhMucService.getAllDanhMucs();
        List<ChatLieu> lstChatLieu = chatLieuService.getAllChatLieus();
        List<Size> lstSize = sizeService.findAll();

        List<MauSac> lstMauSac = mauSacService.findAll();
        model.addAttribute("lstMauSac", lstMauSac);
        model.addAttribute("lstSize", lstSize);
        model.addAttribute("lst", lstDanhMuc);
        model.addAttribute("lstHang", lstHang);
        model.addAttribute("lstDanhMuc", lstDanhMuc);
        model.addAttribute("lstChatlieu", lstChatLieu);
        return "admin/sanpham/updateSanPham";
    }

    @PostMapping("/capnhatsanpham/{id}")
    public String updateSanPham(@PathVariable("id") int id, @ModelAttribute("sanPham") SanPham sanPham, RedirectAttributes redirectAttributes
    ) {
        try {
            SanPham existingSanPham = sanPhamService.findById(id);
            if (sanPham.getTrangThai() == null) {
                sanPham.setTrangThai(false);
            }
            if (existingSanPham != null) {
                existingSanPham.setTenSanPham(sanPham.getTenSanPham());
                existingSanPham.setIdHang(sanPham.getIdHang());
                existingSanPham.setIdChatLieu(sanPham.getIdChatLieu());
                existingSanPham.setIdDanhMuc(sanPham.getIdDanhMuc());
                existingSanPham.setNgaySua(new Date());
                existingSanPham.setTrangThai(sanPham.getTrangThai() ? true : false);
                sanPhamService.update(existingSanPham);
                redirectAttributes.addFlashAttribute("", "Cập nhật sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật sản phẩm!");
        }

        return "redirect:/sanpham/chitietsanpham/" + id;
    }

    @PostMapping("/addctsanpham/{id}")
    public String themChiTietSanPham(@PathVariable("id") int idSanPham,
                                     @RequestParam("img") MultipartFile[] img,
                                     @ModelAttribute ChiTietSanPham chiTietSanPham, RedirectAttributes redirectAttributes) {
        try {

            List<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findByIdSanPham(idSanPham);
            if (chiTietSanPham != null) {
                List<String> paths = new ArrayList<>();
                if(img!=null && img.length>0) {
                    for (MultipartFile fileItem : img) {
                        if (!fileItem.isEmpty()) {
                            // Lưu file vào thư mục static/images
                            String fileName = fileItem.getOriginalFilename();
                            String uploadDir = "src/main/resources/static/images/";
                            Path path = Paths.get(uploadDir + fileName);
                            try {
                                Files.copy(fileItem.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                                paths.add(fileName); // Lưu đường dẫn ảnh để lưu DB
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // Ghép đường dẫn ảnh thành 1 chuỗi (ngăn cách bởi ,)
                        }
                    }
                }

                String allImages = String.join(",", paths);

                ChiTietSanPham ctsp = new ChiTietSanPham();
                ctsp.setIdSanPham(sanPhamService.findById(idSanPham));
                ctsp.setIdSize(chiTietSanPham.getIdSize());
                ctsp.setIdMauSac(chiTietSanPham.getIdMauSac());
                for (ChiTietSanPham ct : lstCTSP
                ) {
                    if ((ct.getIdSize().getId()==chiTietSanPham.getIdSize().getId()) && (ct.getIdMauSac().getId()==chiTietSanPham.getIdMauSac().getId())) {
                        redirectAttributes.addFlashAttribute("errrorMasage", "Chi tiết sản phẩm đã tồn tại");
                    }
                }
                ctsp.setTrongLuong(chiTietSanPham.getTrongLuong());
                ctsp.setGiaNhap(chiTietSanPham.getGiaNhap());
                ctsp.setGiaBan(chiTietSanPham.getGiaBan());
                ctsp.setSoLuong(chiTietSanPham.getSoLuong());
                ctsp.setHinhAnh(allImages);
                ctsp.setMoTa(chiTietSanPham.getMoTa());
                ctsp.setNgayTao(new Date());
                ctsp.setNgaySua(new Date());
                ctsp.setTrangThai(true);
                chiTietSanPhamService.saveChiTietSanPham(ctsp);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm chi tiết sản phẩm thành công");

            } else {
                redirectAttributes.addFlashAttribute("errrorMasage", "Có lỗi xảy ra khi thêm chi tiết sản phẩm");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errrorMasage", "Có lỗi xảy ra khi thêm chi tiết sản phẩm");
        }

        return "redirect:/sanpham/chitietsanpham/" + idSanPham;
    }


    @PutMapping("/updateCTSP/{id}")
    public ResponseEntity<?> updateSanPham(@PathVariable int id, @RequestBody Map<String, Object> chiTietSanPham) {
        try {
            // Tìm sản phẩm theo ID
            List<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findByIdSanPham(id);

            ChiTietSanPham ctsp = chiTietSanPhamService.findById(id);
            if (ctsp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Không tìm thấy sản phẩm!"));
            }
            // Ép kiểu dữ liệu an toàn
            Integer idSize = convertToInt(chiTietSanPham.get("idSize"));
            Integer idMauSac = convertToInt(chiTietSanPham.get("idMauSac"));
            Float trongLuong = convertToFloat(chiTietSanPham.get("trongLuong"));
            Float giaNhap = convertToFloat(chiTietSanPham.get("giaNhap"));
            Float giaBan = convertToFloat(chiTietSanPham.get("giaBan"));
            Integer soLuong = convertToInt(chiTietSanPham.get("soLuong"));
            String moTa = (chiTietSanPham.get("moTa") != null) ? chiTietSanPham.get("moTa").toString() : "";
            Boolean trangThai = (chiTietSanPham.get("trangThai") != null) ?
                    Boolean.parseBoolean(chiTietSanPham.get("trangThai").toString()) : false;
            // Cập nhật thông tin
            if (idSize != null) {
                ctsp.setIdSize(sizeService.findById(idSize));
                Size size = sizeService.findById(idSize);
                size.getMaSize();

            }
            if (idMauSac != null) {
                ctsp.setIdMauSac(mauSacService.findById(idMauSac));
            }
            ctsp.setTrongLuong((trongLuong != null) ? trongLuong : 0.0f);
            ctsp.setGiaNhap((giaNhap != null) ? giaNhap : 0.0f);
            ctsp.setGiaBan((giaBan != null) ? giaBan : 0.0f);
            ctsp.setSoLuong((soLuong != null) ? soLuong : 0);
            ctsp.setMoTa(moTa);
            ctsp.setTrangThai(trangThai);

            for (ChiTietSanPham ct : lstCTSP) {
                if (ct.getIdSize().getId().equals(idSize) && ct.getIdMauSac().getId().equals(idMauSac)) {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Chi tiết sản phẩm đã tồn tại"));
                }
            }
            // Gọi service để cập nhật dữ liệu
            chiTietSanPhamService.update(ctsp);

            return ResponseEntity.ok(Collections.singletonMap("success", true));


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Lỗi khi cập nhật sản phẩm!"));
        }
    }




    private int convertToInt(Object value) {
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0; // hoặc có thể ném ngoại lệ tùy vào yêu cầu của bạn
        }
    }

    @GetMapping("/createProductValidations")
    @ResponseBody
    public ResponseEntity<?> createProdcutValidations(@RequestParam("idSanPham") int idSanPham) {

        try {
            ArrayList<ChiTietSanPham> chiTietSanPhams = sanPhamService.createProductValidations(idSanPham);
            return ResponseEntity.ok().body(Map.of("success", "Thêm sản phẩm thanh công"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Tất cả sản phẩm đều đã được tạo"));
    }
    // Hàm hỗ trợ chuyển đổi kiểu dữ liệu an toàn


    private float convertToFloat(Object value) {
        try {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            return Float.parseFloat(value.toString());

        } catch (NumberFormatException e) {
            return 0;
        }

    }


}
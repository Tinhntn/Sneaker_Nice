package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Service.*;

import java.io.File;
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
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        if (page < 0) {
            page = 0;
        }
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));

        // Xử lý logic mặc định cho ngày
        Date now = new Date();
        Calendar cal = Calendar.getInstance();

        if (startDate != null && endDate == null) {
            endDate = now;
        } else if (endDate != null && startDate == null) {
            cal.setTime(endDate);
            cal.add(Calendar.DATE, -30);
            startDate = cal.getTime();
        }

        List<Hang> lstHang = hangService.getAllHangs();
        List<DanhMuc> lstDanhMuc = danhMucService.getAllDanhMucs();
        List<ChatLieu> lstChatLieu = chatLieuService.getAllChatLieus();
        Page<SanPham> lstSanPham = sanPhamService.searchSanPham(keyword, startDate, endDate, pageable);

        // Truyền lại dữ liệu tìm kiếm cho view
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("lstHang", lstHang);
        model.addAttribute("lstDanhMuc", lstDanhMuc);
        model.addAttribute("lstChatLieu", lstChatLieu);
        if (lstSanPham.isEmpty()) {
            model.addAttribute("message", "Không tìm thấy sản phẩm.");
        }

        model.addAttribute("lstSanPham", lstSanPham.getContent());
        model.addAttribute("currentPage", lstSanPham.getNumber());
        model.addAttribute("totalPages", lstSanPham.getTotalPages());

        return "admin/sanpham/listSanPham";
    }
    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@RequestParam("id")int id,@RequestParam("status")boolean status) {
        SanPham sanPham = sanPhamService.findById(id);
        if(sanPham == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message","Không tìm thấy sản phẩm"));
        }
        sanPham.setTrangThai(status);
        sanPhamService.save(sanPham);
        return ResponseEntity.ok().body(Collections.singletonMap("message",status?"Mở trạng thái hoạt động":"Tắt trạng thái hoạt động"));

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
    @ResponseBody
    public ResponseEntity<?> them(@RequestBody Map<String, Object> sanPhamRQ) {
        // Gán giá trị từ combobox vào sản phẩm
        if (sanPhamRQ.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Dữ liệu truyền vào không hợp lệ"));
        }
        if (sanPhamRQ.get("idHang").equals("") || hangService.getHangById(Integer.parseInt(sanPhamRQ.get("idHang").toString())) == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy hãng"));
        }
        if (sanPhamRQ.get("idDanhMuc").equals("") || danhMucService.findDanhMucById(Integer.parseInt(sanPhamRQ.get("idDanhMuc").toString())) == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy danh mục"));
        }
        if (sanPhamRQ.get("idChatLieu").equals("") || chatLieuService.getChatLieuById(Integer.parseInt(sanPhamRQ.get("idChatLieu").toString())) == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy chất liệu"));
        }
        SanPham sanPham = new SanPham();
        sanPham.setTenSanPham((String) sanPhamRQ.get("tenSanPham"));
        sanPham.setIdHang(hangService.getHangById(Integer.parseInt(sanPhamRQ.get("idHang").toString())));
        sanPham.setIdDanhMuc(danhMucService.findDanhMucById(Integer.parseInt(sanPhamRQ.get("idDanhMuc").toString())));
        sanPham.setIdChatLieu(chatLieuService.getChatLieuById(Integer.parseInt(sanPhamRQ.get("idChatLieu").toString())));
        sanPham.setTrangThai(true);
        sanPham.setNgaySua(new Date());
        sanPham.setNgayTao(new Date());
        List<SanPham> lstSP = sanPhamService.getAllSanPhams();
        for (SanPham sp : lstSP) {
            if (sp.getTenSanPham().trim().equalsIgnoreCase(sanPham.getTenSanPham().trim())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên phẩm đã tồn tại"));
            }
        }
        // Lưu sản phẩm vào database
        sanPhamService.save(sanPham);
        // Thông báo thành công
        return ResponseEntity.ok().body(Map.of("message", "Thêm sản phẩm thành công","success",true));

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

    @PutMapping("/capnhatsanpham/{idSanPham}")
    @ResponseBody
    public ResponseEntity<?> updateSanPham(@PathVariable("idSanPham") int id, @RequestBody Map<String, Object> sanPham
    ) {
        try {
            SanPham existingSanPham = sanPhamService.findById(id);
            if (existingSanPham != null) {
                List<SanPham> lstSanPham = sanPhamService.getAllSanPhams();
                String tenSanPham = sanPham.get("tenSanPham").toString();
                int idHang = Integer.parseInt((String) sanPham.get("idHang"));
                int idDanhMuc = Integer.parseInt((String) sanPham.get("idDanhMuc"));
                int idChatLieu = Integer.parseInt((String) sanPham.get("idChatLieu"));
                boolean trangThai = (boolean) sanPham.get("trangThai");
                for (SanPham sp : lstSanPham) {
                    if (sp.getId() != existingSanPham.getId() && sp.getTenSanPham().equalsIgnoreCase(tenSanPham)
                    ) {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên sản phẩm đã tồn tại"));
                    }
                }
                existingSanPham.setTenSanPham(tenSanPham);
                existingSanPham.setIdHang(hangService.getHangById(idHang));
                existingSanPham.setIdChatLieu(chatLieuService.getChatLieuById(idChatLieu));
                existingSanPham.setIdDanhMuc(danhMucService.findDanhMucById(idDanhMuc));
                existingSanPham.setNgaySua(new Date());
                existingSanPham.setTrangThai(trangThai);
                sanPhamService.update(existingSanPham);
                return ResponseEntity.ok().body(Collections.singletonMap("message", "Cập nhật sản phẩm thành công"));
            } else {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy sản phẩm"));

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Cập nhật sản phẩm thất bại"));
        }

    }

    @PostMapping("/addctsanpham/{id}")
    @ResponseBody
    public ResponseEntity<?> themChiTietSanPham(@PathVariable("id") int idSanPham,
                                                @RequestParam("img") MultipartFile img,
                                                @RequestParam("idSize") int idSize,
                                                @RequestParam("idMauSac") int idMauSac,
                                                @RequestParam("trongLuong") float trongLuong,
                                                @RequestParam("giaNhap") float giaNhap,
                                                @RequestParam("giaBan") float giaBan,
                                                @RequestParam("soLuong") int soLuong,
                                                @RequestParam(value = "moTa", required = false) String moTa) {
        try {
            List<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findByIdSanPham(idSanPham);
            if (sizeService.findById(idSize) == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Size không tồn tại"));
            }
            if (mauSacService.findById(idMauSac) == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Màu sắc không tồn tại"));
            }
            String fileName = null;
            List<String> paths = new ArrayList<>();
            if (!img.isEmpty()) {
                // Lưu file vào thư mục static/images
                fileName = img.getOriginalFilename();
                String uploadDir = "src/main/resources/static/images/";
// Tạo thư mục nếu chưa tồn tại
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Path path = Paths.get(uploadDir + fileName);
                try {
                    Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    paths.add(fileName); // Lưu đường dẫn ảnh để lưu DB
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Ghép đường dẫn ảnh thành 1 chuỗi (ngăn cách bởi ,)
            }
            String allImages = ("/images/" + fileName);
            ChiTietSanPham ctsp = new ChiTietSanPham();
            ctsp.setIdSanPham(sanPhamService.findById(idSanPham));
            ctsp.setIdSize(sizeService.findById(idSize));
            ctsp.setIdMauSac(mauSacService.findById(idMauSac));
            if (lstCTSP.size() > 0) {
                for (ChiTietSanPham ct : lstCTSP) {
                    if (ct.getIdSize().getId() == idSize
                            && ct.getIdMauSac().getId() == idMauSac) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Sản phẩm đã tồn tại", "success", false));
                    }
                }
            }
            ctsp.setTrongLuong(trongLuong);
            ctsp.setGiaNhap(giaNhap);
            ctsp.setGiaBan(giaBan);
            ctsp.setSoLuong(soLuong);
            ctsp.setHinhAnh(allImages);
            ctsp.setMoTa(moTa);
            ctsp.setNgayTao(new Date());
            ctsp.setNgaySua(new Date());
            ctsp.setTrangThai(true);
            chiTietSanPhamService.saveChiTietSanPham(ctsp);
            return ResponseEntity.ok().body(Map.of("message", "Thêm sản phẩm thành công", "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thêm sản phẩm thất bại", "success", false));
        }

    }

    @GetMapping("/chitietsanpham/fragment/{id}")
    public String getTableFragment(
            @PathVariable("id") int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pages = PageRequest.of(page, size);
        Page<ChiTietSanPham> pageCTSP = chiTietSanPhamService.findChiTietSanPhamByIDSanPham(id, pages);
        model.addAttribute("lstCTSP", pageCTSP.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageCTSP.getTotalPages());
        model.addAttribute("lstSize", sizeService.findAll());
        model.addAttribute("lstMauSac", mauSacService.findAll());
        return "fragments/chitietsanpham-table :: tableFragment";
    }


    @PutMapping("/updateCTSP/{id}")
    public ResponseEntity<?> updateChiTietSanPham(@PathVariable int id, @RequestBody Map<String, Object> chiTietSanPham) {
        try {
            // Tìm sản phẩm theo ID
            ChiTietSanPham ctsp = chiTietSanPhamService.findById(id);

            if (ctsp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Không tìm thấy sản phẩm!"));
            }
            List<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findByIdSanPham(ctsp.getIdSanPham().getId());

            // Ép kiểu dữ liệu an toàn
            int idSize = convertToInt(chiTietSanPham.get("idSize"));
            int idMauSac = convertToInt(chiTietSanPham.get("idMauSac"));
            for (ChiTietSanPham ct : lstCTSP) {
                // Nếu không phải bản ghi đang cập nhật thì mới check
                if (ctsp.getId() != ct.getId() && ct.getIdSize().getId() == idSize
                        && ct.getIdMauSac().getId() == idMauSac) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Chi tiết sản phẩm đã tồn tại", "success", false));
                }
            }
            Float trongLuong = convertToFloat(chiTietSanPham.get("trongLuong"));
            Float giaNhap = convertToFloat(chiTietSanPham.get("giaNhap"));
            Float giaBan = convertToFloat(chiTietSanPham.get("giaBan"));
            Integer soLuong = convertToInt(chiTietSanPham.get("soLuong"));
            String moTa = (chiTietSanPham.get("moTa") != null) ? chiTietSanPham.get("moTa").toString() : "";
            Boolean trangThai = (chiTietSanPham.get("trangThai") != null) ?
                    Boolean.parseBoolean(chiTietSanPham.get("trangThai").toString()) : false;
            ctsp.setIdSize(sizeService.findById(idSize));
            ctsp.setIdMauSac(mauSacService.findById(idMauSac));

            if (giaNhap <= 0 || giaBan <= 0 || soLuong <= 0 || trongLuong <= 0) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Giá trị nhập vào phải lớn hơn 0"));
            }
            ctsp.setTrongLuong((trongLuong != null) ? trongLuong : 0.0f);
            ctsp.setGiaNhap((giaNhap != null) ? giaNhap : 0.0f);
            ctsp.setGiaBan((giaBan != null) ? giaBan : 0.0f);
            ctsp.setSoLuong((soLuong != null) ? soLuong : 0);
            ctsp.setMoTa(moTa);
            ctsp.setTrangThai(trangThai);
            ctsp.setNgaySua(new Date());
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
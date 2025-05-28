package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private NhanVienService nhanVienService;

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Trả về email của người dùng đã đăng nhập
        }
        return null; // Nếu chưa đăng nhập, trả về null hoặc giá trị mặc định
    }


    @GetMapping("/hienthi")

    public String hienThiSanPham(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Integer idDanhMuc,
            @RequestParam(required = false) Integer idChatLieu,
            @RequestParam(required = false) Integer idHang,
            Model model) {


        // Xử lý keyword

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? "%" + keyword.trim() + "%" : null;

        // Xử lý ngày tháng
        if (startDate != null && endDate == null) {
            endDate = LocalDate.now();
        } else if (endDate != null && startDate == null) {
            startDate = endDate.minusMonths(1);
        }




        // Lấy danh sách cho dropdown
        model.addAttribute("lstDanhMuc", danhMucService.getAllDanhMucs().stream().sorted(Comparator.comparing(DanhMuc::getTenDanhMuc, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList()));
        model.addAttribute("lstChatLieu", chatLieuService.getAllChatLieus().stream().sorted(Comparator.comparing(ChatLieu::getTenChatLieu, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList()));
        model.addAttribute("lstHang", hangService.getAllHangs().stream().sorted(Comparator.comparing(Hang::getTenHang, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList()));

        // Thực hiện lọc
        Page<SanPham> pageSanPham = sanPhamService.filterSanPham(
                searchKeyword, startDate, endDate,
                idDanhMuc, idChatLieu, idHang,
                page, 10 // Số lượng item mỗi trang
        );
        // Đưa dữ liệu vào model
        model.addAttribute("lstSanPham", pageSanPham.getContent());
        model.addAttribute("currentPage", pageSanPham.getNumber());
        model.addAttribute("totalPages", pageSanPham.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("idDanhMuc", idDanhMuc);
        model.addAttribute("idChatLieu", idChatLieu);
        model.addAttribute("idHang", idHang);


        return "admin/sanpham/listSanPham";
    }

    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@RequestParam("id") int id, @RequestParam("status") boolean status) {
        SanPham sanPham = sanPhamService.findById(id);
        if (sanPham == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy sản phẩm"));
        }
        sanPham.setTrangThai(status);
        sanPhamService.save(sanPham);
        return ResponseEntity.ok().body(Collections.singletonMap("message", status ? "Mở trạng thái hoạt động" : "Tắt trạng thái hoạt động"));

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
        return ResponseEntity.ok().body(Map.of("message", "Thêm sản phẩm thành công", "success", true));

    }

    @GetMapping("/chitietsanpham/{id}")
    public String chiTietSanPham(
            @PathVariable("id") int idSanPham,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer idSize,
            @RequestParam(required = false) Integer idMauSac,
            @RequestParam(required = false) Integer trangThai // ✅ Sửa từ `boolean` sang `Boolean`
    ) {
        int size = 10;
        Boolean tt = null;

        if(trangThai != null) {
            if(trangThai==3){
                tt = false;
            }else if(trangThai==2){
                tt = true;
            }
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<ChiTietSanPham> lstCTSP = chiTietSanPhamService.locChiTietSanPham(idSanPham, idSize, idMauSac, tt, pageable);

        model.addAttribute("lstCTSP", lstCTSP.getContent());
        model.addAttribute("currentPage", lstCTSP.getNumber());
        model.addAttribute("totalPages", lstCTSP.getTotalPages());

        SanPham sanPham = sanPhamService.findById(idSanPham);
        model.addAttribute("sanPham", sanPham);

        List<Hang> lstHang = hangService.getAllHangs().stream()
                .sorted(Comparator.comparing(Hang::getTenHang, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        List<DanhMuc> lstDanhMuc = danhMucService.getAllDanhMucs().stream()
                .sorted(Comparator.comparing(DanhMuc::getTenDanhMuc, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        List<ChatLieu> lstChatLieu = chatLieuService.getAllChatLieus().stream()
                .sorted(Comparator.comparing(ChatLieu::getTenChatLieu, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        List<Size> lstSize = sizeService.findAll().stream()
                .sorted((s1, s2) -> Integer.compare(
                        Integer.parseInt(s1.getTenSize()),
                        Integer.parseInt(s2.getTenSize())
                ))
                .collect(Collectors.toList());

        List<MauSac> lstMauSac = mauSacService.findAll().stream()
                .sorted(Comparator.comparing(MauSac::getTenMauSac, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

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
    public ResponseEntity<?> themChiTietSanPham(
            @PathVariable("id") int idSanPham,
            @RequestParam("img") MultipartFile[] imgs, // Thay đổi từ MultipartFile sang MultipartFile[]
            @RequestParam("idSize") int idSize,
            @RequestParam("idMauSac") int idMauSac,
            @RequestParam("trongLuong") float trongLuong,
            @RequestParam("giaNhap") float giaNhap,
            @RequestParam("giaBan") float giaBan,
            @RequestParam("soLuong") int soLuong,
            @RequestParam(value = "moTa", required = false) String moTa) {

        try {
            List<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findByIdSanPham(idSanPham);

            // Kiểm tra Size và Màu sắc
            if (sizeService.findById(idSize) == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Size không tồn tại"));
            }
            if (mauSacService.findById(idMauSac) == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Màu sắc không tồn tại"));
            }

            // Xử lý lưu nhiều ảnh
            List<String> imagePaths = new ArrayList<>();
            if (imgs == null || imgs.length == 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Vui lòng chọn ít nhất 1 ảnh",
                        "success", false
                ));
            }
            if (imgs.length > 5) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Chỉ được upload tối đa 5 ảnh",
                        "success", false
                ));
            }
            for (MultipartFile img : imgs) {
                if (!img.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + img.getOriginalFilename(); // Tránh trùng tên
                    String uploadDir = "src/main/resources/static/images/";

                    // Tạo thư mục nếu chưa tồn tại
                    File dir = new File(uploadDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    // Lưu file vào thư mục
                    Path path = Paths.get(uploadDir + fileName);
                    Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    imagePaths.add("/images/" + fileName); // Lưu đường dẫn ảnh
                }
            }

            // Ghép các đường dẫn ảnh thành chuỗi (ngăn cách bằng dấu phẩy)
            String allImages = String.join(",", imagePaths);

            // Kiểm tra trùng lặp (Size + Màu sắc)
            for (ChiTietSanPham ct : lstCTSP) {
                if (ct.getIdSize().getId() == idSize && ct.getIdMauSac().getId() == idMauSac) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "message", "Sản phẩm " + ct.getIdSanPham().getTenSanPham() + " " + "size: " + ct.getIdSize().getTenSize() + " màu: " + ct.getIdMauSac().getTenMauSac() + " đã tồn tại",
                            "success", false
                    ));
                }
            }

            // Tạo mới ChiTietSanPham
            ChiTietSanPham ctsp = new ChiTietSanPham();
            ctsp.setIdSanPham(sanPhamService.findById(idSanPham));
            ctsp.setIdSize(sizeService.findById(idSize));
            ctsp.setIdMauSac(mauSacService.findById(idMauSac));
            ctsp.setTrongLuong(trongLuong);
            ctsp.setGiaNhap(giaNhap);
            ctsp.setGiaBan(giaBan);
            ctsp.setSoLuong(soLuong);
            ctsp.setHinhAnh(allImages); // Lưu chuỗi đường dẫn ảnh
            ctsp.setMoTa(moTa);
            ctsp.setNgayTao(new Date());
            ctsp.setNgaySua(new Date());
            ctsp.setTrangThai(true);

            chiTietSanPhamService.saveChiTietSanPham(ctsp);

            return ResponseEntity.ok().body(Map.of(
                    "message", "Thêm sản phẩm thành công",
                    "success", true
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Thêm sản phẩm thất bại: " + e.getMessage(),
                    "success", false
            ));
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
        model.addAttribute("lstSize", sizeService.findAll().stream().sorted(Comparator.comparing(Size::getTenSize, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList()));
        model.addAttribute("lstMauSac", mauSacService.findAll().stream().sorted(Comparator.comparing(MauSac::getTenMauSac, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList()));
        return "fragments/chitietsanpham-table :: tableFragment";
    }


    @GetMapping("/getSizeByMau/{id}")
    @ResponseBody
    public ResponseEntity<List<Size>> getSizeByMau(@PathVariable("id") int idSP, @RequestParam("idMau") int idMau) {
        List<ChiTietSanPham> lstChiTietSanPham = chiTietSanPhamService.findByIdSanPham(idSP);

        //Lấy ra các size đã có màu dược truyền vào
        Set<Integer> sizeDaCo = lstChiTietSanPham.stream()
                .filter(ctsp -> ctsp.getIdMauSac().getId() == idMau)
                .map(ctsp -> ctsp.getIdSize().getId())
                .collect(Collectors.toSet());
        List<Size> lstSize = sizeService.findAll();

        List<Size> lstSizeChuaCo = lstSize.stream()
                .sorted((s1, s2) -> Integer.compare(
                        Integer.parseInt(s1.getTenSize()),
                        Integer.parseInt(s2.getTenSize())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lstSizeChuaCo);
    }

    @PostMapping("/updateCTSPWithImage/{id}")
    @ResponseBody
    public ResponseEntity<?> updateChiTietSanPhamWithImage(
            @PathVariable int id,
            @RequestParam("idSize") int idSize,
            @RequestParam("idMauSac") int idMauSac,
            @RequestParam("trongLuong") Float trongLuong,
            @RequestParam("giaNhap") Float giaNhap,
            @RequestParam("giaBan") Float giaBan,
            @RequestParam("soLuong") int soLuong,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("trangThai") boolean trangThai,
            @RequestParam(value = "hinhAnhFiles", required = false) MultipartFile[] hinhAnhFiles
    ) {
        try {
            // Tìm sản phẩm theo ID
            ChiTietSanPham ctsp = chiTietSanPhamService.findById(id);

            if (ctsp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Không tìm thấy sản phẩm!"));
            }
            List<ChiTietSanPham> lstCTSP = chiTietSanPhamService.findByIdSanPham(ctsp.getIdSanPham().getId());
            for (ChiTietSanPham ct : lstCTSP) {
                if (ct.getId() != ctsp.getId() && ct.getIdSize().getId() == idSize && ct.getIdMauSac().getId() == idMauSac) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Chi tiết sản phẩm đã tồn tại", "success", false));
                }
            }
            if (giaNhap <= 0 || giaBan <= 0 || soLuong <= 0 || trongLuong <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Giá trị nhập vào phải lớn hơn 0"));
            }
            ctsp.setIdSize(sizeService.findById(idSize));
            ctsp.setIdMauSac(mauSacService.findById(idMauSac));
            ctsp.setTrongLuong(trongLuong);
            ctsp.setGiaNhap(giaNhap);
            ctsp.setGiaBan(giaBan);
            ctsp.setSoLuong(soLuong);
            ctsp.setMoTa(moTa != null ? moTa : "");
            ctsp.setTrangThai(trangThai);
            ctsp.setNgaySua(new Date());
            ;
            // Xử lý ảnh nếu có
            if (hinhAnhFiles != null && hinhAnhFiles.length > 0) {
                List<String> newImagePaths = new ArrayList<>();

                // Lưu các ảnh mới
                for (MultipartFile file : hinhAnhFiles) {
                    if (!file.isEmpty()) {
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        Path path = Paths.get("src/main/resources/static/images/" + fileName);
                        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                        newImagePaths.add("/images/" + fileName);
                    }
                }

                // Kết hợp với ảnh cũ (nếu muốn giữ lại)
                // Hoặc chỉ lưu ảnh mới: ctsp.setHinhAnh(String.join(",", newImagePaths));
                String currentImages = ctsp.getHinhAnh();
                if (currentImages != null && !currentImages.isEmpty()) {
                    newImagePaths.addAll(Arrays.asList(currentImages.split(",")));
                }
                ctsp.setHinhAnh(String.join(",", newImagePaths));

            }
            // Gọi service để cập nhật dữ liệu
            chiTietSanPhamService.update(ctsp);
            return ResponseEntity.ok(Map.of("message", "Cập nhật sản phẩm thành công", "success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Lỗi khi cập nhật sản phẩm!"));
        }
    }



    @PostMapping("/them-bien-the")
    @ResponseBody
    public ResponseEntity<?> themBienThe(
            @RequestParam("idSanPham") int idSanPham,
            @RequestParam("idMauSac") int idMauSac,
            @RequestParam("hinhAnh") List<MultipartFile> hinhAnh,
            @RequestParam Map<String, String> params) {

        try {
            List<ChiTietSanPham> lstChiTietSanPham = chiTietSanPhamService.findByIdSanPham(idSanPham);
            // Kiểm tra sản phẩm và danh sách biến thể hiện tại
            SanPham sanPham = sanPhamService.findById(idSanPham);
            MauSac mauSac = mauSacService.findMauSacById(idMauSac);


            if (sanPham == null || mauSac == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Không tìm thấy sản phẩm hoặc màu sắc"
                ));
            }

            // Kiểm tra ảnh
            if (hinhAnh == null || hinhAnh.isEmpty() || hinhAnh.get(0).isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Vui lòng chọn ít nhất 1 ảnh"
                ));
            }

            List<ChiTietSanPham> lstCTSPNew = new ArrayList<>();
            int index = 0;

            while (true) {
                String idSizeKey = "sizes[" + index + "].idSize";
                String giaNhapKey = "sizes[" + index + "].giaNhap";
                String giaBanKey = "sizes[" + index + "].giaBan";
                String soLuongKey = "sizes[" + index + "].soLuong";
                String trongLuongKey = "sizes[" + index + "].trongLuong";
                if (!params.containsKey(idSizeKey)) break;

                try {
                    int idSize = Integer.parseInt(params.get(idSizeKey));
                    float giaNhap = Float.parseFloat(params.get(giaNhapKey));
                    float giaBan = Float.parseFloat(params.get(giaBanKey));
                    int soLuong = Integer.parseInt(params.get(soLuongKey));
                    float trongLuong = Float.parseFloat(params.get(trongLuongKey));
                    // Kiểm tra size có tồn tại không
                    Size size = sizeService.findById(idSize);
                    if (size == null) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "success", false,
                                "message", "Size không tồn tại ở dòng " + (index + 1)
                        ));
                    }

                    // Kiểm tra trùng biến thể
                    boolean isExist = lstChiTietSanPham.stream()
                            .anyMatch(ctsp -> ctsp.getIdSanPham().getId() == idSanPham && ctsp.getIdSize().getId() == idSize && ctsp.getIdMauSac().getId() == idMauSac);

                    if (isExist) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "success", false,
                                "message", "Biến thể đã tồn tại (Size: " + size.getTenSize() + ")"
                        ));
                    }

                    // Tạo mới ChiTietSanPham
                    ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
                    chiTietSanPham.setIdSanPham(sanPham);
                    chiTietSanPham.setIdMauSac(mauSac);
                    chiTietSanPham.setIdSize(size);
                    chiTietSanPham.setGiaNhap(giaNhap);
                    chiTietSanPham.setGiaBan(giaBan);
                    chiTietSanPham.setSoLuong(soLuong);
                    chiTietSanPham.setTrongLuong(trongLuong);
                    chiTietSanPham.setTrangThai(true);
                    chiTietSanPham.setNgayTao(new Date());
                    chiTietSanPham.setNgaySua(new Date());

                    // Xử lý lưu ảnh
                    List<String> imagePaths = new ArrayList<>();
                    for (MultipartFile file : hinhAnh) {
                        if (!file.isEmpty()) {
                            // Validate file
                            if (file.getSize() > 5 * 1024 * 1024) {
                                return ResponseEntity.badRequest().body(Map.of(
                                        "success", false,
                                        "message", "Ảnh " + file.getOriginalFilename() + " vượt quá 5MB"
                                ));
                            }

                            String contentType = file.getContentType();
                            if (contentType == null ||
                                    (!contentType.equals("image/jpeg") &&
                                            !contentType.equals("image/jpg") &&
                                            !contentType.equals("image/png"))) {
                                return ResponseEntity.badRequest().body(Map.of(
                                        "success", false,
                                        "message", "Chỉ chấp nhận ảnh JPG/PNG"
                                ));
                            }

                            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                            Path path = Paths.get("src/main/resources/static/images/" + fileName);
                            Files.createDirectories(path.getParent());
                            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                            imagePaths.add("/images/" + fileName);
                        }
                    }

                    chiTietSanPham.setHinhAnh(String.join(",", imagePaths));

                    // Thêm mô tả
                    NhanVien nhanVien = nhanVienService.getNhanVienByEmail(getCurrentUserEmail());
                    String moTa = "Thêm mới bởi " + nhanVien.getHoVaTen() + " vào " +
                            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
                    chiTietSanPham.setMoTa(moTa);

                    lstCTSPNew.add(chiTietSanPham);

                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Dữ liệu không hợp lệ ở dòng " + (index + 1)
                    ));
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Lỗi khi lưu ảnh"
                    ));
                }

                index++;
            }

            // Kiểm tra nếu không có dòng nào hợp lệ
            if (lstCTSPNew.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Không có biến thể nào được thêm"
                ));
            }

            // Lưu danh sách vào DB
            for (ChiTietSanPham ct : lstCTSPNew) {
                chiTietSanPhamService.saveChiTietSanPham(ct);
            }
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Thêm thành công " + lstCTSPNew.size() + " biến thể"
            ));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + ex.getMessage()
            ));
        }
    }


}
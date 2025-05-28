package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poly.edu.sneaker.DAO.ChucVuDTO;
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Service.ChucVuService;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chucvu")
public class ChucVuController {

    @Autowired
    private ChucVuService chucVuService;

    // Lấy danh sách chức vụ phân trang
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChucVu> chucVuPage;

        if (keyword != null && !keyword.isEmpty()) {
            chucVuPage = chucVuService.search(keyword, pageable);
        } else {
            chucVuPage = chucVuService.getAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("listCV", chucVuPage.getContent());
        response.put("currentPage", chucVuPage.getNumber());
        response.put("totalItems", chucVuPage.getTotalElements());
        response.put("totalPages", chucVuPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // Thêm mới chức vụ
    @PostMapping
    public ResponseEntity<?> addChucVu(@Valid @RequestBody ChucVuDTO chucVuDTO,
                                       BindingResult bindingResult) {

        // Kiểm tra validate
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        // Kiểm tra trùng tên
        if (chucVuService.existsByTenChucVu(chucVuDTO.getTenChucVu())) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("tenChucVu", "Tên chức vụ đã tồn tại"));
        }

        try {
            String maChucVu = chucVuService.taoMaChucVu();
            maChucVu += chucVuDTO.isPhanQuyen() ? "ADMIN" : "EMPLOYEE";

            ChucVu chucVu = new ChucVu();
            chucVu.setTenChucVu(chucVuDTO.getTenChucVu());
            chucVu.setMaChucVu(maChucVu);

            chucVuService.save(chucVu);
            return ResponseEntity.ok(Collections.singletonMap("message", "Thêm chức vụ thành công"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("message", "Lỗi khi thêm chức vụ"));
        }
    }

    // Cập nhật chức vụ
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChucVu(@PathVariable Integer id,
                                          @Valid @RequestBody ChucVuDTO chucVuDTO,
                                          BindingResult bindingResult) {

        // Kiểm tra validate
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        // Kiểm tra trùng tên (trừ chính nó)
        List<ChucVu> chucVuCheck = chucVuService.getAll();
        ChucVu existingChucVu = chucVuService.findChucVuById(id);



        try {
            if (existingChucVu == null) {
                return ResponseEntity.notFound().build();
            }
            boolean chucVuExist = chucVuCheck.stream().filter(chucVu->chucVu.getTenChucVu().equalsIgnoreCase(chucVuDTO.getTenChucVu())).findFirst().isPresent();
            if (chucVuExist&&chucVuDTO.getId()!=existingChucVu.getId()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("tenChucVu", "Tên chức vụ đã tồn tại"));
            }
            boolean currentPhanQuyen = existingChucVu.getMaChucVu().contains("ADMIN");
            boolean newPhanQuyen = chucVuDTO.isPhanQuyen();

            existingChucVu.setTenChucVu(chucVuDTO.getTenChucVu());

            if (currentPhanQuyen != newPhanQuyen) {
                String newRoleSuffix = newPhanQuyen ? "ADMIN" : "EMPLOYEE";
                String newMaChucVu = existingChucVu.getMaChucVu()
                        .replace("ADMIN", "")
                        .replace("EMPLOYEE", "") + newRoleSuffix;
                existingChucVu.setMaChucVu(newMaChucVu);
            }

            chucVuService.save(existingChucVu);
            return ResponseEntity.ok(Collections.singletonMap("message", "Cập nhật chức vụ thành công"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("message", "Lỗi khi cập nhật chức vụ"));
        }
    }

    // Xóa chức vụ
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChucVu(@PathVariable Integer id) {
        try {
            if (chucVuService.findChucVuById(id)==null) {
                return ResponseEntity.notFound().build();
            }

            chucVuService.deleteById(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Xóa chức vụ thành công"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("message", "Lỗi khi xóa chức vụ"));
        }
    }

    // Lấy thông tin 1 chức vụ
    @GetMapping("/{id}")
    public ResponseEntity<?> getChucVuById(@PathVariable Integer id) {
        ChucVu chucVu = chucVuService.findChucVuById(id);
        if (chucVu == null) {
            return ResponseEntity.notFound().build();
        }

        ChucVuDTO chucVuDTO = new ChucVuDTO();
        chucVuDTO.setId(chucVu.getId());
        chucVuDTO.setTenChucVu(chucVu.getTenChucVu());
        chucVuDTO.setPhanQuyen(chucVu.getMaChucVu().contains("ADMIN"));

        return ResponseEntity.ok(chucVuDTO);
    }
}
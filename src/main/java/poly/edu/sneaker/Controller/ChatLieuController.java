package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.ChatLieu;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Model.SanPham;
import poly.edu.sneaker.Model.Size;
import poly.edu.sneaker.Service.ChatLieuService;

import jakarta.validation.Valid;
import poly.edu.sneaker.Service.SanPhamService;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/chat_lieu")
public class ChatLieuController {

    @Autowired
    private ChatLieuService chatLieuService;
    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping("/hienthi")
    public String hienthi(Model model, @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate starDate,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate endDate,
                          @RequestParam(required = false) Integer trangThai) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Boolean tt =null;
        if(trangThai!=null){
            if(trangThai==0){
                tt=true;
            }else if(trangThai==1){
                tt=false;
            }
        }
        Page<ChatLieu> chatLieuPage = chatLieuService.locChatLieu(keyword,starDate,endDate,tt,pageable);
        model.addAttribute("lstChatLieu", chatLieuPage.getContent());
        model.addAttribute("currentPage", chatLieuPage.getNumber());
        model.addAttribute("totalPages", chatLieuPage.getTotalPages());
        return "admin/chat_lieu/ListChatLieu";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> add(
            @RequestParam("tenChatLieu") String tenChatLieu,
            @RequestParam("trangThai") Boolean trangThai) {
        if (tenChatLieu == null || tenChatLieu.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }
        List<ChatLieu> lstChatLieu = chatLieuService.getAllChatLieus();
        for (ChatLieu cl : lstChatLieu) {
            if (cl.getTenChatLieu().equalsIgnoreCase(tenChatLieu)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên chất liệu đã tồn tại"));
            }
        }
        ChatLieu chatLieu = new ChatLieu();
        chatLieu.setMaChatLieu(chatLieuService.taoMaChatLieu());
        chatLieu.setTenChatLieu(tenChatLieu);
        chatLieu.setTrangThai(trangThai);
        chatLieu.setNgayTao(new Date());
        try {
            chatLieuService.save(chatLieu);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Thêm thất bại"));
        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));
    }

    @PostMapping("/them_nhanh")
    @ResponseBody
    public ResponseEntity<?> themNhanh(@ModelAttribute ChatLieu chatLieu) {

        if (chatLieu == null|| chatLieu.getTenChatLieu() == null || chatLieu.getTenChatLieu().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Bạn cần nhập đủ thông tin"));
        }
        String tenChatLieu = chatLieu.getTenChatLieu();
        ArrayList<ChatLieu> chatLieus = chatLieuService.getAllChatLieus();
        for (ChatLieu cl : chatLieus) {
            if (cl.getTenChatLieu().trim().equalsIgnoreCase(tenChatLieu.trim())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên chất liệu đã tồn tại"));
            }
        }
        ChatLieu newCL = new ChatLieu();
        newCL.setTenChatLieu(tenChatLieu);
        newCL.setNgayTao(new Date());
        newCL.setTrangThai(true);
        newCL.setMaChatLieu(chatLieuService.taoMaChatLieu());
        chatLieuService.save(newCL);
        return ResponseEntity.ok().body(Map.of("message", "Thêm hãng mới thành công", "success", true));
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable("id") Integer idChatLieu,
                                    @RequestParam("tenChatLieu") String tenChatLieu,
                                    @RequestParam("trangThai") Boolean trangThai) {
        if (tenChatLieu == null || tenChatLieu.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vui lòng nhập đủ thông tin"));
        }
        ArrayList<ChatLieu> chatLieus = chatLieuService.getAllChatLieus();
        for (ChatLieu cl : chatLieus) {
            if (cl.getId()!=idChatLieu&&cl.getTenChatLieu().trim().equalsIgnoreCase(tenChatLieu.trim())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tên chất liệu đã tồn tại"));
            }
        }
        ChatLieu chatLieu = chatLieuService.findChatLieuById(idChatLieu);
        chatLieu.setTenChatLieu(tenChatLieu);
        chatLieu.setNgaySua(new Date());
        chatLieu.setTrangThai(trangThai);
        try {
            chatLieuService.update(chatLieu,idChatLieu);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Cập nhật thất bại"));

        }
        return ResponseEntity.ok().body(Collections.singletonMap("success", true));

    }


}
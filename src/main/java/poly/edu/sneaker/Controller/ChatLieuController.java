package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.sneaker.Model.ChatLieu;
import poly.edu.sneaker.Service.ChatLieuService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/chat_lieu")
public class ChatLieuController {

    @Autowired
    private ChatLieuService chatLieuService;

    @GetMapping("/hienthi")
    public String hienThiChatLieu(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatLieu> chatLieuPage;

        if (keyword != null && !keyword.isEmpty()) {
            chatLieuPage = chatLieuService.search(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            chatLieuPage = chatLieuService.getAll(pageable);
        }

        model.addAttribute("listCL", chatLieuPage.getContent());
        model.addAttribute("currentPage", chatLieuPage.getNumber());
        model.addAttribute("totalPages", chatLieuPage.getTotalPages());
        return "admin/chat_lieu/ListChatLieu";
    }

    @PostMapping("/add")
    public String addChatLieu(@Valid @ModelAttribute("chatLieu") ChatLieu chatLieu, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/chat_lieu/add";
        }
        try {
            if (chatLieuService.findByMaChatLieu(chatLieu.getMaChatLieu()) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mã chất liệu đã tồn tại!");
                return "redirect:/chat_lieu/add";
            }
            chatLieuService.save(chatLieu);
            redirectAttributes.addFlashAttribute("successMessage", "Chất liệu được thêm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm chất liệu!");
        }
        return "redirect:/chat_lieu/hienthi";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        ChatLieu chatLieu = chatLieuService.findChatLieuById(id);
        if (chatLieu == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chất liệu không tồn tại!");
            return "redirect:/chat_lieu/hienthi";
        }
        model.addAttribute("chatLieu", chatLieu);
        return "admin/chat_lieu/update";
    }

    @PostMapping("/update/{id}")
    public String editChatLieu(@PathVariable("id") Integer id, @Valid @ModelAttribute("chatLieu") ChatLieu chatLieu, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/chat_lieu/update";
        }
        try {
            ChatLieu existingChatLieu = chatLieuService.findChatLieuById(id);
            if (existingChatLieu != null) {
                chatLieu.setId(id);
                chatLieu.setNgayTao(existingChatLieu.getNgayTao());
                chatLieuService.update(chatLieu, id);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chất liệu thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Chất liệu không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật chất liệu!");
        }
        return "redirect:/chat_lieu/hienthi";
    }

    @GetMapping("/delete/{id}")
    public String deleteChatLieu(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            chatLieuService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chất liệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa chất liệu!");
        }
        return "redirect:/chat_lieu/hienthi";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("chatLieu", new ChatLieu());
        return "admin/chat_lieu/add";
    }
}
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

    private final ChatLieuService chatLieuService;

    @Autowired
    public ChatLieuController(ChatLieuService chatLieuService) {
        this.chatLieuService = chatLieuService;
    }

    @GetMapping("/hienthi")
    public String hienThiChatLieu(@RequestParam(required = false) String search, Pageable pageable, Model model) {
        Page<ChatLieu> page;
        if (search != null && !search.isEmpty()) {
            page = chatLieuService.findByTenChatLieuOrMaChatLieuAndDeletedAt(search, search, false, pageable);
        } else {
            page = chatLieuService.findAll(pageable);
        }
        model.addAttribute("listCL", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("search", search);
        return "admin/chat_lieu/ListChatLieu";
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

    @GetMapping("/listPage")
    @ResponseBody
    public Page<ChatLieu> listPage(
            @RequestParam(defaultValue = "0") int page, // Mặc định là trang 0
            @RequestParam(defaultValue = "10") int size // Mặc định là 10 bản ghi mỗi trang
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return chatLieuService.listPage(pageable);
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("chatLieu", new ChatLieu());
        return "admin/chat_lieu/add";
    }

    @PostMapping("/add")
    public String addChatLieu(@Valid @ModelAttribute("chatLieu") ChatLieu chatLieu, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/chat_lieu/add";
        }
        try {
            chatLieuService.save(chatLieu);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm chất liệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm chất liệu!");
        }
        return "redirect:/chat_lieu/hienthi";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        ChatLieu chatLieu = chatLieuService.findById(id);
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
            ChatLieu existingChatLieu = chatLieuService.findById(id);
            if (existingChatLieu != null) {
                chatLieu.setId(id);
                chatLieuService.update(chatLieu);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chất liệu thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Chất liệu không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật chất liệu!");
        }
        return "redirect:/chat_lieu/hienthi";
    }
}
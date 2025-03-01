package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.ChatLieu;

import poly.edu.sneaker.Repository.ChatLieuRepostory;
import poly.edu.sneaker.Service.ChatLieuService;

import java.util.Date;

@Service
public class ChatLieuImplement implements ChatLieuService {
    private final ChatLieuRepostory chatLieuRepository;

    @Autowired
    public ChatLieuImplement(ChatLieuRepostory chatLieuRepository) {
        this.chatLieuRepository = chatLieuRepository;
    }

    @Override
    public Page<ChatLieu> findAll(Pageable pageable) {
        return chatLieuRepository.findByDeletedAtFalse(pageable);
    }

    @Override
    public ChatLieu findById(int id) {
        return chatLieuRepository.findByIdAndDeletedAtFalse(id).orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại"));
    }

    @Override
    public ChatLieu save(ChatLieu chatLieu) {
        chatLieu.setNgayTao(new Date());
        chatLieu.setNgaySua(new Date());
        return chatLieuRepository.save(chatLieu);
    }

    @Override
    public void deleteById(int id) {
        ChatLieu chatLieu = chatLieuRepository.findById(id).orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại"));
        chatLieu.setDeletedAt(true);
        chatLieuRepository.save(chatLieu);
    }

    @Override
    public void update(ChatLieu chatLieu) {
        chatLieu.setNgaySua(new Date());
        chatLieuRepository.save(chatLieu);
    }

    @Override
    public Page<ChatLieu> listPage(Pageable pageable) {
        return chatLieuRepository.findByDeletedAtFalse(pageable);
    }

    @Override
    public void delete(Integer id) {
        deleteById(id);
    }

    @Override
    public Page<ChatLieu> findByTenChatLieuOrMaChatLieuAndDeletedAt(String tenChatLieu, String maChatLieu, boolean deletedAt, Pageable pageable) {
        return chatLieuRepository.findByTenChatLieuContainingAndDeletedAtFalse(tenChatLieu, pageable);
    }
}
package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.ChatLieu;
import poly.edu.sneaker.Repository.ChatLieuRepostory;
import poly.edu.sneaker.Service.ChatLieuService;

import java.util.List;

@Service

public class ChatLieuImplement implements ChatLieuService {
    @Autowired
    ChatLieuRepostory chatLieuRepostory;
    @Override
    public Page<ChatLieu> getAllChatLieu(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chatLieuRepostory.findAll(pageable);
    }

    @Override
    public void saveChatLieu(ChatLieu chatLieu) {
        chatLieuRepostory.save(chatLieu);
    }

    @Override
    public ChatLieu getChatLieuById(int id) {
        ChatLieu chatLieu = chatLieuRepostory.findById(id).get();
        return chatLieu;
    }

    @Override
    public void deleteChatLieuById(int id) {
        chatLieuRepostory.deleteById(id);
    }

    @Override
    public void updateChatLieu(ChatLieu chatLieu) {
        chatLieuRepostory.save(chatLieu);
    }

    @Override
    public List<ChatLieu> getAllChatLieus() {
        return chatLieuRepostory.findAll();
    }
}

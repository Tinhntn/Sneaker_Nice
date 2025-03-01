package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChatLieu;

import java.util.List;

public interface ChatLieuService {
     Page<ChatLieu> findAll(Pageable pageable);

     ChatLieu findById(int id);

     ChatLieu save(ChatLieu chatLieu);

     void deleteById(int id);

     void update(ChatLieu chatLieu);

     Page<ChatLieu> listPage(Pageable pageable);

     void delete(Integer id);

     Page<ChatLieu> findByTenChatLieuOrMaChatLieuAndDeletedAt(String tenChatLieu, String maChatLieu, boolean deletedAt, Pageable pageable);
}
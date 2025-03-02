package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChatLieu;

public interface ChatLieuService {
     Page<ChatLieu> getAll(Pageable pageable);

     ChatLieu findChatLieuById(int id);

     void save(ChatLieu chatLieu);

     void update(ChatLieu chatLieu, int id);

     void deleteById(int id);

     Page<ChatLieu> search(String keyword, Pageable pageable);

     ChatLieu findByMaChatLieu(String maChatLieu);
}
package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import poly.edu.sneaker.Model.ChatLieu;

public interface ChatLieuService {

     Page<ChatLieu> getAllChatLieu(int page, int size);

     void saveChatLieu(ChatLieu chatLieu);
     ChatLieu getChatLieuById(int id);
     void deleteChatLieuById(int id);
     void updateChatLieu(ChatLieu chatLieu);

}

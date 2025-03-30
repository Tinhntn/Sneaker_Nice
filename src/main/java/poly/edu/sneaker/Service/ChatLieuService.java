package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChatLieu;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface ChatLieuService {
     Page<ChatLieu> getAll(Pageable pageable);

     ChatLieu findChatLieuById(int id);

     ChatLieu getChatLieuById(int id);
     ArrayList<ChatLieu> getAllChatLieus();

     void save(ChatLieu chatLieu);

     void update(ChatLieu chatLieu, int id);

     void deleteById(int id);

     Page<ChatLieu> search(String keyword, Pageable pageable);

     ChatLieu findByMaChatLieu(String maChatLieu);
     String taoMaChatLieu();

}
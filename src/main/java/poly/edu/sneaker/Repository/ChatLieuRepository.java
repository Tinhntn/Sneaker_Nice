package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChatLieu;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {
    Page<ChatLieu> findByMaChatLieuContainingOrTenChatLieuContaining(String maChatLieu, String tenChatLieu, Pageable pageable);

    ChatLieu findByMaChatLieu(String maChatLieu);
}
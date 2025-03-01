package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChatLieu;

import java.util.Optional;

@Repository
public interface ChatLieuRepostory extends JpaRepository<ChatLieu, Integer> {

    Page<ChatLieu> findByDeletedAtFalse(Pageable pageable);

    Optional<ChatLieu> findByIdAndDeletedAtFalse(int id);

    Page<ChatLieu> findByTenChatLieuContainingAndDeletedAtFalse(String tenChatLieu, Pageable pageable);

    ChatLieu findByMaChatLieuAndDeletedAtFalse(String maChatLieu);

    Page<ChatLieu> findByIdOrMaChatLieuAndDeletedAtFalse(Integer id, String maChatLieu, Pageable pageable);
}
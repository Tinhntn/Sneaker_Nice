package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChatLieu;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {
    Page<ChatLieu> findByMaChatLieuContainingOrTenChatLieuContaining(String maChatLieu, String tenChatLieu, Pageable pageable);

    ChatLieu findByMaChatLieu(String maChatLieu);
    ChatLieu findById(int id);
    //Code cua quan start
    @Query(value = "SELECT * FROM ChatLieu cl " +
            "WHERE cl.trang_thai = 1;",
            nativeQuery = true)
    List<ChatLieu> getAllChatLieuTimKiem();
    //Code cua quan end
    boolean existsByTenChatLieu(String tenChatLieu);
}
package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChatLieu;

@Repository
public interface ChatLieuRepostory extends JpaRepository<ChatLieu, Integer> {
}

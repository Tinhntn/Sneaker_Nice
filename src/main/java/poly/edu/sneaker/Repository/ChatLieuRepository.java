package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChatLieu;
import poly.edu.sneaker.Model.DanhMuc;

import java.util.ArrayList;
import java.util.Date;
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
    @Query("SELECT cl FROM ChatLieu cl " +
            "WHERE (:keyword IS NULL OR cl.maChatLieu LIKE %:keyword% OR cl.tenChatLieu LIKE %:keyword%) " +
            "AND ( " +
            "(:startDate IS NULL AND :endDate IS NULL) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NULL AND cl.ngayTao >= :startDate) " +
            "OR (:startDate IS NULL AND :endDate IS NOT NULL AND cl.ngayTao <= :endDate) " +
            "OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND cl.ngayTao BETWEEN :startDate AND :endDate)) " +
            "AND (:trangThai IS NULL OR cl.trangThai = :trangThai)")
    Page<ChatLieu> getChatLieuByKeyword(@Param("keyword") String keyword,
                                      @Param("startDate") Date startDate,
                                      @Param("endDate") Date endDate,
                                      @Param("trangThai") Boolean trangThai,
                                      Pageable pageable);
}
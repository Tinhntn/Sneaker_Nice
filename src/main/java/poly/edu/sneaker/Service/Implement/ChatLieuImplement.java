package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.sneaker.Model.ChatLieu;
import poly.edu.sneaker.Repository.ChatLieuRepository;
import poly.edu.sneaker.Repository.SanPhamRepository;
import poly.edu.sneaker.Service.ChatLieuService;

import java.time.LocalDate;
import java.util.*;

@Service
public class ChatLieuImplement implements ChatLieuService {

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Override
    public Page<ChatLieu> getAll(Pageable pageable) {
        return chatLieuRepository.findAll(pageable);
    }

    @Override
    public ChatLieu findChatLieuById(int id) {
        return chatLieuRepository.findById(id);
    }

    @Override
    public ChatLieu getChatLieuById(int id) {
        return chatLieuRepository.findById(id);
    }

    @Override
    public void save(ChatLieu chatLieu) {
        chatLieuRepository.save(chatLieu);
    }

    @Override
    public void update(ChatLieu chatLieu, int id) {
        ChatLieu existingChatLieu = chatLieuRepository.findById(id);
        existingChatLieu.setMaChatLieu(chatLieu.getMaChatLieu());
        existingChatLieu.setTenChatLieu(chatLieu.getTenChatLieu());
        existingChatLieu.setNgayTao(chatLieu.getNgayTao());
        existingChatLieu.setNgaySua(chatLieu.getNgaySua());
        existingChatLieu.setTrangThai(chatLieu.getTrangThai());
        chatLieuRepository.save(existingChatLieu);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        // Kiểm tra và xóa các sản phẩm liên quan đến chất liệu
        // Xóa chất liệu
        chatLieuRepository.deleteById(id);
    }

    @Override
    public ArrayList<ChatLieu> getAllChatLieus() {
        return (ArrayList<ChatLieu>) chatLieuRepository.findAll();
    }

    public Page<ChatLieu> search(String keyword, Pageable pageable) {
        return chatLieuRepository.findByMaChatLieuContainingOrTenChatLieuContaining(keyword, keyword, pageable);
    }

    @Override
    public ChatLieu findByMaChatLieu(String maChatLieu) {
        return chatLieuRepository.findByMaChatLieu(maChatLieu);
    }

    @Override
    public String taoMaChatLieu() {
        Random random = new Random();
        String maCL = "CL" + 1000 + random.nextInt(9000);
        return maCL;
    }
    @Override
    public boolean existsByTenChatLieu(String tenChatLieu) {
        return chatLieuRepository.existsByTenChatLieu(tenChatLieu);
    }

    @Override
    public Page<ChatLieu> locChatLieu(String keyword, LocalDate startDate, LocalDate endDate, Boolean trangThai, Pageable pageable) {
        Date start = startDate != null ? java.sql.Date.valueOf(startDate) : null;
        Date end = endDate != null ? java.sql.Date.valueOf(endDate) : null;
        if (end != null) {
            // Gán endDate thành 23:59:59 để bao phủ hết ngày đó
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            end = cal.getTime();
        }
        return chatLieuRepository.getChatLieuByKeyword(keyword, start, end, trangThai, pageable);
    }
}
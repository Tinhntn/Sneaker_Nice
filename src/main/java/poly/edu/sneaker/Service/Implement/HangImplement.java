package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Repository.HangRepository;
import poly.edu.sneaker.Service.HangService;

import java.time.LocalDate;
import java.util.*;

@Service

public class HangImplement implements HangService {
    @Autowired
    private HangRepository hangRepository;
    @Override
    public Page<Hang> getAll(Pageable pageable) {
        return hangRepository.findAll(pageable);
    }

    @Override
    public Hang getHangById(int id) {
        return hangRepository.findById(id).get();
    }

    @Override
    public void saveHang(Hang hang) {
        Hang existingHang = hangRepository.findByMaHang(hang.getMaHang());
        if (existingHang != null) {
            throw new IllegalArgumentException("Mã hãng đã tồn tại!");
        }
        hangRepository.save(hang);
    }

    @Override
    public void deleteHang(int id) {
        hangRepository.deleteById(id);
    }

    @Override
    public void updateHang(Hang hang) {
        hangRepository.save(hang);
    }

    @Override
    public void updateHang(Hang hang, int id) {
        Hang existingHang = hangRepository.findByMaHang(hang.getMaHang());
        if (existingHang != null && existingHang.getId() != (id)) {
            throw new IllegalArgumentException("Mã hãng đã tồn tại!");
        }
        hangRepository.save(hang);
    }

    @Override
    public String taoMaHang() {
        Random random = new Random();
        String maMaHang = "MH"+String.valueOf(1000+random.nextInt(9000));
        return maMaHang;
    }

    @Override
    public Page<Hang> locHang(String keyword, LocalDate startDate, LocalDate endDate, Boolean trangThai, Pageable pageable) {
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
        return hangRepository.getHangByKeyword(keyword, start, end, trangThai, pageable);
    }

    @Override
    public ArrayList<Hang> getAllHangs() {
        return (ArrayList<Hang>) hangRepository.findAll();
    }
}

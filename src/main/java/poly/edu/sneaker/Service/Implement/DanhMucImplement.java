package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Repository.DanhMucRepository;
import poly.edu.sneaker.Service.DanhMucService;

import java.time.LocalDate;
import java.util.*;

@Service
public class DanhMucImplement implements DanhMucService {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Override
    public Page<DanhMuc> getAll(Pageable pageable) {
        return danhMucRepository.findAll(pageable);
    }

    @Override
    public DanhMuc findDanhMucById(int id) {
        return danhMucRepository.findById(id).orElseThrow(() -> new RuntimeException("DanhMuc not found"));
    }

    @Override
    public void save(DanhMuc danhMuc) {
        danhMucRepository.save(danhMuc);
    }

    @Override
    public void update(DanhMuc danhMuc, int id) {
        DanhMuc existingDanhMuc = danhMucRepository.findById(id).orElseThrow(() -> new RuntimeException("DanhMuc not found"));
        existingDanhMuc.setMaDanhMuc(danhMuc.getMaDanhMuc());
        existingDanhMuc.setTenDanhMuc(danhMuc.getTenDanhMuc());
        existingDanhMuc.setNgayTao(danhMuc.getNgayTao());
        existingDanhMuc.setNgaySua(danhMuc.getNgaySua());
        existingDanhMuc.setTrangThai(danhMuc.getTrangThai());
        danhMucRepository.save(existingDanhMuc);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        DanhMuc danhMuc = danhMucRepository.findById(id).orElseThrow(() -> new RuntimeException("DanhMuc not found"));
        // Xóa tất cả các sản phẩm liên quan đến danh mục
        // Xóa danh mục
        danhMucRepository.deleteById(id);
    }

    @Override
    public Page<DanhMuc> search(String keyword, Pageable pageable) {
        return danhMucRepository.findByMaDanhMucContainingOrTenDanhMucContaining(keyword, keyword, pageable);
    }

    @Override
    public DanhMuc findByMaDanhMuc(String maDanhMuc) {
        return danhMucRepository.findByMaDanhMuc(maDanhMuc);
    }

    @Override
    public String taoMaDanhMuc() {
        Random random = new Random();
        String maDanhMuc = "DM" + 1000 + random.nextInt(9000);
        return maDanhMuc;
    }

    @Override
    public ArrayList<DanhMuc> getAllDanhMucs() {
        return (ArrayList<DanhMuc>) danhMucRepository.findAll();
    }
    @Override
    public boolean existsByTenDanhMuc(String tenDanhMuc) {
        return danhMucRepository.existsByTenDanhMuc(tenDanhMuc);
    }

    @Override
    public Page<DanhMuc> locDanhMuc(String keyword, LocalDate startDate, LocalDate endDate, Boolean trangThai, Pageable pageable) {
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
        return danhMucRepository.getDanhMucByKeyword(keyword, start, end, trangThai, pageable);    }
}
package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Repository.MauSacRepository;
import poly.edu.sneaker.Service.MauSacService;

import java.time.LocalDate;
import java.util.*;


@Service
public class MauSacImplement implements MauSacService {

    @Autowired
    private MauSacRepository mauSacRepository;

    @Override
    public Page<MauSac> getAll(Pageable pageable) {
        return mauSacRepository.findAll(pageable);
    }

    @Override
    public MauSac findMauSacById(int id) {
        return mauSacRepository.findById(id);
    }

    @Override
    public void save(MauSac mauSac) {
        mauSacRepository.save(mauSac);
    }

    @Override
    public Page<MauSac> findAll(Pageable pageable) {
        return mauSacRepository.findAll(pageable);
    }

    @Override
    public void delete(MauSac mauSac) {
        mauSacRepository.delete(mauSac);
    }

    @Override
    public MauSac findById(int id) {
        return mauSacRepository.findById(id);
    }

    @Override
    public void update(MauSac mauSac) {

    }

    @Override
    public ArrayList<MauSac> findAll() {
        return (ArrayList<MauSac>) mauSacRepository.findAll();
    }

    @Override
    public void update(MauSac mauSac, int id) {
        MauSac existingMauSac = mauSacRepository.findById(id);
        existingMauSac.setMaMauSac(mauSac.getMaMauSac());
        existingMauSac.setTenMauSac(mauSac.getTenMauSac());
        existingMauSac.setNgayTao(mauSac.getNgayTao());
        existingMauSac.setNgaySua(mauSac.getNgaySua());
        existingMauSac.setTrangThai(mauSac.getTrangThai());
        mauSacRepository.save(existingMauSac);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        mauSacRepository.deleteById(id);
    }

    @Override
    public Page<MauSac> search(String keyword, Pageable pageable) {
        return mauSacRepository.findByMaMauSacContainingOrTenMauSacContaining(keyword, keyword, pageable);
    }

    @Override
    public MauSac findByMaMauSac(String maMauSac) {
        return mauSacRepository.findByMaMauSac(maMauSac);
    }

    @Override
    public String taoMaMauSac() {
        Random random = new Random();
        String maMauSac = "MMS"+1000+random.nextInt(9000);
        return maMauSac;
    }

    @Override
    public Page<MauSac> locMauSac(String keyword, LocalDate startDate, LocalDate endDate, Boolean trangThai, Pageable pageable) {
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
        return mauSacRepository.getMauSacByKeyword(keyword, start, end, trangThai, pageable);
    }
}

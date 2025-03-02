package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Repository.MauSacRepository;
import poly.edu.sneaker.Service.MauSacService;

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
        return mauSacRepository.findById(id).orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));
    }

    @Override
    public void save(MauSac mauSac) {
        mauSacRepository.save(mauSac);
    }

    @Override
    public void update(MauSac mauSac, int id) {
        MauSac existingMauSac = mauSacRepository.findById(id).orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));
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
}
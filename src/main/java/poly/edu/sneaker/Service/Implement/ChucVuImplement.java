package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Repository.ChucVuRepository;
import poly.edu.sneaker.Service.ChucVuService;

@Service
public class ChucVuImplement implements ChucVuService {

    @Autowired
    private ChucVuRepository chucVuRepository;

    @Override
    public Page<ChucVu> getAll(Pageable pageable) {
        return chucVuRepository.findAll(pageable);
    }

    @Override
    public ChucVu findChucVuById(int id) {
        return chucVuRepository.findById(id).orElseThrow(() -> new RuntimeException("Chức vụ không tồn tại"));
    }

    @Override
    public void save(ChucVu chucVu) {
        chucVuRepository.save(chucVu);
    }

    @Override
    public void update(ChucVu chucVu, int id) {
        ChucVu existingChucVu = chucVuRepository.findById(id).orElseThrow(() -> new RuntimeException("Chức vụ không tồn tại"));
        existingChucVu.setMaChucVu(chucVu.getMaChucVu());
        existingChucVu.setTenChucVu(chucVu.getTenChucVu());
        chucVuRepository.save(existingChucVu);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        chucVuRepository.deleteById(id);
    }

    @Override
    public Page<ChucVu> search(String keyword, Pageable pageable) {
        return chucVuRepository.findByMaChucVuContainingOrTenChucVuContaining(keyword, keyword, pageable);
    }

    @Override
    public ChucVu findByMaChucVu(String maChucVu) {
        return chucVuRepository.findByMaChucVu(maChucVu);
    }
}
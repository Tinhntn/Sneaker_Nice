package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Repository.ChucVuRepository;
import poly.edu.sneaker.Service.ChucVuService;

import java.util.Date;

@Service
public class ChucVuImplement implements ChucVuService {
    private final ChucVuRepository chucVuRepository;

    @Autowired
    public ChucVuImplement(ChucVuRepository chucVuRepository) {
        this.chucVuRepository = chucVuRepository;
    }

    @Override
    public Page<ChucVu> findAll(Pageable pageable) {
        return chucVuRepository.findByDeletedAtFalse(pageable);
    }

    @Override
    public ChucVu findById(int id) {
        return chucVuRepository.findByIdAndDeletedAtFalse(id).orElseThrow(() -> new RuntimeException("Chức vụ không tồn tại"));
    }

    @Override
    public ChucVu save(ChucVu chucVu) {
        return chucVuRepository.save(chucVu);
    }

    @Override
    public void deleteById(int id) {
        ChucVu chucVu = chucVuRepository.findById(id).orElseThrow(() -> new RuntimeException("Chức vụ không tồn tại"));
        chucVu.setDeletedAt(true);
        chucVuRepository.save(chucVu);
    }

    @Override
    public void update(ChucVu chucVu) {
        chucVuRepository.save(chucVu);
    }

    @Override
    public Page<ChucVu> listPage(Pageable pageable) {
        return chucVuRepository.findByDeletedAtFalse(pageable);
    }

    @Override
    public void delete(Integer id) {
        deleteById(id);
    }

    @Override
    public Page<ChucVu> findByTenChucVuOrMaChucVuAndDeletedAt(String tenChucVu, String maChucVu, boolean deletedAt, Pageable pageable) {
        return chucVuRepository.findByTenChucVuContainingAndDeletedAtFalse(tenChucVu, pageable);
    }
}
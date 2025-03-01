package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Repository.DanhMucRepository;
import poly.edu.sneaker.Service.DanhMucService;

import java.util.Date;
import java.util.List;

@Service
public class DanhMucImplement implements DanhMucService {

    private final DanhMucRepository danhMucRepository;

    @Autowired
    public DanhMucImplement(DanhMucRepository danhMucRepository) {
        this.danhMucRepository = danhMucRepository;
    }

    @Override
    public Page<DanhMuc> findAllDanhMuc(Pageable pageable) {
        return danhMucRepository.findAllByDeletedAt(false, pageable);
    }

    @Override
    public DanhMuc findDanhMucById(int id) {
        return danhMucRepository.findByIdAndDeletedAt(id, false).orElse(null);
    }

    @Override
    public DanhMuc save(DanhMuc danhMuc) {
        danhMuc.setNgayTao(new Date());
        danhMuc.setNgaySua(new Date());
        return danhMucRepository.save(danhMuc);
    }

    @Override
    public void updateDanhMuc(DanhMuc danhMuc) {
        danhMuc.setNgaySua(new Date());
        danhMucRepository.save(danhMuc);
    }

    @Override
    public void delete(Integer id) {
        DanhMuc danhMuc = findDanhMucById(id);
        if (danhMuc != null) {
            danhMuc.setDeletedAt(true);
            danhMucRepository.save(danhMuc);
        }
    }

    @Override
    public Page<DanhMuc> findByMaDanhMucContainingAndDeletedAt(String maDanhMuc, boolean deletedAt, Pageable pageable) {
        return danhMucRepository.findByMaDanhMucContainingAndDeletedAt(maDanhMuc, deletedAt, pageable);
    }

    @Override
    public Page<DanhMuc> findByTenDanhMucContainingAndDeletedAt(String tenDanhMuc, boolean deletedAt, Pageable pageable) {
        return danhMucRepository.findByTenDanhMucContainingAndDeletedAt(tenDanhMuc, deletedAt, pageable);
    }

    @Override
    public Page<DanhMuc> findAllByDeletedAt(boolean deletedAt, Pageable pageable) {
        return danhMucRepository.findAllByDeletedAt(deletedAt, pageable);
    }

    @Override
    public List<DanhMuc> findByTenDanhMucContaining(String tenDanhMuc) {
        return danhMucRepository.findByTenDanhMucContaining(tenDanhMuc);
    }
}
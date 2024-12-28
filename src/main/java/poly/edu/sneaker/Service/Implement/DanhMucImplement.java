package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.DanhMuc;
import poly.edu.sneaker.Repository.DanhMucRepository;
import poly.edu.sneaker.Service.DanhMucService;
@Service

public class DanhMucImplement implements DanhMucService {
    @Autowired
    private DanhMucRepository danhMucRepository;
    @Override
    public Page<DanhMuc> findAllDanhMuc(Pageable pageable) {
        return danhMucRepository.findAll(pageable);
    }

    @Override
    public DanhMuc findDanhMucById(int id) {
        return danhMucRepository.findById(id).get();
    }

    @Override
    public DanhMuc save(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    @Override
    public void delete(DanhMuc danhMuc) {
        danhMucRepository.delete(danhMuc);
    }

    @Override
    public void updateDanhMuc(DanhMuc danhMuc) {
        danhMucRepository.save(danhMuc);
    }
}

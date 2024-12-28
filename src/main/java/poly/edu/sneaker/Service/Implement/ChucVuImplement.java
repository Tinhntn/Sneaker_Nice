package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.ChucVu;
import poly.edu.sneaker.Repository.ChucVuRepository;
import poly.edu.sneaker.Service.ChucVuService;
@Service

public class ChucVuImplement implements ChucVuService {
    @Autowired
    private ChucVuRepository chucVuRepository;
    @Override
    public Page<ChucVu> findAll(Pageable pageable) {
        return chucVuRepository.findAll(pageable);
    }

    @Override
    public ChucVu findById(int id) {
        return chucVuRepository.findById(id).get();
    }

    @Override
    public ChucVu save(ChucVu chucVu) {
        return chucVuRepository.save(chucVu);
    }

    @Override
    public void delete(ChucVu chucVu) {
        chucVuRepository.delete(chucVu);
    }
}

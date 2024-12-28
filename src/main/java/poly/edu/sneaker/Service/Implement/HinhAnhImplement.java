package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.HinhAnh;
import poly.edu.sneaker.Repository.HinhAnhRepository;
import poly.edu.sneaker.Service.HinhAnhService;
@Service

public class HinhAnhImplement implements HinhAnhService {
    @Autowired
    HinhAnhRepository hinhAnhRepository;
    @Override
    public Page<HinhAnh> findAll(Pageable pageable) {
        return hinhAnhRepository.findAll(pageable) ;
    }

    @Override
    public HinhAnh findById(int id) {
        return hinhAnhRepository.findById(id).get();
    }

    @Override
    public HinhAnh save(HinhAnh hinhAnh) {
        return hinhAnhRepository.save(hinhAnh);
    }

    @Override
    public void delete(HinhAnh hinhAnh) {
        hinhAnhRepository.delete(hinhAnh);
    }

    @Override
    public void deleteById(int id) {
        hinhAnhRepository.deleteById(id);
    }

    @Override
    public void update(HinhAnh hinhAnh) {
        hinhAnhRepository.save(hinhAnh);
    }
}

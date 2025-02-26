package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.Size;
import poly.edu.sneaker.Repository.SizeRepository;
import poly.edu.sneaker.Service.SizeService;

import java.util.List;

@Service
public class SizeImplement implements SizeService {
    @Autowired
    private SizeRepository sizeRepository;
    @Override
    public Page<Size> findAll(Pageable pageable) {
        return sizeRepository.findAll(pageable);
    }

    @Override
    public Size save(Size size) {
        return sizeRepository.save(size);
    }

    @Override
    public Size findById(int id) {
        return sizeRepository.findById(id).get();
    }

    @Override
    public void delete(Size size) {
        sizeRepository.delete(size);
    }

    @Override
    public void deleteById(int id) {
        sizeRepository.deleteById(id);
    }

    @Override
    public void update(Size size) {
        sizeRepository.save(size);
    }

    @Override
    public List<Size> findAll() {
        return sizeRepository.findAll();
    }
}

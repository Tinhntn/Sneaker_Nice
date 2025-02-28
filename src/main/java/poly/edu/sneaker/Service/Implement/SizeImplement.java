package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.Size;
import poly.edu.sneaker.Repository.SizeRepository;
import poly.edu.sneaker.Service.SizeService;
@Service
public class SizeImplement implements SizeService {
    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public Page<Size> getAll(Pageable pageable) {
        return sizeRepository.getAll(pageable);
    }

    @Override
    public Size getSizeById(int id) {
        return sizeRepository.findById(id).get();
    }

    @Override
    public void saveSize(Size size) {
        Size existingHang = sizeRepository.findByMaSize(size.getMaSize());
        if (existingHang != null) {
            throw new IllegalArgumentException("Mã hãng đã tồn tại!");
        }
        sizeRepository.save(size);
    }

//    @Override
//    public void deleteSize(int id) {
//
//    }

    @Override
    public void updateSize(Size size, int id) {

        Size existingHang = sizeRepository.findByMaSize(size.getMaSize());
        if (existingHang != null && existingHang.getId() != (id)) {
            throw new IllegalArgumentException("Mã hãng đã tồn tại!");
        }
        sizeRepository.save(size);
    }

}

package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Repository.HangRepository;
import poly.edu.sneaker.Service.HangService;
@Service

public class HangImplement implements HangService {
    @Autowired
    private HangRepository hangRepository;
    @Override
    public Page<Hang> getAllHangs(Pageable pageable) {
        return hangRepository.findAll(pageable);
    }

    @Override
    public Hang getHangById(int id) {
        return hangRepository.findById(id).get();
    }

    @Override
    public Hang saveHang(Hang hang) {
        return hangRepository.save(hang);
    }

    @Override
    public void deleteHang(int id) {
        hangRepository.deleteById(id);
    }

    @Override
    public void updateHang(Hang hang) {
        hangRepository.save(hang);
    }
}

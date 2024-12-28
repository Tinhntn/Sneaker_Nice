package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.LichSuTrangThai;
import poly.edu.sneaker.Repository.LichSuTrangThaiRepository;
import poly.edu.sneaker.Service.LichSuTrnngThaiService;
@Service

public class LichSuTrangThaiImplement implements LichSuTrnngThaiService {
    @Autowired
    private LichSuTrangThaiRepository lichSuTrangThaiRepository;
    @Override
    public Page<LichSuTrangThai> findAllLichSuTrangThai(Pageable pageable) {
        return lichSuTrangThaiRepository.findAll(pageable);
    }

    @Override
    public void saveLichSuTrangThai(LichSuTrangThai lt) {
        lichSuTrangThaiRepository.save(lt);
    }

    @Override
    public void deleteLichSuTrangThai(LichSuTrangThai lt) {
        lichSuTrangThaiRepository.delete(lt);

    }

    @Override
    public LichSuTrangThai findLichSuTrangThaiById(Integer id) {
        return lichSuTrangThaiRepository.findById(id).get();
    }

    @Override
    public void updateLichSuTrangThai(LichSuTrangThai lt) {
        lichSuTrangThaiRepository.save(lt);
    }
}

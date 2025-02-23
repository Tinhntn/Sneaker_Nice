package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.TrangThaiDonHang;
import poly.edu.sneaker.Repository.LichSuTrangThaiRepository;
import poly.edu.sneaker.Service.LichSuTrnngThaiService;
@Service

public class LichSuTrangThaiImplement implements LichSuTrnngThaiService {
    @Autowired
    private LichSuTrangThaiRepository lichSuTrangThaiRepository;
    @Override
    public Page<TrangThaiDonHang> findAllLichSuTrangThai(Pageable pageable) {
        return lichSuTrangThaiRepository.findAll(pageable);
    }

    @Override
    public void saveLichSuTrangThai(TrangThaiDonHang lt) {
        lichSuTrangThaiRepository.save(lt);
    }

    @Override
    public void deleteLichSuTrangThai(TrangThaiDonHang lt) {
        lichSuTrangThaiRepository.delete(lt);

    }

    @Override
    public TrangThaiDonHang findLichSuTrangThaiById(Integer id) {
        return lichSuTrangThaiRepository.findById(id).get();
    }

    @Override
    public void updateLichSuTrangThai(TrangThaiDonHang lt) {
        lichSuTrangThaiRepository.save(lt);
    }
}

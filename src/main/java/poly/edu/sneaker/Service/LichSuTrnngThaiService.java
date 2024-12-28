package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.LichSuTrangThai;

public interface LichSuTrnngThaiService {
    Page<LichSuTrangThai> findAllLichSuTrangThai(Pageable pageable);
    void saveLichSuTrangThai(LichSuTrangThai lt);
    void deleteLichSuTrangThai(LichSuTrangThai lt);
    LichSuTrangThai findLichSuTrangThaiById(Integer id);
    void updateLichSuTrangThai(LichSuTrangThai lt);
}

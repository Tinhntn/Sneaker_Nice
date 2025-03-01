package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.MauSac;

public interface MauSacService {
    Page<MauSac> findByMaMauSacContainingOrTenMauSacContaining(String maMauSac, String tenMauSac, Pageable pageable);
    Page<MauSac> findAll(Pageable pageable);
    MauSac findById(Integer id);
    void save(MauSac mauSac);
    void update(MauSac mauSac);
    void deleteById(Integer id);
}
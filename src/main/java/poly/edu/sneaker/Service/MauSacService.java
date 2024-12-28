package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.MauSac;

public interface MauSacService {
    Page<MauSac> findAll(Pageable pageable);
    MauSac save(MauSac mauSac);
    void delete(MauSac mauSac);
    MauSac findById(int id);
    void update(MauSac mauSac);
}

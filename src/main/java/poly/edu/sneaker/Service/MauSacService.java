package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Model.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface MauSacService {
    Page<MauSac> findAll(Pageable pageable);
    void delete(MauSac mauSac);
    MauSac findById(int id);
    void update(MauSac mauSac);
    ArrayList<MauSac> findAll();
    Page<MauSac> getAll(Pageable pageable);

    MauSac findMauSacById(int id);

    void save(MauSac mauSac);

    void update(MauSac mauSac, int id);

    void deleteById(int id);

    Page<MauSac> search(String keyword, Pageable pageable);

    MauSac findByMaMauSac(String maMauSac);

    String taoMaMauSac();

    Page<MauSac> locMauSac(String keyword, LocalDate startDate, LocalDate endDate, Boolean trangThai, Pageable pageable);

}

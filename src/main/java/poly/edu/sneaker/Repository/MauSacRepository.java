package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.MauSac;

import java.util.List;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {
    Page<MauSac> findByMaMauSacContainingOrTenMauSacContaining(String maMauSac, String tenMauSac, Pageable pageable);

    MauSac findByMaMauSac(String maMauSac);
    MauSac findById(int id);
    @Query(value = "SELECT * FROM MauSac ms " +
            "WHERE ms.trang_thai = 1;",
            nativeQuery = true)
    List<MauSac> getAllMauSacTimKiem();
}
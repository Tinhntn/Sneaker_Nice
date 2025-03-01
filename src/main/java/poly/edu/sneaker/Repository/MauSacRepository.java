package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.MauSac;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    Page<MauSac> findByMaMauSacContainingOrTenMauSacContaining(String maMauSac, String tenMauSac, Pageable pageable);
}
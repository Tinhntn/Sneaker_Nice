package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChucVu;

import java.util.ArrayList;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, Integer> {
    Page<ChucVu> findByMaChucVuContainingOrTenChucVuContaining(String maChucVu, String tenChucVu, Pageable pageable);

    ChucVu findByMaChucVu(String maChucVu);
    ChucVu findAllById(int id);
}
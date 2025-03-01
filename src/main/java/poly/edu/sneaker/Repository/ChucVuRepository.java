package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChucVu;

import java.util.Optional;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, Integer> {

    Page<ChucVu> findByDeletedAtFalse(Pageable pageable);

    Optional<ChucVu> findByIdAndDeletedAtFalse(int id);

    Page<ChucVu> findByTenChucVuContainingAndDeletedAtFalse(String tenChucVu, Pageable pageable);

    ChucVu findByMaChucVuAndDeletedAtFalse(String maChucVu);

    Page<ChucVu> findByIdOrMaChucVuAndDeletedAtFalse(Integer id, String maChucVu, Pageable pageable);
}
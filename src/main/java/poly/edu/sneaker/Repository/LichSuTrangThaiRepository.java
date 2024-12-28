package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.LichSuTrangThai;

@Repository
public interface LichSuTrangThaiRepository extends JpaRepository<LichSuTrangThai, Integer> {
}

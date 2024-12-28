package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChucVu;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, Integer>{
}

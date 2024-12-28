package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.HinhAnh;

@Repository
public interface HinhAnhRepository extends JpaRepository<HinhAnh, Integer> {
}

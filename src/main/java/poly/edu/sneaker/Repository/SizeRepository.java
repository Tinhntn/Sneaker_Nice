package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {
}

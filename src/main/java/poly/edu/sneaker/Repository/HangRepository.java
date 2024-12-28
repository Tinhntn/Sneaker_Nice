package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.Hang;
@Repository
public interface HangRepository extends JpaRepository<Hang, Integer> {
}

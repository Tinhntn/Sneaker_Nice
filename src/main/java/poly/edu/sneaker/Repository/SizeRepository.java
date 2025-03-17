package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {

    @Query("SELECT z FROM Size z")
    Page<Size> getAll(Pageable pageable);

    Size findByMaSize(String maSize);
}

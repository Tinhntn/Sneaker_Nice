package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.Hang;
import poly.edu.sneaker.Model.Size;

import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {

    @Query("SELECT z FROM Size z")
    Page<Size> getAll(Pageable pageable);

    Size findByMaSize(String maSize);

    @Query(value = "SELECT * FROM Size s " +
            "WHERE s.trang_thai = 1;",
            nativeQuery = true)
    List<Size> getAllSizeTimKiem();
}

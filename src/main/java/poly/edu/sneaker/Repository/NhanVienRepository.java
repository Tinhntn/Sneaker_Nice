package poly.edu.sneaker.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.NhanVien;

import java.util.UUID;

@Repository
    public interface NhanVienRepository extends JpaRepository<NhanVien, Integer > {

}

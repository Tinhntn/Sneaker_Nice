package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.GioHangChiTiet;
@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {
}

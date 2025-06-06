package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.GioHang;
@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    GioHang findByIdKhachHang_Id(int id);
}

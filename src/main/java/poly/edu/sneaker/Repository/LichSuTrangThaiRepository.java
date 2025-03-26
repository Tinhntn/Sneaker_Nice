package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.TrangThaiDonHang;

import java.util.List;

@Repository
public interface LichSuTrangThaiRepository extends JpaRepository<TrangThaiDonHang, Integer> {
    List<TrangThaiDonHang> findAllByIdHoaDon_Id(int idHD);
}

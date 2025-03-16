package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.DAO.HoaDonChiTietOnlCustom;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;

import java.util.List;

@Repository
public interface HoaDonChiTietOnlRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    List<HoaDonChiTietOnlCustom> findByIdHoaDon(HoaDon idHoaDon);

}

package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.HoaDon;

import java.util.List;

@Repository
public interface HoaDonRepository  extends JpaRepository<HoaDon, Integer> {
    @Query(value = "select * from hoadon where trang_thai = 0", nativeQuery = true)
    List<HoaDon> getAllHoaDon();

    @Query(value = "SELECT * FROM hoadon WHERE id = :id", nativeQuery = true)
    HoaDon findByHDId(@Param("id") Integer id);
}

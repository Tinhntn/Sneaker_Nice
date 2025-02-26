package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.Hang;
@Repository
public interface HangRepository extends JpaRepository<Hang, Integer> {

    @Query("SELECT h.id, h.maHang, h.tenHang, h.ngayTao, h.ngaySua, h.trangThai FROM Hang h")
    Page<Hang> getAll(Pageable pageable);

    Hang findByMaHang(String maHang);

}

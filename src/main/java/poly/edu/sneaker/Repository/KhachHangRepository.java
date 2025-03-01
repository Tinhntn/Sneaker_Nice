package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.KhachHang;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    Page<KhachHang> findByTenKhachHangContainingOrEmailContainingOrSdtContaining(String tenKhachHang, String email, String sdt, Pageable pageable);
}
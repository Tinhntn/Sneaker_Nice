package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.KhachHang;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    Page<KhachHang> findByMaKhachHangContainingOrTenKhachHangContaining(String maKhachHang, String tenKhachHang, Pageable pageable);

    KhachHang findByMaKhachHang(String maKhachHang);

    KhachHang findByEmail(String email);
    KhachHang findByEmailAndMatKhau(String email,String matKhau);
    boolean existsKhachHangByEmail(String email);
    Page<KhachHang> findByTrangThai(Boolean trangThai, Pageable pageable);

    @Query("SELECT k FROM KhachHang k WHERE k.sdt = :sdt")
    KhachHang TimKhachHangQuaSDT(String sdt);

    @Query("SELECT k FROM KhachHang k WHERE k.id = :id")
    KhachHang findKhachHangByIdkh(Integer id);
}
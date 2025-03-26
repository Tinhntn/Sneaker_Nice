package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.DAO.KhuyenMaiCustom;
import poly.edu.sneaker.Model.KhuyenMai;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    @Query("""
           SELECT km.id AS id, km.maKhuyenMai AS maKhuyenMai, km.tenKhuyenMai AS tenKhuyenMai, 
           km.moTa AS moTa, km.dieuKienApDung AS dieuKienApDung, km.loaiKhuyenMai AS loaiKhuyenMai, 
           km.giaTriGiam AS giaTriGiam, km.mucGiamGiaToiDa AS mucGiamGiaToiDa, km.daSuDung AS daSuDung, 
           km.soLuong AS soLuong, km.ngayBatDau AS ngayBatDau, km.ngayKetThuc AS ngayKetThuc, 
           km.ngayTao AS ngayTao, km.ngaySua AS ngaySua, km.trangThai AS trangThai
           FROM KhuyenMai km
             """)
    Page<KhuyenMaiCustom> getAll(Pageable pageable);

    KhuyenMai findByMaKhuyenMai(String maKhuyenMai);

    Page<KhuyenMaiCustom> findKhuyenMaiByMaKhuyenMaiContainingOrTenKhuyenMaiContaining(String maKhuyenMai, String tenKhuyenMai, Pageable pageable);
    @Query("SELECT km FROM KhuyenMai km WHERE km.maKhuyenMai = :maKhuyenMai")
    KhuyenMai TimKhuyenMaiQuaMa(String maKhuyenMai);

    KhuyenMai findById(int id);
}

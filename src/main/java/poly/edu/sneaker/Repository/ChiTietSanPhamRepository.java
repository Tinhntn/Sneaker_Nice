package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChiTietSanPham;

import java.util.List;
import java.util.ArrayList;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {

    Page<ChiTietSanPham> findChiTietSanPhamByIdSanPham_Id(int idSanPham, Pageable pageable);

    @Query("SELECT ctp FROM ChiTietSanPham ctp " +
            "WHERE ctp.ngayTao = (SELECT MAX(ctp2.ngayTao) FROM ChiTietSanPham ctp2 WHERE ctp2.idSanPham.id = ctp.idSanPham.id and ctp.trangThai = true) ")
    Page<ChiTietSanPham> findFirstRecordForEachProduct(Pageable pageable);

    @Query(value = "select * from ChiTietSanPham", nativeQuery = true)
    List<ChiTietSanPham> getALl();

    ChiTietSanPham findById(int id);


    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :idSanPham AND c.idMauSac.id = :idMauSac and c.trangThai=true")
    ChiTietSanPham findChiTietSanPhamByIdAndIdMauSacAndTrangThai(@Param("idSanPham") int idSanPham, @Param("idMauSac") int idMauSac,@Param("trangThai")boolean trangThai);
    public abstract ArrayList<ChiTietSanPham> findByIdSanPham_IdAndTrangThai(int idSanPham,boolean trangThai);
    ChiTietSanPham findChiTietSanPhamByIdSanPham_IdAndIdSize_IdAndIdMauSac_Id(int idSanPham,int idSize,int idMauSac);
}

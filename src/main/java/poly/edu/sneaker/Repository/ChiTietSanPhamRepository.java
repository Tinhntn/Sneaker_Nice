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

    // code quan
    @Query(value = "SELECT * FROM chitietsanpham WHERE chitietsanpham.trang_thai = 1",
            nativeQuery = true)
    Page<ChiTietSanPham> getAllChiTieSanPhamDAO(Pageable pageable);

    @Query(value = "SELECT * FROM chitietsanpham WHERE id = :id", nativeQuery = true)
    ChiTietSanPham getChiTietSanPhamById(@Param("id") Integer id);

    @Query("SELECT c FROM ChiTietSanPham c WHERE " +
            "( c.idSanPham.tenSanPham LIKE CONCAT('%', :tenSanPham, '%')) OR " +
            "(:idSize IS NOT NULL AND c.idSize.id = :idSize) OR " +
            "(:idMauSac IS NOT NULL AND c.idMauSac.id = :idMauSac) OR " +
            "(:idDanhMuc IS NOT NULL AND c.idSanPham.idDanhMuc.id = :idDanhMuc) OR " +
            "(:idHang IS NOT NULL AND c.idSanPham.idHang.id = :idHang) OR " +
            "(:idChatLieu IS NOT NULL AND c.idSanPham.idChatLieu.id = :idChatLieu)")
    Page<ChiTietSanPham> timKiemSanPhamQuaCTSP(@Param("tenSanPham") String tenSanPham,
                                               @Param("idSize") Integer idSize,
                                               @Param("idMauSac") Integer idMauSac,
                                               @Param("idDanhMuc") Integer idDanhMuc,
                                               @Param("idHang") Integer idHang,
                                               @Param("idChatLieu") Integer idChatLieu,
                                               Pageable pageable);

    //code quan end

    //code hung
    @Query("SELECT c FROM ChiTietSanPham c ORDER BY c.ngayTao DESC")
    List<ChiTietSanPham> findTop10NewestProducts(Pageable pageable);
    // code hung end

}

package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChiTietSanPham;

import java.util.ArrayList;

import java.util.List;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {

    Page<ChiTietSanPham> findChiTietSanPhamByIdSanPham_Id(int idSanPham, Pageable pageable);

    @Query("SELECT ctp FROM ChiTietSanPham ctp " +
            "WHERE ctp.ngayTao = (SELECT MAX(ctp2.ngayTao) FROM ChiTietSanPham ctp2 WHERE ctp2.idSanPham.id = ctp.idSanPham.id and ctp.trangThai = true) ")
    Page<ChiTietSanPham> findFirstRecordForEachProduct(Pageable pageable);
    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :idSanPham AND c.idMauSac.id = :idMauSac and c.trangThai=true")
    ChiTietSanPham findChiTietSanPhamByIdAndIdMauSacAndTrangThai(@Param("idSanPham") int idSanPham, @Param("idMauSac") int idMauSac,@Param("trangThai")boolean trangThai);
    public abstract ArrayList<ChiTietSanPham> findByIdSanPham_IdAndTrangThai(int idSanPham,boolean trangThai);
    ChiTietSanPham findChiTietSanPhamByIdSanPham_IdAndIdSize_IdAndIdMauSac_Id(int idSanPham,int idSize,int idMauSac);

    // 3) Tìm kiếm theo nhiều trường (tên SP, tên hãng, chất liệu, danh mục)

    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
            "JOIN ctsp.idSanPham sp " +
            "JOIN sp.idHang h " +
            "JOIN sp.idChatLieu cl " +
            "JOIN sp.idDanhMuc dm " +
            "WHERE sp.tenSanPham LIKE %:keyword% " +
            "OR h.tenHang LIKE %:keyword% " +
            "OR cl.tenChatLieu LIKE %:keyword% " +
            "OR dm.tenDanhMuc LIKE %:keyword%")
    List<ChiTietSanPham> searchByMultipleFields(@Param("keyword") String keyword);

    @Query("SELECT ctp FROM ChiTietSanPham ctp " +
            "JOIN ctp.idSanPham sp " +
            "JOIN sp.idHang h " +
            "JOIN sp.idChatLieu cl " +
            "WHERE (:tenHang IS NULL OR h.tenHang = :tenHang) " +
            "AND (:chatLieu IS NULL OR cl.tenChatLieu = :chatLieu) " +
            "AND ctp.giaBan BETWEEN :minPrice AND :maxPrice")
    Page<ChiTietSanPham> filterByHangAndPrice(
            @Param("tenHang") String tenHang,
            @Param("chatLieu") String chatLieu,
            @Param("minPrice") long minPrice,
            @Param("maxPrice") long maxPrice,
            Pageable pageable);


}

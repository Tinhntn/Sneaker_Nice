package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.ChiTietSanPham;

import java.util.List;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {

    // 1) Lấy danh sách ChiTietSanPham theo idSanPham (phân trang)
    Page<ChiTietSanPham> findChiTietSanPhamByIdSanPham_Id(int idSanPham, Pageable pageable);

    // 2) Lấy bản ghi đầu tiên (theo id nhỏ nhất) cho mỗi sản phẩm
    //    Mục đích: hiển thị 1 record đại diện cho mỗi SP
    @Query("SELECT ctp FROM ChiTietSanPham ctp " +
            "WHERE ctp.id = (SELECT MIN(ctp2.id) FROM ChiTietSanPham ctp2 " +
            "WHERE ctp2.idSanPham.id = ctp.idSanPham.id)")
    Page<ChiTietSanPham> findFirstRecordForEachProduct(Pageable pageable);

    // 3) Tìm kiếm theo nhiều trường (tên SP, tên hãng, chất liệu, danh mục)
    //    Sử dụng LIKE để tìm chuỗi chứa keyword
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

    // 4) Lọc theo hãng (tìm sản phẩm có hãng bằng với tham số)
    @Query("SELECT ctp FROM ChiTietSanPham ctp " +
            "JOIN ctp.idSanPham sp " +
            "JOIN sp.idHang h " +
            "WHERE h.tenHang = :tenHang")
    Page<ChiTietSanPham> findByHang(@Param("tenHang") String tenHang, Pageable pageable);

    // 5) Lọc theo hãng và khoảng giá
    @Query("SELECT ctp FROM ChiTietSanPham ctp " +
            "JOIN ctp.idSanPham sp " +
            "JOIN sp.idHang h " +
            "WHERE (:tenHang IS NULL OR h.tenHang = :tenHang) " +
            "AND ctp.giaBan BETWEEN :minPrice AND :maxPrice")
    Page<ChiTietSanPham> filterByHangAndPrice(
            @Param("tenHang") String tenHang,
            @Param("minPrice") long minPrice,
            @Param("maxPrice") long maxPrice,
            Pageable pageable);
}

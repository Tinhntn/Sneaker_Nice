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

    // Lấy sản phẩm theo id của sản phẩm (SanPham)
    Page<ChiTietSanPham> findChiTietSanPhamByIdSanPham_Id(int idSanPham, Pageable pageable);

    List<ChiTietSanPham> findChiTietSanPhamByIdSanPham_Id(int idSanPham);

    // Lấy bản ghi mới nhất cho mỗi sản phẩm
    @Query("SELECT ctp FROM ChiTietSanPham ctp " +
            "WHERE ctp.id IN (" +
            " SELECT MIN(c.id) FROM ChiTietSanPham c " +
            " WHERE c.idSanPham.trangThai = true AND c.soLuong > 0 AND c.trangThai = true " +
            " GROUP BY c.idSanPham" +
            ")")
    Page<ChiTietSanPham> findFirstRecordForEachProduct(Pageable pageable);

    @Query(value = "select * from ChiTietSanPham", nativeQuery = true)
    List<ChiTietSanPham> getALl();

    ChiTietSanPham findById(int id);


    //code quan end

    //code hung
    @Query("SELECT c FROM ChiTietSanPham c WHERE c.soLuong > 0 ORDER BY c.ngayTao DESC")
    List<ChiTietSanPham> findTop10NewestProducts(Pageable pageable);
    // code hung end

    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :id AND c.idMauSac.id = :idMauSac AND c.trangThai = true")
    List<ChiTietSanPham> findChiTietSanPhamByIdSPAndIdMauSac(@Param("id") int id,
                                                             @Param("idMauSac") int idMauSac);


    // Lấy danh sách chi tiết sản phẩm theo id sản phẩm với trạng thái đang hoạt động
    ArrayList<ChiTietSanPham> findChiTietSanPhamByIdSanPham_IdAndTrangThai(int idSanPham, boolean trangThai);

    // Tìm chi tiết sản phẩm theo id sản phẩm, id size và id màu sắc
    ChiTietSanPham findChiTietSanPhamByIdSanPham_IdAndIdSize_IdAndIdMauSac_Id(int idSanPham, int idSize, int idMauSac);

    // Tìm kiếm theo nhiều trường (tên SP, tên hãng, chất liệu, danh mục)
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

    // Lọc sản phẩm theo hãng, chất liệu và khoảng giá
    @Query("SELECT ctp FROM ChiTietSanPham ctp " +
            "JOIN ctp.idSanPham sp " +
            "JOIN sp.idHang h " +
            "JOIN sp.idChatLieu cl " +
            "WHERE (:tenHang IS NULL OR h.tenHang = :tenHang) " +
            "AND (:chatLieu IS NULL OR cl.tenChatLieu = :chatLieu) " +
            "AND ctp.giaBan BETWEEN :minPrice AND :maxPrice " +
            "AND ctp.soLuong > 0 " + // Thêm điều kiện kiểm tra số lượng
            "AND ctp.trangThai = true")
    // Thêm điều kiện kiểm tra trạng thái
    Page<ChiTietSanPham> filterByHangAndPrice(
            @Param("tenHang") String tenHang,
            @Param("chatLieu") String chatLieu,
            @Param("minPrice") long minPrice,
            @Param("maxPrice") long maxPrice,
            Pageable pageable);

    // Lấy danh sách chất liệu (chatLieu) có sẵn theo hãng được chọn (cascading filter)
    @Query("SELECT DISTINCT cl.tenChatLieu FROM ChiTietSanPham ctp " +
            "JOIN ctp.idSanPham sp " +
            "JOIN sp.idChatLieu cl " +
            "WHERE (:hang IS NULL OR sp.idHang.tenHang = :hang)")
    List<String> findDistinctChatLieuByHang(@Param("hang") String hang);

    // Lấy danh sách hãng có sẵn theo chất liệu được chọn (cascading filter)
    @Query("SELECT DISTINCT h.tenHang FROM ChiTietSanPham ctp " +
            "JOIN ctp.idSanPham sp " +
            "JOIN sp.idHang h " +
            "JOIN sp.idChatLieu cl " +
            "WHERE (:chatLieu IS NULL OR cl.tenChatLieu = :chatLieu)")
    List<String> findDistinctHangByChatLieu(@Param("chatLieu") String chatLieu);

    // code quan
    @Query(value = "SELECT * FROM chitietsanpham WHERE chitietsanpham.trang_thai = 1",
            nativeQuery = true)
    Page<ChiTietSanPham> getAllChiTieSanPhamDAO(Pageable pageable);

    @Query(value = "SELECT * FROM chitietsanpham WHERE id = :id", nativeQuery = true)
    ChiTietSanPham getChiTietSanPhamById(@Param("id") Integer id);

    @Query("SELECT c FROM ChiTietSanPham c WHERE " +
            "c.trangThai = true AND (" +
            "(:tenSanPham IS NOT NULL AND :tenSanPham <> '' AND c.idSanPham.tenSanPham LIKE CONCAT('%', :tenSanPham, '%')) OR " +
            "(:idSize IS NOT NULL AND c.idSize.id = :idSize) OR " +
            "(:idMauSac IS NOT NULL AND c.idMauSac.id = :idMauSac) OR " +
            "(:idDanhMuc IS NOT NULL AND c.idSanPham.idDanhMuc.id = :idDanhMuc) OR " +
            "(:idHang IS NOT NULL AND c.idSanPham.idHang.id = :idHang) OR " +
            "(:idChatLieu IS NOT NULL AND c.idSanPham.idChatLieu.id = :idChatLieu))")
    Page<ChiTietSanPham> timKiemSanPhamQuaCTSP(@Param("tenSanPham") String tenSanPham,
                                               @Param("idSize") Integer idSize,
                                               @Param("idMauSac") Integer idMauSac,
                                               @Param("idDanhMuc") Integer idDanhMuc,
                                               @Param("idHang") Integer idHang,
                                               @Param("idChatLieu") Integer idChatLieu,
                                               Pageable pageable);

    @Query("SELECT c FROM ChiTietSanPham c WHERE " +
            "(:idSanPham IS NULL OR c.idSanPham.id = :idSanPham) " +
            "AND (:idSize IS NULL OR c.idSize.id = :idSize) " +
            "AND (:idMauSac IS NULL OR c.idMauSac.id = :idMauSac) " +
            "AND (:trangThai IS NULL OR c.trangThai = :trangThai)")
    Page<ChiTietSanPham> locChiTietSanPham(
            @Param("idSanPham") Integer idSanPham,
            @Param("idSize") Integer idSize,
            @Param("idMauSac") Integer idMauSac,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable
    );
}

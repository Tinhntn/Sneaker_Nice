package poly.edu.sneaker.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.DAO.NhanVienCustom;
import poly.edu.sneaker.Model.NhanVien;

import java.util.UUID;

@Repository
    public interface NhanVienRepository extends JpaRepository<NhanVien, Integer > {

        @Query(value = """
        SELECT nv.id AS id, nv.maNhanVien AS maNhanVien, nv.hoVaTen AS hoVaTen, 
               nv.gioiTinh AS gioiTinh, nv.ngaySinh AS ngaySinh, nv.diaChi AS diaChi, 
               nv.sdt AS sdt, nv.email AS email, nv.matKhau AS matKhau, 
               cv.tenChucVu AS tenChucVu, nv.ngayTao AS ngayTao, nv.ngaySua AS ngaySua, 
               nv.trangThai AS trangThai
        FROM NhanVien nv
        JOIN nv.idChucVu cv
    """)
        Page<NhanVienCustom> getAll(Pageable pageable);

        NhanVien findByMaNhanVien(String maNhanVien);


    @Query("""
    SELECT nv.id AS id, nv.maNhanVien AS maNhanVien, nv.hoVaTen AS hoVaTen, 
           nv.gioiTinh AS gioiTinh, nv.ngaySinh AS ngaySinh, nv.diaChi AS diaChi, 
           nv.sdt AS sdt, nv.email AS email, nv.matKhau AS matKhau, 
           cv.tenChucVu AS tenChucVu, nv.ngayTao AS ngayTao, nv.ngaySua AS ngaySua, 
           nv.trangThai AS trangThai
    FROM NhanVien nv
    JOIN nv.idChucVu cv
    WHERE (:keyword IS NULL OR nv.maNhanVien LIKE %:keyword% 
           OR nv.hoVaTen LIKE %:keyword% 
           OR nv.email LIKE %:keyword%)
      AND (:trangThai IS NULL OR nv.trangThai = :trangThai)
""")

    Page<NhanVienCustom> searchNhanVien(@Param("keyword") String keyword,@Param("trangThai") Boolean trangThai, Pageable pageable);
    NhanVien findByEmailAndMatKhau(String email,String matKhau);
    NhanVien findByEmail(String email);
}

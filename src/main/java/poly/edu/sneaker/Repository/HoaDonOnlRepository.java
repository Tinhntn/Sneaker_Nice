package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;

import java.time.LocalDate;
import java.util.Date;

@Repository
public interface HoaDonOnlRepository extends JpaRepository<HoaDon, Integer> {


    @Query(value = "SELECT * From HoaDon\n" +
            "WHERE \n" +
            "    hoadon.id = :id;", nativeQuery = true)
    HoaDon getAllHoaDonByid(int id);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true")
    Page<HoaDonOnlCustom> getHoaDonCustomTatCa(Pageable pageable);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 6")
    Page<HoaDonOnlCustom> getHoaDonCustomDH(Pageable pageable);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN NhanVien nv ON h.idNhanVien.id = nv.id \n " +
            "LEFT JOIN KhachHang kh ON h.idKhachHang.id = kh.id " +
            "LEFT JOIN KhuyenMai km ON h.idKhuyenMai.id = km.id " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 2")
    Page<HoaDonOnlCustom> getHoaDonCustomCXN(Pageable pageable);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 3")
    Page<HoaDonOnlCustom> getHoaDonCustomCLH(Pageable pageable);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 4")
    Page<HoaDonOnlCustom> getHoaDonCustomDG(Pageable pageable);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 5")
    Page<HoaDonOnlCustom> getHoaDonCustomHT(Pageable pageable);

    //Hóa đơn của khách hàng
    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true AND h.idKhachHang.id = :idKhachHang")
    Page<HoaDonOnlCustom> getHoaDonCustomTatCaKH(Pageable pageable, Integer idKhachHang);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 6 AND h.idKhachHang.id = :idKhachHang")
    Page<HoaDonOnlCustom> getHoaDonCustomDHKH(Pageable pageable, Integer idKhachHang);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN NhanVien nv ON h.idNhanVien.id = nv.id \n " +
            "LEFT JOIN KhachHang kh ON h.idKhachHang.id = kh.id " +
            "LEFT JOIN KhuyenMai km ON h.idKhuyenMai.id = km.id " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 2 AND h.idKhachHang.id = :idKhachHang")
    Page<HoaDonOnlCustom> getHoaDonCustomCXNKH(Pageable pageable, Integer idKhachHang);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 3 AND h.idKhachHang.id = :idKhachHang")
    Page<HoaDonOnlCustom> getHoaDonCustomCLHKH(Pageable pageable, Integer idKhachHang);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 4 AND h.idKhachHang.id = :idKhachHang")
    Page<HoaDonOnlCustom> getHoaDonCustomDGKH(Pageable pageable, Integer idKhachHang);

    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true AND h.trangThai = 5 AND h.idKhachHang.id = :idKhachHang")
    Page<HoaDonOnlCustom> getHoaDonCustomHTKH(Pageable pageable,Integer idKhachHang);

    //End hóa đon của khách hàng

    // code tìm kiếm hóa đơn
    @Query("SELECT h.id AS id, nv.hoVaTen AS tenNhanVien, kh.tenKhachHang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, h.maHoaDon AS maHoaDon, h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, h.thanhTien AS thanhTien, h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, h.phiShip AS phiShip, h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, h.tenNguoiGiao AS tenNguoiGiao, h.sdtNguoiGiao AS sdtNguoiGiao, " +
            "h.emailNguoiNhan AS emailNguoiNhan, h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, h.quanHuyen AS quanHuyen, h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, h.trangThai AS trangThai, h.ngayTao AS ngayTao, h.ngaySua AS ngaySua " +
            "FROM HoaDon h " +
            "LEFT JOIN h.idNhanVien nv " +
            "LEFT JOIN h.idKhachHang kh " +
            "LEFT JOIN h.idKhuyenMai km " +
            "WHERE h.loaiHoaDon = true " +
            "AND (:keyword IS NULL OR h.maHoaDon LIKE %:keyword% OR kh.tenKhachHang LIKE %:keyword%) " +
            "AND (:startDate IS NULL OR :endDate IS NULL OR h.ngayTao BETWEEN :startDate AND :endDate)")
    Page<HoaDonOnlCustom> searchHoaDonCustom(@Param("keyword") String keyword,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate,
                                             Pageable pageable);

    // end code tìm kiếm hóa đơn


}

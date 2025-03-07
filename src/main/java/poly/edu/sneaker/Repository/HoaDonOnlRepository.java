package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.DAO.HoaDonOnlCustom;
import poly.edu.sneaker.Model.HoaDon;

@Repository
public interface HoaDonOnlRepository extends JpaRepository<HoaDon, Integer> {

    @Query(value = "SELECT h.id AS id, " +
            "nv.hoVaTen AS tenNhanVien, " +
            "kh.ten_khach_hang AS tenKhachHang, " +
            "km.tenKhuyenMai AS tenKhuyenMai, " +
            "h.maHoaDon AS maHoaDon, " +
            "h.tongTien AS tongTien, " +
            "h.tongTienGiam AS tongTienGiam, " +
            "h.thanhTien AS thanhTien, " +
            "h.tienKhachDua AS tienKhachDua, " +
            "h.tienThua AS tienThua, " +
            "h.phiShip AS phiShip, " +
            "h.ngayGiaoHang AS ngayGiaoHang, " +
            "h.donViGiaoHang AS donViGiaoHang, " +
            "h.tenNguoiGiaoHang AS tenNguoiGiaoHang, " +
            "h.emailNguoiNhan AS emailNguoiNhan, " +
            "h.diaChiChiTiet AS diaChiChiTiet, " +
            "h.tinhThanhPho AS tinhThanhPho, " +
            "h.quanHuyen AS quanHuyen, " +
            "h.phuongXa AS phuongXa, " +
            "h.loaiThanhToan AS loaiThanhToan, " +
            "h.trangThai AS trangThai " +
            "FROM HoaDon h " +
            "LEFT JOIN NhanVien nv ON h.idNhanVien = nv.id " +
            "LEFT JOIN KhachHang kh ON h.idKhachHang = kh.id " +
            "LEFT JOIN KhuyenMai km ON h.idKhuyenMai = km.id " +
            "WHERE h.loaiHoaDon = true")
    Page<HoaDonOnlCustom> findCustomHoaDonOnline(Pageable pageable);

}

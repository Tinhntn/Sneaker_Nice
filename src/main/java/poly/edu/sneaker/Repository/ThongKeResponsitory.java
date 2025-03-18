package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.sneaker.Model.HoaDon;


import java.util.Date;

public interface ThongKeResponsitory extends JpaRepository<HoaDon, Integer> {
    // Lấy tổng doanh thu (tongTien) của các hóa đơn có ngày tạo nằm trong khoảng thời gian (startDate đến endDate)
    // và trạng thái hóa đơn (trangThai) bằng 1 (đã thanh toán).
    @Query("SELECT COALESCE(SUM(h.tongTien), 0) " +
            "FROM HoaDon h " +
            "WHERE h.ngayTao BETWEEN :startDate AND :endDate AND h.trangThai = 1")
    Double getRevenueBetweenDates(@Param("startDate") Date startDate,
                                  @Param("endDate") Date endDate);
    // Đếm tổng số hóa đơn có ngày tạo nằm trong khoảng thời gian (startDate đến endDate).
    @Query("SELECT COUNT(h) " +
            "FROM HoaDon h " +
            "WHERE h.ngayTao BETWEEN :startDate AND :endDate")
    Long getOrderCountBetweenDates(@Param("startDate") Date startDate,
                                   @Param("endDate") Date endDate);
    // Đếm số lượng hóa đơn thành công (loaiHoaDon = true) có ngày tạo nằm trong khoảng thời gian (startDate đến endDate).
    @Query("SELECT COUNT(h) " +
            "FROM HoaDon h " +
            "WHERE h.loaiHoaDon = true AND h.ngayTao BETWEEN :startDate AND :endDate")
    Long getSuccessfulOrderCountBetweenDates(@Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);
    // Đếm số lượng hóa đơn bị hủy (loaiHoaDon = false) có ngày tạo nằm trong khoảng thời gian (startDate đến endDate).
    @Query("SELECT COUNT(h) " +
            "FROM HoaDon h " +
            "WHERE h.loaiHoaDon = false AND h.ngayTao BETWEEN :startDate AND :endDate")
    Long getCancelledOrderCountBetweenDates(@Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate);



}

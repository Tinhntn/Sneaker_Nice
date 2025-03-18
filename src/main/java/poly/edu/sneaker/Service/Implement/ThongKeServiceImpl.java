package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.DAO.SanPhamBanChayResponse;
import poly.edu.sneaker.Repository.HoaDonChiTietOnlRepository;
import poly.edu.sneaker.Repository.ThongKeResponsitory;
import poly.edu.sneaker.Service.ThongKeService;
import poly.edu.sneaker.DAO.ThongKeDTO;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    @Autowired
    private ThongKeResponsitory thongKeResponsitory;
    @Autowired
    private HoaDonChiTietOnlRepository hoaDonChiTietOnlRepository;

    @Override
    public Map<String, Object> getDefaultThongKe() {
        Map<String, Object> stats = new HashMap<>();
        Date now = new Date();

        // Tính thời gian cho "Hôm nay"
        Date todayStart = getStartOfDay(now);
        Date todayEnd = getEndOfDay(now);
        Double doanhThuNgay = thongKeResponsitory.getRevenueBetweenDates(todayStart, todayEnd);
        Long orderSuccessNgay = thongKeResponsitory.getSuccessfulOrderCountBetweenDates(todayStart, todayEnd);
        Long orderCancelNgay = thongKeResponsitory.getCancelledOrderCountBetweenDates(todayStart, todayEnd);

// Thêm truy vấn tính tổng số lượng sản phẩm bán ra từ HoaDonChiTiet
        Long totalProductsToday = hoaDonChiTietOnlRepository.getTotalProductSoldBetweenDates(todayStart, todayEnd);
        int soSanPhamNgay = totalProductsToday != null ? totalProductsToday.intValue() : 0;
        ;

        // Tạo DTO cho "Hôm nay"
        ThongKeDTO tkNgay = new ThongKeDTO(doanhThuNgay, soSanPhamNgay,
                orderSuccessNgay.intValue(), orderCancelNgay.intValue());
        stats.put("doanhThuTrongNgay", tkNgay.getDoanhThu());
        stats.put("soLuongSanPhamTrongNgay", tkNgay.getSoSanPham());
        stats.put("soLuongHoaDonThanhCongTrongNgay", tkNgay.getSoHoaDonThanhCong());
        stats.put("soluongHoaDonHuyTrongNgay", tkNgay.getSoHoaDonHuy());


        Date weekStart = getStartOfWeek(now);
        Date weekEnd = getEndOfWeek(now);
        Double doanhThuTuan = thongKeResponsitory.getRevenueBetweenDates(weekStart, weekEnd);
        Long orderSuccessTuan = thongKeResponsitory.getSuccessfulOrderCountBetweenDates(weekStart, weekEnd);
        Long orderCancelTuan = thongKeResponsitory.getCancelledOrderCountBetweenDates(weekStart, weekEnd);
// Tính tổng số sản phẩm bán ra trong tuần
        Long totalProductsWeek = hoaDonChiTietOnlRepository.getTotalProductSoldBetweenDates(weekStart, weekEnd);
        int soSanPhamTuan = totalProductsWeek != null ? totalProductsWeek.intValue() : 0;
        ThongKeDTO tkTuan = new ThongKeDTO(doanhThuTuan, soSanPhamTuan,
                orderSuccessTuan.intValue(), orderCancelTuan.intValue());
        stats.put("doanhThuTrongTuan", tkTuan.getDoanhThu());
        stats.put("soLuongSanPhamTrongTuan", tkTuan.getSoSanPham());
        stats.put("soLuongHoaDonThanhCongTrongTuan", tkTuan.getSoHoaDonThanhCong());
        stats.put("soluongHoaDonHuyTrongTuan", tkTuan.getSoHoaDonHuy());
////thang
        Date monthStart = getStartOfMonth(now);
        Date monthEnd = getEndOfMonth(now);
        Double doanhThuThang = thongKeResponsitory.getRevenueBetweenDates(monthStart, monthEnd);
        Long orderSuccessThang = thongKeResponsitory.getSuccessfulOrderCountBetweenDates(monthStart, monthEnd);
        Long orderCancelThang = thongKeResponsitory.getCancelledOrderCountBetweenDates(monthStart, monthEnd);
// Tính tổng số sản phẩm bán ra trong tháng
        Long totalProductsMonth = hoaDonChiTietOnlRepository.getTotalProductSoldBetweenDates(monthStart, monthEnd);
        int soSanPhamThang = totalProductsMonth != null ? totalProductsMonth.intValue() : 0;
        ThongKeDTO tkThang = new ThongKeDTO(doanhThuThang, soSanPhamThang,
                orderSuccessThang.intValue(), orderCancelThang.intValue());
        stats.put("doanhThuTrongThang", tkThang.getDoanhThu());
        stats.put("soLuongSanPhamTrongThang", tkThang.getSoSanPham());
        stats.put("soLuongHoaDonThanhCongTrongThang", tkThang.getSoHoaDonThanhCong());
        stats.put("soluongHoaDonHuyTrongThang", tkThang.getSoHoaDonHuy());
             //năm
        Date yearStart = getStartOfYear(now);
        Date yearEnd = getEndOfYear(now);
        Double doanhThuNam = thongKeResponsitory.getRevenueBetweenDates(yearStart, yearEnd);
        Long orderSuccessNam = thongKeResponsitory.getSuccessfulOrderCountBetweenDates(yearStart, yearEnd);
        Long orderCancelNam = thongKeResponsitory.getCancelledOrderCountBetweenDates(yearStart, yearEnd);
// Tính tổng số sản phẩm bán ra trong năm
        Long totalProductsYear = hoaDonChiTietOnlRepository.getTotalProductSoldBetweenDates(yearStart, yearEnd);
        int soSanPhamNam = totalProductsYear != null ? totalProductsYear.intValue() : 0;
        ThongKeDTO tkNam = new ThongKeDTO(doanhThuNam, soSanPhamNam,
                orderSuccessNam.intValue(), orderCancelNam.intValue());
        stats.put("doanhThuTrongNam", tkNam.getDoanhThu());
        stats.put("soLuongSanPhamTrongNam", tkNam.getSoSanPham());
        stats.put("soLuongHoaDonThanhCongTrongNam", tkNam.getSoHoaDonThanhCong());
        stats.put("soluongHoaDonHuyTrongNam", tkNam.getSoHoaDonHuy());

        // Dữ liệu cho biểu đồ đơn hàng: giả sử trả về Map với các trạng thái
        Map<String, Integer> hd = new HashMap<>();
        hd.put("trangThai_0", 0);
        hd.put("trangThai_1", 0);
        hd.put("trangThai_2", 0);
        hd.put("trangThai_3", 0);
        hd.put("trangThai_4", 0);
        hd.put("trangThai_5", 0);
        hd.put("trangThai_6", 0);
        stats.put("hd", hd);

        List<SanPhamBanChayResponse> listSanPhamBanChay = hoaDonChiTietOnlRepository.findTop5SanPhamBanChay();
        if (listSanPhamBanChay == null) {
            listSanPhamBanChay = new ArrayList<>();
        }
        stats.put("lstSanPhamBanChay", listSanPhamBanChay);


        return stats;
    }

    @Override
    public List<SanPhamBanChayResponse> getTop5SanPhamBanChay() {
        return hoaDonChiTietOnlRepository.findTop5SanPhamBanChay();
    }


    @Override
    public Map<String, Object> getThongKeTheoLoai(int loaiLoc) {
        // Tùy vào loaiLoc: 0 - ngày, 1 - tuần, 2 - tháng, 3 - năm
        Date now = new Date();
        Date start, end;
        switch (loaiLoc) {
            case 0:
                start = getStartOfDay(now);
                end = getEndOfDay(now);
                break;
            case 1:
                start = getStartOfWeek(now);
                end = getEndOfWeek(now);
                break;
            case 2:
                start = getStartOfMonth(now);
                end = getEndOfMonth(now);
                break;
            case 3:
                start = getStartOfYear(now);
                end = getEndOfYear(now);
                break;
            default:
                start = getStartOfDay(now);
                end = getEndOfDay(now);
        }
        return getStatisticsBetweenDates(start, end);
    }

    @Override
    public Map<String, Object> getThongKeTheoKhoangNgay(String startDateStr, String endDateStr) {
        Map<String, Object> stats = new HashMap<>();
        // Kiểm tra nếu chuỗi rỗng hoặc null, bạn có thể trả về lỗi hoặc giá trị mặc định
        if (startDateStr == null || startDateStr.trim().isEmpty() ||
                endDateStr == null || endDateStr.trim().isEmpty()) {
            // Ví dụ: trả về map rỗng, hoặc bạn có thể thêm thông báo lỗi
            stats.put("error", "Ngày bắt đầu và ngày kết thúc không được để trống!");
            return stats;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDateStr);
            Date end = sdf.parse(endDateStr);
            return getStatisticsBetweenDates(start, end);
        } catch (Exception e) {
            e.printStackTrace();
            stats.put("error", "Lỗi phân tích ngày: " + e.getMessage());
            return stats;
        }
    }


    private Map<String, Object> getStatisticsBetweenDates(Date start, Date end) {
        Map<String, Object> stats = new HashMap<>();
        Double doanhThu = thongKeResponsitory.getRevenueBetweenDates(start, end);
        Long soDonHang = thongKeResponsitory.getOrderCountBetweenDates(start, end);
        Long soDonHangThanhCong = thongKeResponsitory.getSuccessfulOrderCountBetweenDates(start, end);
        Long soDonHangHuy = thongKeResponsitory.getCancelledOrderCountBetweenDates(start, end);
        // Số sản phẩm bán ra có thể tính thêm nếu có truy vấn
        Integer soSanPham = 0;

        ThongKeDTO tk = new ThongKeDTO(doanhThu, soSanPham, soDonHangThanhCong.intValue(), soDonHangHuy.intValue());
        stats.put("doanhThu", tk.getDoanhThu());
        stats.put("soLuongSanPham", tk.getSoSanPham());
        stats.put("soLuongHoaDonThanhCong", tk.getSoHoaDonThanhCong());
        stats.put("soluongHoaDonHuy", tk.getSoHoaDonHuy());

        // Dữ liệu cho biểu đồ
        Map<String, Integer> hd = new HashMap<>();
        hd.put("trangThai_0", 0);
        hd.put("trangThai_1", 0);
        hd.put("trangThai_2", 0);
        hd.put("trangThai_3", 0);
        hd.put("trangThai_4", 0);
        hd.put("trangThai_5", 0);
        hd.put("trangThai_6", 0);
        stats.put("hd", hd);

        List<SanPhamBanChayResponse> listSanPhamBanChay = hoaDonChiTietOnlRepository.findTop5SanPhamBanChay();
        if (listSanPhamBanChay == null) {
            listSanPhamBanChay = new ArrayList<>();
        }
        stats.put("lstSanPhamBanChay", listSanPhamBanChay);

        return stats;
    }

    // --- Các phương thức tiện ích để tính khoảng thời gian ---
    private Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private Date getStartOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return getStartOfDay(cal.getTime());
    }

    private Date getEndOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartOfWeek(date));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        return getEndOfDay(cal.getTime());
    }

    private Date getStartOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(cal.getTime());
    }

    private Date getEndOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getEndOfDay(cal.getTime());
    }

    private Date getStartOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(cal.getTime());
    }

    private Date getEndOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        return getEndOfDay(cal.getTime());
    }

}

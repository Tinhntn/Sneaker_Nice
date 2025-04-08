package poly.edu.sneaker.Service;

import poly.edu.sneaker.DAO.SanPhamBanChayResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ThongKeService {
    Map<String, Object> getDefaultThongKe();

    List<SanPhamBanChayResponse> getTop5SanPhamBanChay(Date startDate, Date endDate, Long loaiSanPham);

    Map<String, Object> getThongKeTheoLoai(int loaiLoc);

    Map<String, Object> getThongKeTheoKhoangNgay(String startDateStr, String endDateStr);

}
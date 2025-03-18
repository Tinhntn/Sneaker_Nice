package poly.edu.sneaker.Service;

import poly.edu.sneaker.DAO.SanPhamBanChayResponse;

import java.util.List;
import java.util.Map;

public interface ThongKeService {
    Map<String, Object> getDefaultThongKe();
    Map<String, Object> getThongKeTheoLoai(int loaiLoc);
    Map<String, Object> getThongKeTheoKhoangNgay(String startDate, String endDate);
    List<SanPhamBanChayResponse> getTop5SanPhamBanChay();

}

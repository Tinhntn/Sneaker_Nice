package poly.edu.sneaker.Service;

import poly.edu.sneaker.Response.GiaoHangTietKiemResponse;

public interface GiaoHangService {
    GiaoHangTietKiemResponse getGiaoHangTietKiem(String pickProvince, String pickDistrict,
                                                 String province, String district, int weight);
}

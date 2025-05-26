package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;
import poly.edu.sneaker.Model.KhuyenMai;
import poly.edu.sneaker.Repository.HoaDonChiTietRepository;
import poly.edu.sneaker.Repository.HoaDonRepository;
import poly.edu.sneaker.Repository.KhuyenMaiRepository;
import poly.edu.sneaker.Service.HoaDonService;

import java.util.List;
import java.util.Random;

@Service

public class HoaDonImplement implements HoaDonService {
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    KhuyenMaiRepository khuyenMaiRepository;
    @Override
    public Page<HoaDon> findAll(Pageable pageable) {
        return hoaDonRepository.findAll(pageable);
    }

    @Override
    public HoaDon findById(int id) {
        return hoaDonRepository.findById(id).get();
    }

    @Override
    public HoaDon save(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public void update(HoaDon hoaDon) {
        hoaDonRepository.save(hoaDon);
    }

    @Override
    public void deleteById(int id) {
        hoaDonRepository.deleteById(id);
    }

    @Override
    public Page<HoaDon> getAllHoaDonTaiQuay(int page, int size) {
        return hoaDonRepository.getAllHoaDon(PageRequest.of(page,size));
    }

    @Override
    public List<HoaDonChiTiet> danhSachChiTietHoaDonByIDHD(Integer id) {
        return hoaDonChiTietRepository.findHoaDonChiTietByIdHoaDon(id);
    }

    @Override
    public String taoMaHoaDon() {
        Random random = new Random();
        String maHoaDon = "HD"+1000+random.nextInt(9000);
        for ( HoaDon hoaDon : hoaDonRepository.findAll()
             ) {
            if(hoaDon.getMaHoaDon().equals(maHoaDon)){
                maHoaDon = "HD"+1000+random.nextInt(9000);            }

        }
        return maHoaDon;
    }

    @Override
    public Page<HoaDon> timkiemhoadon(String keyword,int page, int size) {
        return hoaDonRepository.searchByMaHoaDonTenKhachHangOrSdt(keyword,PageRequest.of(page,size));
    }

    @Override
    public Page<HoaDon> searchHoaDonByDateRange(String startDate, String endDate, int page, int size) {
        return hoaDonRepository.findByNgayTaoBetween(startDate,endDate,PageRequest.of(page,size));
    }

    @Override
    public Page<HoaDon> timHoaDonTheoIdKhuyenMai(Integer idkm, int page, int size) {
        return hoaDonRepository.timHoaDonTheoIdKhuyenMai(idkm,PageRequest.of(page,size));
    }

    @Override
    public void tinhLaiKhuyenMai(HoaDon hoaDon) {
        //Tinh lại phieu giam gia khi khach hang thay doi so luong san pham
        float tongTienGiamMoi = 0;
        KhuyenMai khuyenMaiOld = hoaDon.getIdKhuyenMai();
        KhuyenMai khuyenMaiTotNhat = null;

        List<KhuyenMai> lstKhuyenMai = khuyenMaiRepository.findAll();

        for (KhuyenMai km : lstKhuyenMai) {
            if (!km.getTrangThai() || km.getDieuKienApDung() > hoaDon.getTongTien()) continue;
            float tienGiam;
            if (km.getLoaiKhuyenMai()) {
                tienGiam = km.getGiaTriGiam();
            } else {
                tienGiam = Math.min(km.getMucGiamGiaToiDa(), (hoaDon.getTongTien() * km.getGiaTriGiam() / 100));
            }
            if (tienGiam > tongTienGiamMoi) {
                tongTienGiamMoi = tienGiam;
                khuyenMaiTotNhat = km;
            }
        }

// Xử lý cập nhật hóa đơn và trạng thái mã khuyến mãi
        if (khuyenMaiTotNhat != null) {
            hoaDon.setTongTienGiam(tongTienGiamMoi);
            hoaDon.setIdKhuyenMai(khuyenMaiTotNhat);

            if (khuyenMaiOld == null || khuyenMaiOld.getId() != khuyenMaiTotNhat.getId()) {
                // Tăng lượt dùng mã mới
                khuyenMaiTotNhat.setDaSuDung(khuyenMaiTotNhat.getDaSuDung() + 1);
                khuyenMaiRepository.save(khuyenMaiTotNhat);

                // Giảm lượt dùng mã cũ nếu có
                if (khuyenMaiOld != null) {
                    khuyenMaiOld.setDaSuDung(khuyenMaiOld.getDaSuDung() - 1);
                    khuyenMaiRepository.save(khuyenMaiOld);
                }
            }
        } else {
            // Không có mã hợp lệ nào
            hoaDon.setIdKhuyenMai(null);
            hoaDon.setTongTienGiam(0);
            if (khuyenMaiOld != null) {
                khuyenMaiOld.setDaSuDung(khuyenMaiOld.getDaSuDung() - 1);
                khuyenMaiRepository.save(khuyenMaiOld);
            }
        }
        hoaDonRepository.save(hoaDon);
    }

    //code quan

}

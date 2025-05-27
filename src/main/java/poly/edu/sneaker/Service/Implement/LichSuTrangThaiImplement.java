package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.*;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Repository.HoaDonChiTietRepository;
import poly.edu.sneaker.Repository.KhuyenMaiRepository;
import poly.edu.sneaker.Repository.LichSuTrangThaiRepository;
import poly.edu.sneaker.Service.*;

import java.util.*;

@Service

public class LichSuTrangThaiImplement implements LichSuTrnngThaiService {
    @Autowired
    private LichSuTrangThaiRepository lichSuTrangThaiRepository;
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @Override
    public Page<TrangThaiDonHang> findAllLichSuTrangThai(Pageable pageable) {
        return lichSuTrangThaiRepository.findAll(pageable);
    }

    @Override
    public void saveLichSuTrangThai(TrangThaiDonHang lt) {
        lichSuTrangThaiRepository.save(lt);
    }

    @Override
    public void deleteLichSuTrangThai(TrangThaiDonHang lt) {
        lichSuTrangThaiRepository.delete(lt);

    }

    @Override
    public TrangThaiDonHang findLichSuTrangThaiById(Integer id) {
        return lichSuTrangThaiRepository.findById(id).get();
    }

    @Override
    public void updateLichSuTrangThai(TrangThaiDonHang lt) {
        lichSuTrangThaiRepository.save(lt);
    }

    @Override
    public List<TrangThaiDonHang> getAllByIdHoaDon(int idHD) {
        return lichSuTrangThaiRepository.findAllByIdHoaDon_Id(idHD);
    }

    @Override
    public boolean doiTrangThaiDonHang(int idHD, String ghiChu, int trangThaiMoi, boolean isUndo) {
        try {
            HoaDon hoaDon = hoaDonService.findById(idHD);
            if (hoaDon == null) {
                return false;
            }

            int trangThaiHienTai = hoaDon.getTrangThai();
            List<HoaDonChiTiet> lstHoaDonChiTiet = hoaDonChiTietRepository
                    .findHoaDonChiTietByIdHoaDon_IdAndIdHoaDon_LoaiHoaDon(idHD, true);

            // 1. Xử lý thay đổi số lượng tồn kho
            xuLyThayDoiSoLuongKho(hoaDon, lstHoaDonChiTiet, trangThaiHienTai, trangThaiMoi, isUndo);

            // 2. Xử lý khuyến mãi
            xuLyKhuyenMai(hoaDon, trangThaiHienTai, trangThaiMoi, isUndo);

            // 3. Ghi lịch sử trạng thái
            luuLichSuTrangThai(hoaDon, trangThaiMoi, isUndo ? "[HOÀN TÁC] " + ghiChu : ghiChu);

            // 4. Cập nhật trạng thái mới
            hoaDon.setTrangThai(trangThaiMoi);
            hoaDon.setNgaySua(new Date());
            hoaDonService.save(hoaDon);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void xuLyThayDoiSoLuongKho(HoaDon hoaDon, List<HoaDonChiTiet> lstHoaDonChiTiet,
                                       int trangThaiHienTai, int trangThaiMoi, boolean isUndo) {
        // Danh sách trạng thái đã trừ kho
        Set<Integer> daTruKhoStates = Set.of(3, 4, 5); // Chờ lấy hàng, Đang giao, Đã giao

        boolean isDaTruKho = daTruKhoStates.contains(trangThaiHienTai);
        boolean isMoiTruKho = daTruKhoStates.contains(trangThaiMoi);

        // Trường hợp hủy đơn hoặc giao thất bại: trả hàng vào kho
        if ((trangThaiMoi == 6 || trangThaiMoi == 11) && isDaTruKho) {
            capNhatSoLuongKho(lstHoaDonChiTiet, true);
        }
        // Trường hợp hoàn tác từ hủy/giao thất bại về trạng thái đã trừ kho
        else if (isUndo && (trangThaiHienTai == 6 || trangThaiHienTai == 11) && isMoiTruKho) {
            capNhatSoLuongKho(lstHoaDonChiTiet, false);
        }
        // Trường hợp chuyển sang trạng thái cần trừ kho
        else if (trangThaiMoi == 3 && !isDaTruKho && !isUndo) {
            capNhatSoLuongKho(lstHoaDonChiTiet, false);
        }
    }

    private void xuLyKhuyenMai(HoaDon hoaDon, int trangThaiHienTai, int trangThaiMoi, boolean isUndo) {
        if (hoaDon.getIdKhuyenMai() == null) return;

        KhuyenMai khuyenMai = hoaDon.getIdKhuyenMai();
        int thayDoi = 0;

        // Logic tăng/giảm số lượt sử dụng khuyến mãi
        if ((trangThaiMoi == 6 || trangThaiMoi == 11) && !isUndo) {
            thayDoi = -1; // Hủy đơn/giao thất bại: giảm lượt dùng
        }
        else if (isUndo && (trangThaiHienTai == 6 || trangThaiHienTai == 11)) {
            thayDoi = 1; // Hoàn tác từ hủy: tăng lượt dùng
        }

        if (thayDoi != 0) {
            khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() + thayDoi);
            khuyenMai.setNgaySua(new Date());
            khuyenMaiRepository.save(khuyenMai);
        }
    }

    private void capNhatSoLuongKho(List<HoaDonChiTiet> lstHoaDonChiTiet, boolean isTraHang) {
        for (HoaDonChiTiet hdct : lstHoaDonChiTiet) {
            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(hdct.getIdChiTietSanPham().getId());
            int soLuongThayDoi = hdct.getSoLuong();
            ctsp.setSoLuong(isTraHang
                    ? ctsp.getSoLuong() + soLuongThayDoi
                    : ctsp.getSoLuong() - soLuongThayDoi);
            ctsp.setNgaySua(new Date());
            chiTietSanPhamRepository.save(ctsp);
        }
    }

    private void xuLyKhuyenMai(HoaDon hoaDon, boolean isUndo, int trangThaiMoi) {
        if (hoaDon.getIdKhuyenMai() == null) {
            return;
        }
        KhuyenMai khuyenMai = hoaDon.getIdKhuyenMai();
        //nếu đang ở trạng thái này thì số lượng sp đã được trừ r không trừ lại
        boolean checkCoTruSoLuongSanPham = hoaDon.getTrangThai()==3||hoaDon.getTrangThai()==4||hoaDon.getTrangThai()==5;
        //nếu isUndo thì là quay lại trạng thái
        if(isUndo){
            //nếu không nằm trong các trạng thái đã trừ số lượng tiến hành công số lượng
            if(!checkCoTruSoLuongSanPham){
                if(trangThaiMoi!=hoaDon.getTrangThai()){
                    if ((trangThaiMoi == 6 || trangThaiMoi == 11) && khuyenMai.getDaSuDung() > 0){
                        khuyenMai.setDaSuDung(khuyenMai.getDaSuDung()-1);
                    }
                }
            }else{
                khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() + 1);
            }
            if (khuyenMai.getDaSuDung() != hoaDon.getIdKhuyenMai().getDaSuDung()) {
                khuyenMai.setNgaySua(new Date());
                khuyenMaiRepository.save(khuyenMai);
            }
        }else{
            if((trangThaiMoi==6&&hoaDon.getTrangThai()==3)||trangThaiMoi==11){
                khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() -1);
                khuyenMai.setNgaySua(new Date());
            }
            khuyenMaiRepository.save(khuyenMai);
        }
    }


    private void luuLichSuTrangThai(HoaDon hoaDon, int trangThaiMoi, String ghiChu) {
        TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
        lichSuTrangThai.setIdHoaDon(hoaDon);
        lichSuTrangThai.setTrangThai(trangThaiMoi);
        lichSuTrangThai.setNgayCapNhat(new Date());
        lichSuTrangThai.setGhiChu(ghiChu);
        lichSuTrangThaiRepository.save(lichSuTrangThai);
    }

    @Override
    public TrangThaiDonHang layTrangThaiTruocDo(int idHoaDon, int trangThaiTruoc) {
        return lichSuTrangThaiRepository.findByIdHoaDon_IdAndTrangThai(idHoaDon,trangThaiTruoc);
    }

    @Override
    public List<TrangThaiDonHang> findByIdHoaDon_IdOrderByNgayCapNhatDesc(int idHoaDon) {
        return lichSuTrangThaiRepository.findByIdHoaDon_IdOrderByNgayCapNhatDesc(idHoaDon);
    }
}

package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.TrangThaiDonHang;
import poly.edu.sneaker.Repository.LichSuTrangThaiRepository;
import poly.edu.sneaker.Service.HoaDonService;
import poly.edu.sneaker.Service.LichSuTrnngThaiService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service

public class LichSuTrangThaiImplement implements LichSuTrnngThaiService {
    @Autowired
    private LichSuTrangThaiRepository lichSuTrangThaiRepository;
    @Autowired
    HoaDonService hoaDonService;
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
    public void doiTrangThaiDonHang(int idHD, String ghiChu, int trangThai) {
        HoaDon hoaDon = hoaDonService.findById(idHD);
        if(trangThai==0){
            //Hủy hóa đơn
            hoaDon.setTrangThai(0);
            hoaDonService.save(hoaDon);
            TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
            lichSuTrangThai.setIdHoaDon(hoaDon);
            lichSuTrangThai.setTrangThai(hoaDon.getTrangThai());
            lichSuTrangThai.setNgayCapNhat(new Date());
            lichSuTrangThai.setGhiChu(ghiChu);
            lichSuTrangThaiRepository.save(lichSuTrangThai);
            return;
        }else if(trangThai==1){
            hoaDon.setTrangThai(1);
            hoaDonService.save(hoaDon);
            TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
            lichSuTrangThai.setIdHoaDon(hoaDon);
            lichSuTrangThai.setTrangThai(hoaDon.getTrangThai());
            lichSuTrangThai.setNgayCapNhat(new Date());
            lichSuTrangThai.setGhiChu(ghiChu);
            lichSuTrangThaiRepository.save(lichSuTrangThai);
            return;
        }else if(trangThai==2){
            hoaDon.setTrangThai(2);
            hoaDonService.save(hoaDon);
            TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
            lichSuTrangThai.setIdHoaDon(hoaDon);
            lichSuTrangThai.setTrangThai(hoaDon.getTrangThai());
            lichSuTrangThai.setNgayCapNhat(new Date());
            lichSuTrangThai.setGhiChu(ghiChu);
            lichSuTrangThaiRepository.save(lichSuTrangThai);
            return;
        }else if(trangThai==3){
            hoaDon.setTrangThai(3);
            hoaDonService.save(hoaDon);
            TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
            lichSuTrangThai.setIdHoaDon(hoaDon);
            lichSuTrangThai.setTrangThai(hoaDon.getTrangThai());
            lichSuTrangThai.setNgayCapNhat(new Date());
            lichSuTrangThai.setGhiChu(ghiChu);
            lichSuTrangThaiRepository.save(lichSuTrangThai);
            return;
        }else if(trangThai==4){
            hoaDon.setTrangThai(4);
            hoaDonService.save(hoaDon);
            TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
            lichSuTrangThai.setIdHoaDon(hoaDon);
            lichSuTrangThai.setTrangThai(hoaDon.getTrangThai());
            lichSuTrangThai.setNgayCapNhat(new Date());
            lichSuTrangThai.setGhiChu(ghiChu);
            lichSuTrangThaiRepository.save(lichSuTrangThai);
            return;
        }else if(trangThai==5){
            hoaDon.setTrangThai(5);
            hoaDonService.save(hoaDon);
            TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
            lichSuTrangThai.setIdHoaDon(hoaDon);
            lichSuTrangThai.setTrangThai(hoaDon.getTrangThai());
            lichSuTrangThai.setNgayCapNhat(new Date());
            lichSuTrangThai.setGhiChu(ghiChu);
            lichSuTrangThaiRepository.save(lichSuTrangThai);
            return;
        }else if(trangThai ==6){
            hoaDon.setTrangThai(6);
            hoaDonService.save(hoaDon);
            TrangThaiDonHang lichSuTrangThai = new TrangThaiDonHang();
            lichSuTrangThai.setIdHoaDon(hoaDon);
            lichSuTrangThai.setTrangThai(hoaDon.getTrangThai());
            lichSuTrangThai.setNgayCapNhat(new Date());
            lichSuTrangThai.setGhiChu(ghiChu);
            lichSuTrangThaiRepository.save(lichSuTrangThai);
            return;
        }
    }
}

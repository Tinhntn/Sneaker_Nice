package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.ChiTietSanPham;
import poly.edu.sneaker.Repository.ChiTietSanPhamRepository;
import poly.edu.sneaker.Service.ChiTietSanPhamService;

import java.util.ArrayList;

@Service

public class ChiTietSanPhamImplement implements ChiTietSanPhamService {
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Override
    public Page<ChiTietSanPham> findAll(Pageable pageable) {

        return chiTietSanPhamRepository.findAll(pageable);
    }

    @Override
    public void saveChiTietSanPham(ChiTietSanPham chiTietSanPham) {
        chiTietSanPhamRepository.save(chiTietSanPham);
    }

    @Override
    public ChiTietSanPham findById(int id) {
        return chiTietSanPhamRepository.findById(id).get();
    }

    @Override
    public void deleteChiTietSanPham(int id) {
        chiTietSanPhamRepository.deleteById(id);
    }

    @Override
    public void update(ChiTietSanPham chiTietSanPham) {
        chiTietSanPhamRepository.save(chiTietSanPham);
    }

    @Override
    public Page<ChiTietSanPham> findChiTietSanPhamByIDSanPham(int idSanPham, Pageable pageable) {
        return chiTietSanPhamRepository.findChiTietSanPhamByIdSanPham_Id(idSanPham,pageable);
    }

    @Override
    public Page<ChiTietSanPham> findChiTietSanPhamJustOne(Pageable pageable) {
        return chiTietSanPhamRepository.findFirstRecordForEachProduct(pageable);
    }

    @Override
    public ChiTietSanPham findCTSPByIDMauSac(int idCTSP, int idMauSac) {
        return chiTietSanPhamRepository.findChiTietSanPhamByIdAndIdMauSacAndTrangThai(idCTSP,idMauSac,true);
    }

    @Override
    public ArrayList<ChiTietSanPham> findByIdSanPham(int idSanPham) {
        return chiTietSanPhamRepository.findByIdSanPham_IdAndTrangThai(idSanPham,true);
    }

    @Override
    public ChiTietSanPham findCTSPByIdSPAndIdMauSacAndIdSize(int idSanPham, int idMauSac, int idSize) {
        return chiTietSanPhamRepository.findChiTietSanPhamByIdSanPham_IdAndIdSize_IdAndIdMauSac_Id(idSanPham,idMauSac,idSize);
    }
//
//    @Override
//    public ChiTietSanPham getCTSPByIdSP(Pageable pageable, int idSP) {
//        return chiTietSanPhamRepository.findChiTietSanPhamByIdSanPham(idSP);
//    }
}

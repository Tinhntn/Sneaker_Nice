package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.Model.ChiTietSanPham;

import java.util.ArrayList;

public interface ChiTietSanPhamService {
    Page<ChiTietSanPham> findAll(Pageable pageable);
    void saveChiTietSanPham(ChiTietSanPham chiTietSanPham);
    ChiTietSanPham findById(int id);
    void deleteChiTietSanPham(int id);
    void update(ChiTietSanPham chiTietSanPham);
    Page<ChiTietSanPham> findChiTietSanPhamByIDSanPham(int idSanPham, Pageable pageable);
//    ChiTietSanPham getCTSPByIdSP(Pageable pageable, int idSP);
    Page<ChiTietSanPham> findChiTietSanPhamJustOne(Pageable pageable);
    ChiTietSanPham findCTSPByIDMauSac(int idCTSP, int idMauSac);
    ArrayList<ChiTietSanPham> findByIdSanPham(int idSanPham);
    ChiTietSanPham findCTSPByIdSPAndIdMauSacAndIdSize(int idSanPham, int idMauSac, int idSize);
}

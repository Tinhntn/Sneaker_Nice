package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Repository.KhachHangRepository;
import poly.edu.sneaker.Service.KhachHangService;

import java.util.ArrayList;
import java.util.Random;

@Service
public class KhachHangImplement implements KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;


    @Override
    public Page<KhachHang> getAll(Pageable pageable) {
        return khachHangRepository.findAll(pageable);
    }

    @Override
    public KhachHang findKhachHangById(int id) {
        return khachHangRepository.findById(id).orElseThrow(() -> new RuntimeException("KhachHang not found"));
    }

    @Override
    public void saveKhachHang(KhachHang khachHang) {
        System.out.println(khachHang.getMaKhachHang());
        khachHangRepository.save(khachHang);
    }

    @Override
    public void updateKhachHang(KhachHang khachHang, int id) {
        KhachHang existingKhachHang = khachHangRepository.findById(id).orElseThrow(() -> new RuntimeException("KhachHang not found"));
        existingKhachHang.setMaKhachHang(khachHang.getMaKhachHang());
        existingKhachHang.setTenKhachHang(khachHang.getTenKhachHang());
        existingKhachHang.setTinhThanhPho(khachHang.getTinhThanhPho());
        existingKhachHang.setQuanHuyen(khachHang.getQuanHuyen());
        existingKhachHang.setPhuongXa(khachHang.getPhuongXa());
        existingKhachHang.setGioiTinh(khachHang.getGioiTinh());
        existingKhachHang.setEmail(khachHang.getEmail());
        existingKhachHang.setSdt(khachHang.getSdt());
        existingKhachHang.setNgaySinh(khachHang.getNgaySinh());
        existingKhachHang.setNgayTao(khachHang.getNgayTao());
        existingKhachHang.setNgaySua(khachHang.getNgaySua());
        existingKhachHang.setMatKhau(khachHang.getMatKhau());
        existingKhachHang.setHinhAnh(khachHang.getHinhAnh());
        existingKhachHang.setTrangThai(khachHang.getTrangThai());
        khachHangRepository.save(existingKhachHang);
    }

    @Override
    public void deleteById(int id) {
        khachHangRepository.deleteById(id);
    }

    @Override
    public Page<KhachHang> search(String keyword, Pageable pageable) {
        return khachHangRepository.findByMaKhachHangContainingOrTenKhachHangContaining(keyword, keyword, pageable);
    }

    @Override
    public KhachHang findByEmail(String email) {
        return khachHangRepository.findByEmail(email);
    }

    @Override
    public KhachHang findByEmailAndMatKhau(String Email, String matKhau) {
        return khachHangRepository.findByEmailAndMatKhau(Email, matKhau);
    }

    @Override
    public boolean exitsKhachHangByEmail(String email) {
        return khachHangRepository.existsKhachHangByEmail(email);
    }

    @Override
    public String taoMaKhachHang() {
        Random r = new Random();
        String maKhachHang = "KH" + 1000 + r.nextInt(9000);
        ArrayList<KhachHang> lstKhachHang = (ArrayList<KhachHang>) khachHangRepository.findAll();
        for (int i = 0; i < lstKhachHang.size(); i++) {
            if(maKhachHang.equals(lstKhachHang.get(i).getMaKhachHang())){
                maKhachHang = "KH" + 1000 + r.nextInt(9000);
            }
        }
        return maKhachHang;
    }

    @Override
    public boolean layLaiKhachHang(KhachHang khachHang) {
        Random r = new Random();
        String matKhau = String.valueOf(r.nextInt(9000));
        khachHang.setMatKhau(matKhau);
        khachHangRepository.save(khachHang);
        return true;
    }
}
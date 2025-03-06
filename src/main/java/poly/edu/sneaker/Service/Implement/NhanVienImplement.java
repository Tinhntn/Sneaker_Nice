package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.DAO.NhanVienCustom;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Repository.NhanVienRepository;
import poly.edu.sneaker.Service.NhanVienService;

import java.util.List;

@Service
public class NhanVienImplement implements NhanVienService {

    @Autowired
    NhanVienRepository nhanVienRepository;

    @Override
    public Page<NhanVienCustom> getAll(Pageable pageable) {
        return nhanVienRepository.getAll(pageable);
    }

    @Override
    public void saveNhanVien(NhanVien nhanVien) {
        NhanVien existingNhanVien = nhanVienRepository.findByMaNhanVien(nhanVien.getMaNhanVien());
        if (existingNhanVien != null) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại!");
        }
        nhanVienRepository.save(nhanVien);
    }

    @Override
    public void updateNhanVien(NhanVien nhanVien, int id) {
        NhanVien existingNhanVien = nhanVienRepository.findByMaNhanVien(nhanVien.getMaNhanVien());
        if (existingNhanVien != null && existingNhanVien.getId() != (id)) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại!");
        }
        nhanVienRepository.save(nhanVien);
    }

    @Override
    public NhanVien findNhanVienById(int id) {
        return nhanVienRepository.findById(id).get();
    }

    @Override
    public Page<NhanVienCustom> search(String keyword, Pageable pageable) {
        return nhanVienRepository.searchNhanVien(keyword, pageable);
    }

    @Override
    public NhanVien getNhanVienByEmailandMatKhau(String email, String matKhau) {
        return nhanVienRepository.findByEmailAndMatKhau(email,matKhau);
    }

    @Override
    public NhanVien getNhanVienByEmail(String email) {
        return nhanVienRepository.findByEmail(email);
    }

}

package poly.edu.sneaker.Service.Implement;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Repository.NhanVienRepository;
import poly.edu.sneaker.Service.NhanVienService;

import java.util.List;

@Service
public class NhanVienImplement implements NhanVienService {
    @AutoConfigureOrder
    private NhanVienRepository nhanVienRepository;
    @Override
    public List<NhanVien> getAllNhanVien(int page, int size) {

        return nhanVienRepository.findAll(PageRequest.of(page,size)).getContent();
    }
}

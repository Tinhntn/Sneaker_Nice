package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import poly.edu.sneaker.Model.NhanVien;

import java.util.List;

public interface NhanVienService {
    List<NhanVien> getAllNhanVien(int page, int size);
}

package poly.edu.sneaker.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.sneaker.DAO.KhuyenMaiCustom;
import poly.edu.sneaker.Model.KhuyenMai;

public interface KhuyenMaiService {

    Page<KhuyenMaiCustom> getAll(Pageable pageable);
    void addKhuyenMai(KhuyenMai khuyenMai);
    void updateKhuyenMai(KhuyenMai khuyenMai, int id);
    KhuyenMai detailKhuyenMai(int id);
    Page<KhuyenMaiCustom> findKhuyenMaiByMaKhuyenMaiContainingOrTenKhuyenMaiContaining(String maKhuyenMai, String tenKhuyenMai, Pageable pageable);

}

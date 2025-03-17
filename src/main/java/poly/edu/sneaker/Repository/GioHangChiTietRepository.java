package poly.edu.sneaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.sneaker.Model.GioHangChiTiet;

import java.util.ArrayList;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {

    GioHangChiTiet findByIdGioHang_IdAndIdChiTietSanPham_Id(int idGioHang, int idCTSP);
    ArrayList<GioHangChiTiet> findByIdGioHang_Id(int idGioHang);
}

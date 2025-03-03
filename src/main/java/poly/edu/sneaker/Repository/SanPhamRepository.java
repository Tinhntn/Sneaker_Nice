package poly.edu.sneaker.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import poly.edu.sneaker.Model.SanPham;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    Page<SanPham> findByMaSanPhamContainingOrTenSanPhamContaining(String maSanPham, String tenSanPham, Pageable pageable);



}

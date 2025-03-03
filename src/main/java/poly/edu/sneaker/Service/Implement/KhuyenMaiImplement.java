package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.DAO.KhuyenMaiCustom;
import poly.edu.sneaker.Model.KhuyenMai;
import poly.edu.sneaker.Repository.KhuyenMaiRepository;
import poly.edu.sneaker.Service.KhuyenMaiService;

import java.util.*;

@Service
public class KhuyenMaiImplement implements KhuyenMaiService {

    @Autowired
    KhuyenMaiRepository khuyenMaiRepository;

    @Override
    public Page<KhuyenMaiCustom> getAll(Pageable pageable) {
        return khuyenMaiRepository.getAll(pageable);
    }

    @Override
    public void addKhuyenMai(KhuyenMai khuyenMai) {
        KhuyenMai existingKhuyenMai = khuyenMaiRepository.findByMaKhuyenMai(khuyenMai.getMaKhuyenMai());
        if (existingKhuyenMai != null){
            throw new IllegalArgumentException("Mã khuyến mại đã tồn tại");
        }
        khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    public void updateKhuyenMai(KhuyenMai khuyenMai, int id) {
        KhuyenMai existingKhuyenMai = khuyenMaiRepository.findByMaKhuyenMai(khuyenMai.getMaKhuyenMai());
        if (existingKhuyenMai != null && existingKhuyenMai.getId() != (id)) {
            throw new IllegalArgumentException("Mã khuyến mại đã tồn tại!");
        }
        khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    public KhuyenMai detailKhuyenMai(int id) {
        return khuyenMaiRepository.findById(id).orElse(null);
    }

    @Override
    public Page<KhuyenMaiCustom> findKhuyenMaiByMaKhuyenMaiContainingOrTenKhuyenMaiContaining(String maKhuyenMai, String tenKhuyenMai, Pageable pageable) {
        return khuyenMaiRepository.findKhuyenMaiByMaKhuyenMaiContainingOrTenKhuyenMaiContaining(maKhuyenMai, tenKhuyenMai, pageable);
    }

    @Override
    public String taoMaoKhuyenMai() {
        Random random = new Random();
        String maKhuyenMai =String.valueOf("KM"+1000+ random.nextInt(9000));
        return maKhuyenMai;
    }

    @Override
    public ArrayList<KhuyenMai> getAllKhuyenMai() {
        return (ArrayList<KhuyenMai>) khuyenMaiRepository.findAll();
    }


    private void updateTrangThai(List<KhuyenMai> khuyenMaiList) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        for (KhuyenMai km : khuyenMaiList) {
            if (km.getNgayBatDau().before(endOfDay) && km.getNgayKetThuc().after(startOfDay)) {
                km.setTrangThai(true);
            } else {
                km.setTrangThai(false);
            }
        }

        khuyenMaiRepository.saveAll(khuyenMaiList);
    }


}

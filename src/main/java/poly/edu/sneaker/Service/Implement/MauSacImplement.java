package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.MauSac;
import poly.edu.sneaker.Repository.MauSacRepository;
import poly.edu.sneaker.Service.MauSacService;

@Service
public class MauSacImplement implements MauSacService {

    private final MauSacRepository mauSacRepository;

    @Autowired
    public MauSacImplement(MauSacRepository mauSacRepository) {
        this.mauSacRepository = mauSacRepository;
    }

    @Override
    public Page<MauSac> findByMaMauSacContainingOrTenMauSacContaining(String maMauSac, String tenMauSac, Pageable pageable) {
        return mauSacRepository.findByMaMauSacContainingOrTenMauSacContaining(maMauSac, tenMauSac, pageable);
    }

    @Override
    public Page<MauSac> findAll(Pageable pageable) {
        return mauSacRepository.findAll(pageable);
    }

    @Override
    public MauSac findById(Integer id) {
        return mauSacRepository.findById(id).orElse(null);
    }

    @Override
    public void save(MauSac mauSac) {
        mauSacRepository.save(mauSac);
    }

    @Override
    public void update(MauSac mauSac) {
        mauSacRepository.save(mauSac);
    }

    @Override
    public void deleteById(Integer id) {
        mauSacRepository.deleteById(id);
    }
}
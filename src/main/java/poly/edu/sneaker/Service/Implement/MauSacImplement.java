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
    @Autowired
    private MauSacRepository mauSacRepository;
    @Override
    public Page<MauSac> findAll(Pageable pageable) {
        return mauSacRepository.findAll(pageable);
    }

    @Override
    public MauSac save(MauSac mauSac) {
        return mauSacRepository.save(mauSac);
    }

    @Override
    public void delete(MauSac mauSac) {
        mauSacRepository.delete(mauSac);
    }

    @Override
    public MauSac findById(int id) {
        return mauSacRepository.findById(id).get();
    }

    @Override
    public void update(MauSac mauSac) {
        mauSacRepository.save(mauSac);
    }
}

package poly.edu.sneaker.Service.Implement;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import poly.edu.sneaker.Config.NhanVietPhanQuyen;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.NhanVienService;

public class NhanVienPhanQuyenService implements UserDetailsService {
    private final NhanVienService nhanVienService;

    public NhanVienPhanQuyenService(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NhanVien nhanVien = nhanVienService.getNhanVienByEmail(email);
        if (nhanVien == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản: " + email);
        }
        return new NhanVietPhanQuyen(nhanVien);    }


}

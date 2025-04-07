package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.KhachHangService;
import poly.edu.sneaker.Service.NhanVienService;

import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    KhachHangService khachHangService;
    @Autowired
    NhanVienService nhanVienService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NhanVien nhanVien = nhanVienService.getNhanVienByEmail(email);
        if (nhanVien != null) {
            String chucVu =(nhanVien.getIdChucVu()!=null&&("CV001".equals(nhanVien.getIdChucVu().getMaChucVu()))?"ADMIN":"EMPLOYEE");
            return User.builder()
                    .username(nhanVien.getEmail())
                    .password(nhanVien.getMatKhau())
                    .authorities(new SimpleGrantedAuthority(chucVu)) // Gán quyền
                    .build();
        }

        // Tìm kiếm trong bảng khách hàng
        KhachHang khachHang = khachHangService.findByEmail(email);
        if (khachHang != null) {
            return User.builder()
                    .username(khachHang.getEmail())
                    .password(khachHang.getMatKhau())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                    .build();
        }

        throw new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + email);    }
}

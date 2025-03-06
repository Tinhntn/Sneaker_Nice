package poly.edu.sneaker.Config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import poly.edu.sneaker.Model.NhanVien;

import java.util.Collection;
import java.util.List;

public class NhanVietPhanQuyen implements UserDetails {
    private final NhanVien nhanVien;

    public NhanVietPhanQuyen(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  List.of(new SimpleGrantedAuthority(nhanVien.getIdChucVu().getTenChucVu()));
    }

    @Override
    public String getPassword() {
        return nhanVien.getMatKhau();
    }

    @Override
    public String getUsername() {
        return nhanVien.getMaNhanVien();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}


package poly.edu.sneaker.Config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    UserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/api/dang-nhap", "/dang-nhap", "/quen_mat_khau", "/dang-ky", "/dang_ky_moi", "/Sneakers_Nice/**", "/register",
                                "/css/**", "/js/**", "/fonts/**", "/scss/**",
                                "/vendor/**", "/images/**", "/Roboto/**")
                        .permitAll()
                        .requestMatchers("/gio-hang/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/dang-nhap", "/quen_mat_khau", "/dang-ky").permitAll()
                        .requestMatchers("/sanpham/**", "/hoadon/**", "/thongke/**", "/khuyenmai/**", "/nhanvien/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/dang-nhap") // Trang đăng nhập tùy chỉnh
                        .loginProcessingUrl("/dang-nhap") // URL xử lý submit form
                        .successHandler((request, response, authentication) -> {
                            // Trả về JSON khi đăng nhập thành công
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\": \"Đăng nhập thành công\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            // Trả về JSON khi đăng nhập thất bại
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"message\": \"Sai email hoặc mật khẩu\"}");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/dang-nhap?logout=true") // Chuyển hướng sau khi đăng xuất
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // LUÔN tạo session
                );
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(15); // Bạn có thể thay đổi mức độ mã hóa tùy theo yêu cầu
    }




}

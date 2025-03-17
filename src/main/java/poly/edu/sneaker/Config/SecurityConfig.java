package poly.edu.sneaker.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Tắt CSRF nếu không sử dụng
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll() // Cho phép tài nguyên tĩnh
                        .requestMatchers("/hoadononline/update-soluong").permitAll() // Cho phép API cập nhật số lượng
                        .anyRequest().authenticated() // Các trang khác yêu cầu đăng nhập
                )
                .formLogin(withDefaults()) // Cho phép đăng nhập bằng form
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/")); // Cấu hình đăng xuất

        return http.build();
    }
}

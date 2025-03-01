package poly.edu.sneaker.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Tắt CSRF nếu không cần thiết
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/sanpham/updateCTSP/**").permitAll() // Cho phép API cập nhật
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

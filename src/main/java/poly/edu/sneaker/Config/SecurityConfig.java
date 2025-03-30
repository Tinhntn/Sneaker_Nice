
package poly.edu.sneaker.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/dang-nhap","/quen-mat-khau","/dang-ky","/dang-ky-moi/**","/sanpham/hienthi/**","/Sneakers_Nice/**","/gio-hang/**", "/register",
                                "/css/**", "/js/**", "/fonts/**", "/scss/**",
                                "/vendor/**", "/images/**", "/Roboto/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )// Tắt CSRF nếu dùng API
                .sessionManagement(
                        session -> session.maximumSessions(1)
                )
                .formLogin(login -> login
                        .loginPage("/dang-nhap")
                        .loginProcessingUrl("/dang-nhap")
                        .defaultSuccessUrl("/Sneakers_Nice/hienthi", true)
                        .successHandler((request, response, authentication) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"message\": \"Đăng nhập thành công!\", \"redirectUrl\": \"/sanpham/hienthi\"}");
                        })
                        .permitAll()
                )


                .logout(logout -> logout
                        .logoutUrl("/logout") // Định nghĩa đường dẫn logout
                        .logoutSuccessUrl("/") // Sau khi logout thì về trang chủ
                        .permitAll()
                ).csrf(csrf -> csrf.disable()); // Tắt CSRF nếu cần

        ;

        return http.build();
    }
}

package poly.edu.sneaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SneakerNiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SneakerNiceApplication.class, args);
    }

}

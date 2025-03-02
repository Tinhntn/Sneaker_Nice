package poly.edu.sneaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DangNhap {
    @GetMapping("/dang-nhap")
    public String dangNhap(){
        return "user/dangnhap";
    }
}

package poly.edu.sneaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/chucvu")
public class ChucVuController2 {
    // Controller View
    @GetMapping
    public String viewChucVu() {
        return "admin/chuc_vu/ListChucVu";
    }
}

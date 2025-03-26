package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.edu.sneaker.Service.BanHangTaiQuayService;

@Controller
@RequestMapping("/hoadontaiquay")
public class HoaDonTaiQuayController {
    @Autowired
    BanHangTaiQuayService banHangTaiQuayService ;

}

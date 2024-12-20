package poly.edu.sneaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Service.NhanVienService;

import java.util.List;

@RestController
public class NhanVienController {
    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping("/NhanVien")
    public List<NhanVien> hienThiNhanVien(
            @RequestParam( value = "page",defaultValue = "1") int page,
            @RequestParam(value = "size",defaultValue = "3") int size
    ){
        List<NhanVien> lstNhanVien =  nhanVienService.getAllNhanVien(page,size);
        for ( NhanVien ls: lstNhanVien
             ) {
            System.out.println(ls.getHo_va_ten());

        }
        return nhanVienService.getAllNhanVien(page,size);
    }
}

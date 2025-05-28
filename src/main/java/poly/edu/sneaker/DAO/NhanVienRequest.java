package poly.edu.sneaker.DAO;

import lombok.Data;

import java.util.Date;

@Data
public class NhanVienRequest {
    private String hoVaTen;
    private Integer idcv;
    private Date ngaySinh;
    private Boolean gioiTinh;
    private String diaChi;
    private String sdt;
    private String email;
}
package poly.edu.sneaker.DAO;

public class SanPhamBanChayResponse {
    private String tenSanPham;
    private String hinhAnh;
    private Float giaBan;
    private Long soLuongBan;
    private String tenMauSac;

    public SanPhamBanChayResponse(String tenSanPham, String hinhAnh, Float giaBan, Long soLuongBan,String tenMauSac) {
        this.tenSanPham = tenSanPham;
        this.hinhAnh = hinhAnh;
        this.giaBan = giaBan;
        this.soLuongBan = soLuongBan;
        this.tenMauSac = tenMauSac;
    }

    // Getters
    public String getTenMauSac(){
        return tenMauSac;
    }
    public String getTenSanPham() {
        return tenSanPham;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public Float getGiaBan() {
        return giaBan;
    }

    public Long getSoLuongBan() {
        return soLuongBan;
    }

    // Setters
    public void setTenMauSac(String tenMauSac) {
        this.tenMauSac = tenMauSac;
    }
    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public void setGiaBan(Float giaBan) {
        this.giaBan = giaBan;
    }

    public void setSoLuongBan(Long soLuongBan) {
        this.soLuongBan = soLuongBan;
    }
}
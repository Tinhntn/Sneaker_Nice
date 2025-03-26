package poly.edu.sneaker.DAO;

public class HoaDonChiTietDTO {

    private int idChiTietSanPham;
    private int soLuong;
    private float tongTrongLuong;
    private float donGia;

    public HoaDonChiTietDTO(int idChiTietSanPham, int soLuong, float tongTrongLuong, float donGia) {
        this.idChiTietSanPham = idChiTietSanPham;
        this.soLuong = soLuong;
        this.tongTrongLuong = tongTrongLuong;
        this.donGia = donGia;
    }

    // Getters và Setters
    public int getIdChiTietSanPham() {
        return idChiTietSanPham;
    }

    public void setIdChiTietSanPham(int idChiTietSanPham) {
        this.idChiTietSanPham = idChiTietSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public float getTongTrongLuong() {
        return tongTrongLuong;
    }

    public void setTongTrongLuong(float tongTrongLuong) {
        this.tongTrongLuong = tongTrongLuong;
    }

    public float getDonGia() {
        return donGia;
    }

    public void setDonGia(float donGia) {
        this.donGia = donGia;
    }

}

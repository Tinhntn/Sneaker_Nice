package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Model.HoaDon;
import poly.edu.sneaker.Model.HoaDonChiTiet;
import poly.edu.sneaker.Model.KhachHang;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Repository.HoaDonChiTietRepository;
import poly.edu.sneaker.Repository.KhachHangRepository;
import poly.edu.sneaker.Repository.NhanVienRepository;
import poly.edu.sneaker.Service.KhachHangService;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class KhachHangImplement implements KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Override
    public Page<KhachHang> getAll(Pageable pageable) {
        return khachHangRepository.findAll(pageable);
    }

    @Override
    public KhachHang findKhachHangById(int id) {
        return khachHangRepository.findById(id).orElseThrow(() -> new RuntimeException("KhachHang not found"));
    }

    @Override
    public void saveKhachHang(KhachHang khachHang) {
        khachHangRepository.save(khachHang);
    }

    @Override
    public void updateKhachHang(KhachHang khachHang, int id) {
        KhachHang existingKhachHang = khachHangRepository.findById(id).orElseThrow(() -> new RuntimeException("KhachHang not found"));
        existingKhachHang.setMaKhachHang(khachHang.getMaKhachHang());
        existingKhachHang.setTenKhachHang(khachHang.getTenKhachHang());
        existingKhachHang.setTinhThanhPho(khachHang.getTinhThanhPho());
        existingKhachHang.setQuanHuyen(khachHang.getQuanHuyen());
        existingKhachHang.setPhuongXa(khachHang.getPhuongXa());
        existingKhachHang.setGioiTinh(khachHang.getGioiTinh());
        existingKhachHang.setEmail(khachHang.getEmail());
        existingKhachHang.setSdt(khachHang.getSdt());
        existingKhachHang.setNgaySinh(khachHang.getNgaySinh());
        existingKhachHang.setNgayTao(khachHang.getNgayTao());
        existingKhachHang.setNgaySua(khachHang.getNgaySua());
        existingKhachHang.setMatKhau(khachHang.getMatKhau());
        existingKhachHang.setHinhAnh(khachHang.getHinhAnh());
        existingKhachHang.setTrangThai(khachHang.getTrangThai());
        khachHangRepository.save(existingKhachHang);
    }

    @Override
    public void updateKhachHangHung(KhachHang khachHang, int id) {
        khachHangRepository.save(khachHang);
    }

    @Override
    public void updateKhachHangHoaDonOnl(KhachHang khachHang, int id) {
        KhachHang existingKhachHang = khachHangRepository.findById(id).orElseThrow(() -> new RuntimeException("KhachHang not found"));
        existingKhachHang.setMaKhachHang(khachHang.getMaKhachHang());
        existingKhachHang.setTenKhachHang(khachHang.getTenKhachHang());
        existingKhachHang.setTinhThanhPho(khachHang.getTinhThanhPho());
        existingKhachHang.setQuanHuyen(khachHang.getQuanHuyen());
        existingKhachHang.setPhuongXa(khachHang.getPhuongXa());
        existingKhachHang.setEmail(khachHang.getEmail());
        existingKhachHang.setSdt(khachHang.getSdt());
        existingKhachHang.setTrangThai(khachHang.getTrangThai());
        khachHangRepository.save(existingKhachHang);
    }

    @Override
    public void deleteById(int id) {
        khachHangRepository.deleteById(id);
    }

    @Override
    public Page<KhachHang> search(String keyword, Pageable pageable) {
        return khachHangRepository.findByMaKhachHangContainingOrTenKhachHangContaining(keyword , keyword, pageable);
    }

    @Override
    public KhachHang findByEmail(String email) {
        return khachHangRepository.findByEmail(email);
    }

    @Override
    public KhachHang findByEmailAndMatKhau(String Email, String matKhau) {
        return khachHangRepository.findByEmailAndMatKhau(Email, matKhau);
    }

    @Override
    public boolean exitsKhachHangByEmail(String email) {
        return khachHangRepository.existsKhachHangByEmail(email);
    }

    @Override
    public String taoMaKhachHang() {
        Random r = new Random();
        String maKhachHang = "KH" + 1000 + r.nextInt(9000);
        ArrayList<KhachHang> lstKhachHang = (ArrayList<KhachHang>) khachHangRepository.findAll();
        for (int i = 0; i < lstKhachHang.size(); i++) {
            if(maKhachHang.equals(lstKhachHang.get(i).getMaKhachHang())){
                maKhachHang = "KH" + 1000 + r.nextInt(9000);
            }
        }
        return maKhachHang;
    }

    @Override
    public boolean layLaiKhachHang(KhachHang khachHang) {
        Random r = new Random();
        String matKhau = String.valueOf(r.nextInt(9000));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10
        );
        khachHang.setMatKhau(passwordEncoder.encode(matKhau));
        khachHangRepository.save(khachHang);
        boolean sendMail = sendEmail(khachHang.getEmail(),"Mật khẩu mới của bạn là: ",matKhau);
        return sendMail;
    }
    @Override
    public KhachHang findByMaKhachHang(String maKhachHang) {
        return khachHangRepository.findByMaKhachHang(maKhachHang);
    }
    @Override
    public Page<KhachHang> filterAndSort(Boolean trangThai, String sortBy, String sortDir, Pageable pageable) {
        Sort sort;
        if (sortBy != null && sortDir != null) {
            sort = Sort.by(sortBy);
            sort = sortDir.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();
        } else {
            sort = Sort.by("maKhachHang").ascending();
        }
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        if (trangThai != null) {
            return khachHangRepository.findByTrangThai(trangThai, newPageable);
        } else {
            return khachHangRepository.findAll(newPageable);
        }
    }

    @Override
    public boolean guiMailDonHang(KhachHang khachHang, HoaDon hoaDon, String ghiChu) {
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findHoaDonChiTietByIdHoaDon_Id(hoaDon.getId());
        if (chiTietList == null || chiTietList.isEmpty()) {
            return false;
        }

        // Lọc danh sách nhân viên có chức vụ là ADMIN
        List<NhanVien> lstNhanVien = nhanVienRepository.findAll();
        List<NhanVien> lstNhanVienAdmin = lstNhanVien.stream()
                .filter(nv -> nv.getIdChucVu().getMaChucVu().equalsIgnoreCase("ADMIN"))
                .collect(Collectors.toList());

        String emailNoiDung = buildEmailContent(khachHang, hoaDon, chiTietList, ghiChu);
        String tieuDe = hoaDon.getTrangThai() == 2 ? "YÊU CẦU HỦY ĐƠN!" : "XÁC NHẬN ĐÃ NHẬN HÀNG THÀNH CÔNG";

        final String senderEmail = "ntinh4939@gmail.com";
        final String senderPassword = "rljh bqxc dufy aptz";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Gửi mail cho khách hàng
            if (khachHang.getEmail() != null && !khachHang.getEmail().isEmpty()) {
                Message messageToCustomer = new MimeMessage(session);
                messageToCustomer.setFrom(new InternetAddress(senderEmail));
                messageToCustomer.setRecipients(Message.RecipientType.TO, InternetAddress.parse(khachHang.getEmail()));
                messageToCustomer.setSubject(tieuDe);
                messageToCustomer.setText(emailNoiDung);
                Transport.send(messageToCustomer);
            }

            // Gửi mail cho tất cả ADMIN
            for (NhanVien admin : lstNhanVienAdmin) {
                if (admin.getEmail() != null && !admin.getEmail().isEmpty()) {
                    Message messageToAdmin = new MimeMessage(session);
                    messageToAdmin.setFrom(new InternetAddress(senderEmail));
                    messageToAdmin.setRecipients(Message.RecipientType.TO, InternetAddress.parse(admin.getEmail()));
                    messageToAdmin.setSubject("[ADMIN] " + tieuDe);
                    messageToAdmin.setText(emailNoiDung);
                    Transport.send(messageToAdmin);
                }
            }

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }


    private String buildEmailContent(KhachHang khachHang, HoaDon hoaDon, List<HoaDonChiTiet> chiTietList, String ghiChu) {
        StringBuilder sb = new StringBuilder();

        sb.append("Khách hàng: ").append(khachHang.getTenKhachHang())
                .append(" | Mã KH: ").append(khachHang.getMaKhachHang())
                .append(" | SĐT: ").append(khachHang.getSdt())
                .append(" | Email: ").append(khachHang.getEmail() != null ? khachHang.getEmail() : "Không có").append("\n\n");

        sb.append("Mã hóa đơn: ").append(hoaDon.getMaHoaDon()).append("\n");

        for (HoaDonChiTiet chiTiet : chiTietList) {
            String tenSP = chiTiet.getIdChiTietSanPham().getIdSanPham().getTenSanPham();
            sb.append(String.format("- Sản phẩm: %s | Giá: %,.0f VNĐ | SL: %d%n",
                    tenSP, chiTiet.getDonGia(), chiTiet.getSoLuong()));
        }

        sb.append("\n")
                .append(String.format("Tổng tiền: %,.0f VNĐ%n", hoaDon.getTongTien()))
                .append(String.format("Giảm giá: %,.0f VNĐ%n", hoaDon.getTongTienGiam()))
                .append(String.format("Thành tiền: %,.0f VNĐ%n", hoaDon.getThanhTien()));

        String hinhThucThanhToan = hoaDon.getLoaiThanhToan() ? "Thanh toán bằng VNPAY" : "Thanh toán khi nhận hàng";
        sb.append("Hình thức thanh toán: ").append(hinhThucThanhToan).append("\n");

        if (hoaDon.getLoaiThanhToan()) {
            sb.append(String.format("Đã thanh toán: %,.0f VNĐ%n", hoaDon.getTienKhachDua()));
        }

        if (ghiChu != null && !ghiChu.isEmpty()) {
            sb.append("\nGhi chú: ").append(ghiChu);
        }

        return sb.toString();
    }



    public static boolean sendEmail(String emailNguoiNhan, String tieuDe, String body) {
        // Địa chỉ email và mật khẩu của tài khoản Gmail để gửi email
        String senderEmail = "ntinh4939@gmail.com";
        String senderPassword = "rljh bqxc dufy aptz";

        // Thiết lập thuộc tính của phiên gửi email
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Tạo phiên gửi email với thông tin xác thực
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Tạo một email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailNguoiNhan));
            message.setSubject(tieuDe);
            message.setText(body);

            // Gửi email
            Transport.send(message);
            System.out.println("Email sent successfully!");
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

}
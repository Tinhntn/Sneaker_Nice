package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.DAO.NhanVienCustom;
import poly.edu.sneaker.Model.NhanVien;
import poly.edu.sneaker.Repository.NhanVienRepository;
import poly.edu.sneaker.Service.NhanVienService;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.List;
import java.util.Properties;
import java.util.Random;

@Service
public class NhanVienImplement implements NhanVienService {

    @Autowired
    NhanVienRepository nhanVienRepository;
    private String matKhauMoi;

    @Override
    public Page<NhanVienCustom> getAll(Pageable pageable) {
        return nhanVienRepository.getAll(pageable);
    }

    @Override
    public void saveNhanVien(NhanVien nhanVien) {
        NhanVien existingNhanVien = nhanVienRepository.findByMaNhanVien(nhanVien.getMaNhanVien());
        if (existingNhanVien != null) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại!");
        }
        nhanVienRepository.save(nhanVien);
    }

    @Override
    public void updateNhanVien(NhanVien nhanVien, int id) {
        NhanVien existingNhanVien = nhanVienRepository.findByMaNhanVien(nhanVien.getMaNhanVien());
        if (existingNhanVien != null && existingNhanVien.getId() != (id)) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại!");
        }
        nhanVienRepository.save(nhanVien);
    }

    @Override
    public NhanVien findNhanVienById(int id) {
        return nhanVienRepository.findById(id).get();
    }

    @Override
    public Page<NhanVienCustom> search(String keyword, Pageable pageable) {
        return nhanVienRepository.searchNhanVien(keyword, pageable);
    }

    @Override
    public NhanVien getNhanVienByEmailandMatKhau(String email, String matKhau) {
        return nhanVienRepository.findByEmailAndMatKhau(email,matKhau);
    }

    @Override
    public NhanVien getNhanVienByEmail(String email) {
        return nhanVienRepository.findByEmail(email);
    }

    @Override
    public boolean layLaiMatKhauNhanVien(NhanVien nhanVien) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String matKhauMoi = generatePassword();
        String matKhauHash = passwordEncoder.encode(matKhauMoi);
        nhanVien.setMatKhau(matKhauHash);
        nhanVienRepository.save(nhanVien);
        boolean emailSent = sendEmail(nhanVien.getEmail(), "Mật khẩu mới", nhanVien.getMatKhau());
       return emailSent;

    }
    public static boolean sendEmail(String emailNguoiNhan, String tieuDe, String body) {
        // Địa chỉ email và mật khẩu của tài khoản Gmail để gửi email
        String senderEmail = "ntinh4939@gmail.com";
        String senderPassword = "izygkdjtuqeouoet";

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

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {


        StringBuilder password = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}

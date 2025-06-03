package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import poly.edu.sneaker.Service.MailService;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailImplement implements EmailService {

    @Autowired
    private MailService mailService;
    @Override
    public boolean sendLayLaiMatKhau(String email, String tieuDe, String content) {
        // Địa chỉ email và mật khẩu của tài khoản Gmail để gửi email
        return mailService.sendMail(email, tieuDe, content);
    }
}

package poly.edu.sneaker.Service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import poly.edu.sneaker.Service.EmailService;
import org.springframework.mail.SimpleMailMessage;

@Service
public class EmailImplement implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void sendLayLaiMatKhau(String email, String tieuDe, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_email@gmail.com"); // Email người gửi
        message.setTo(email);
        message.setSubject(tieuDe);
        message.setText(content);
        mailSender.send(message);
    }
}

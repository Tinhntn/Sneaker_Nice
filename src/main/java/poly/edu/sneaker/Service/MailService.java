package poly.edu.sneaker.Service;

public interface MailService {
    boolean sendMail(String toEmail, String subject, String body);
}

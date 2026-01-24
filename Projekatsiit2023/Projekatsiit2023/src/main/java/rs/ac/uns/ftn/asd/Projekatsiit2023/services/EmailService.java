package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String to, String activationLink) {
        try {
            System.out.println("📧 Attempting to send email...");
            System.out.println("   To: " + to);
            System.out.println("   Link: " + activationLink);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("taxiappftn@gmail.com"); // DODAJ OVO!
            message.setTo(to);
            message.setSubject("Activate Your Account");
            message.setText("Click the link to activate your account:\n\n" + activationLink + 
                          "\n\nThis link expires in 24 hours.");
            
            mailSender.send(message);
            System.out.println("✅ Activation email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace(); // DODAJ OVO za detalje
        }
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Reset Your Password");
            message.setText("Click the link to reset your password:\n\n" + resetLink);
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
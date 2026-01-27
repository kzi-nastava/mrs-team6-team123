package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
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
            message.setFrom("taxiappftn@gmail.com");
            message.setTo(to);
            message.setSubject("Activate Your Account");
            message.setText("Click the link to activate your account:\n\n" + activationLink +
                    "\n\nThis link expires in 24 hours.");

            mailSender.send(message);
            System.out.println("✅ Activation email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        try {
            System.out.println("📧 Sending password reset email...");
            System.out.println("   To: " + to);
            System.out.println("   Link: " + resetLink);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("taxiappftn@gmail.com");
            message.setTo(to);
            message.setSubject("Reset Your Password");
            message.setText("Click the link to reset your password:\n\n" + resetLink +
                    "\n\nThis link expires in 1 hour.\n\n" +
                    "If you didn't request this, please ignore this email.");

            mailSender.send(message);
            System.out.println("✅ Password reset email sent to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendDriverProfileChangeNotification(String adminEmail, String driverName,
            String driverEmail, Long changeRequestId,
            String changes) {
        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("taxiappftn@gmail.com");
            message.setTo(adminEmail);
            message.setSubject("Driver Profile Change Request - " + driverName);
            message.setText(String.format(
                    "A driver has requested to update their profile.\n\n" +
                            "Driver: %s (%s)\n" +
                            "Changes requested:\n%s\n\n" +
                            "Please review and approve/reject this request in the admin panel.\n" +
                            "Review URL: http://localhost:4200/admin/profile-changes/%d",
                    driverName, driverEmail, changes, changeRequestId));

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDriverWelcomeEmail(String to, String driverName, String passwordSetupLink) {
        try {
            System.out.println("   To: " + to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("taxiappftn@gmail.com");
            message.setTo(to);
            message.setSubject("Welcome to Our Taxi Service - Set Your Password");
            String emailBody = String.format(
                    "Hello %s,\n\n" +
                            "Welcome to our taxi service! Your driver account has been created by an administrator.\n\n"
                            +
                            "To get started, please set your password by clicking the link below:\n\n" +
                            "%s\n\n" +
                            "This link expires in 24 hours.\n\n" +
                            "After setting your password, you'll be able to log in and start accepting rides.\n\n",
                    driverName,
                    passwordSetupLink);

            message.setText(emailBody);

            mailSender.send(message);
            System.out.println("Driver welcome email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send driver welcome email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendRideFinishedEmail(String to, String rideDetails, String rateLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("taxiappftn@gmail.com");
            message.setTo(to);
            message.setSubject("Your Ride is Complete!");
            String emailBody = String.format(
                    "Hello,\n\n" +
                            "Your ride has been completed. Here are the details of your ride:\n\n" +
                            "%s\n\n" +
                            "You can now rate the driver and vehicle, and provide feedback on you experience\n\n" +
                            "%s\n\n" +
                            "Thank you for choosing our taxi service!\n\n",
                    rideDetails,
                    rateLink);
            message.setText(emailBody);
            mailSender.send(message);
            System.out.println("email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send ride finished email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
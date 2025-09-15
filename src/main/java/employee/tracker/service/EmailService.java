package employee.tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("akg27737@gmail.com");
        message.setSubject("Your Password Reset OTP");
        message.setText("Your OTP is: " + otp + ". It expires in 10 minutes.");
        mailSender.send(message);
    }
}

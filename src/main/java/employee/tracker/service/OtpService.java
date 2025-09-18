package employee.tracker.service;

import employee.tracker.model.Otp;
import employee.tracker.repository.OtpRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpService {
    public final OtpRepo otpRepository;
    public final JavaMailSender mailSender;

    public void generateOtp(String userName, String email) {
        // Create 6-digit random OTP
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        // Store in DB

        otpRepository.deleteByUserName(userName); // remove old otp
        Otp entity = new Otp();
        entity.setUserName(userName);
        entity.setOtp(otp);
        entity.setExpiry(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(entity);

        // Send email
        sendEmail(email, otp);

//        return otp;
    }

    public void sendEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setFrom("akg27737@gmail.com");
        message.setText("Your OTP is: " + otp + " (valid for 5 minutes)");
        mailSender.send(message);
    }

    @Transactional
    public boolean validateOtp(String userName, String otp) {
        Optional<Otp> entity = otpRepository.findByUserName(userName);
        if (entity.isEmpty()) return false;
        if (entity.get().isExpired()) {
            otpRepository.deleteByUserName(userName);
            return false;
        }
        boolean valid = entity.get().getOtp().equals(otp);
        if (valid) otpRepository.deleteByUserName(userName); // one-time use
        return valid;
    }
}

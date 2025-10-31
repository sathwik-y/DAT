package employee.tracker.service;

import employee.tracker.model.Otp;
import employee.tracker.repository.OtpRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpService {

    private final OtpRepo otpRepository;

    // load from application.properties or environment
    @Value("${aws.region:ap-south-1}")
    private String awsRegion;

    @Value("${aws.accessKeyId:}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey:}")
    private String secretAccessKey;

    // in-memory RNG; if you want crypto-grade use SecureRandom
    private final Random random = new Random();

    private SnsClient buildSnsClient() {
        if (accessKeyId != null && !accessKeyId.isBlank()) {
            AwsBasicCredentials creds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            return SnsClient.builder()
                    .region(Region.of(awsRegion))
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .build();
        } else {
            // rely on environment / IAM role
            return SnsClient.builder()
                    .region(Region.of(awsRegion))
                    .build();
        }
    }

    public void generateOtp(String userName, String phone10Digits) {
        // create 6 digit OTP
        String otp = String.format("%06d", random.nextInt(1_000_000));

        // store in DB (remove old)
        otpRepository.deleteByUserName(userName);
        Otp entity = new Otp();
        entity.setUserName(userName);
        entity.setOtp(otp);
        entity.setExpiry(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(entity);

        // send SMS
        sendSmsOtp(phone10Digits, otp);
    }

    private void sendSmsOtp(String phone10Digits, String otp) {
        String e164 = toE164(phone10Digits); // "+91" + phone

        String message = "Your verification code is: " + otp + " (valid for 5 minutes)";

        SnsClient sns = buildSnsClient();

        PublishRequest req = PublishRequest.builder()
                .message(message)
                .phoneNumber(e164)
                .build();

        try {
            PublishResponse res = sns.publish(req);
            // log the message id for debugging/audit
            System.out.println("SNS messageId=" + res.messageId() + " sent to " + e164);
        } catch (SdkException e) {
            // handle error: log and optionally retry via a queue
            System.err.println("Failed to publish SMS to SNS: " + e.getMessage());
            // TODO: add retry (exponential backoff) or push to DLQ
        } finally {
            sns.close();
        }
    }

    private String toE164(String phone10Digits) {
        // sanitize
        String cleaned = phone10Digits.replaceAll("\\D", "");
        if (cleaned.length() == 10) return "+91" + cleaned;
        if (cleaned.startsWith("91") && cleaned.length() == 12) return "+" + cleaned;
        // fallback - try to return as-is
        return "+" + cleaned;
    }

    @Transactional
    public boolean validateOtp(String userName, String otp) {
        Optional<Otp> entityOpt = otpRepository.findByUserName(userName);
        if (entityOpt.isEmpty()) return false;
        Otp entity = entityOpt.get();
        if (entity.isExpired()) {
            otpRepository.deleteByUserName(userName);
            return false;
        }
        boolean valid = entity.getOtp().equals(otp);
        if (valid) otpRepository.deleteByUserName(userName);
        return valid;
    }
}

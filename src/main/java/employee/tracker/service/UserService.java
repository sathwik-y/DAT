package employee.tracker.service;

import employee.tracker.enums.Role;
import employee.tracker.model.Otp;
import employee.tracker.model.Users;
import employee.tracker.repository.OtpRepo;
import employee.tracker.repository.UsersRepo;
import employee.tracker.utility.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepo userRepo;
    private final JwtUtil jwtUtil;
    final AuthenticationManager authenticationManager;
    private final UsersRepo usersRepo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final OtpRepo otpRepo;

    public Users register(Users user){
        if(userRepo.findByUserName(user.getUserName()) != null) throw new RuntimeException("User already exists");
//        if(userRepo.findByEmailId(user.getEmailId()) != null) throw new RuntimeException("Email already exists");
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public String verify(Users user){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
        if(authentication.isAuthenticated()) return jwtUtil.generateToken(user.getUserName());
        return "fail";

    }

    public Users findByUserName(String userName){
        return userRepo.findByUserName(userName);
    }


    public List<Users> findMyTeam(String username) {


        Users manager = userRepo.findByUserName(username);
        if(manager==null) throw new RuntimeException("User not found: " + username);
        return switch (manager.getRole()) {
            case ZH ->
                    userRepo.findByZoneAndUserNameNot(manager.getZone(), username);
            case RH ->
                    userRepo.findByRegionAndUserNameNot(manager.getRegion(), username);
            case ARH ->
                    userRepo.findByRegionAndUserNameNotAndRoleNot(manager.getRegion(),username, Role.RH);
            case AM ->
                    userRepo.findByAreaAndUserNameNot(manager.getArea(), username);
            case NH ->
                    userRepo.findByUserNameNot(username);
            default -> List.of(); // others don’t have a team, but it will never come to this
        };


    }

    @Transactional
    public void registerBulk(List<Users> users) {
        for (Users user : users) {
            // Encode default password for all
            user.setPassword(new BCryptPasswordEncoder().encode("Dat@123"));
            user.setFirstLogin(true);
        }
        usersRepo.saveAll(users); // bulk insert
    }


    @Transactional
    public String generateOtp(Users user) {
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999)); // 6-digit OTP
        Otp otpEntity = Otp.builder()
                .otp(otp)
                .expiry(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        otpRepo.save(otpEntity);
        return otp;
    }
    public boolean verifyOtp(Users user, String otp) {
        Otp otpEntity = otpRepo.findByUserAndOtp(user, otp);
        if(otpEntity == null) return false;
        if(otpEntity.getExpiry().isBefore(LocalDateTime.now())) return false;

        otpRepo.delete(otpEntity); // delete after use
        return true;
    }

}

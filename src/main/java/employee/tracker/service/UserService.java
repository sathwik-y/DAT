package employee.tracker.service;

import employee.tracker.model.Sales;
import employee.tracker.model.Users;
import employee.tracker.repository.UsersRepo;
import employee.tracker.utility.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepo userRepo;
    private final JwtUtil jwtUtil;
    final AuthenticationManager authenticationManager;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

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


}

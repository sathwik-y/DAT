package employee.tracker.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import employee.tracker.model.Users;
import employee.tracker.repository.UsersRepo;
import employee.tracker.service.EmailService;
import employee.tracker.service.OtpService;
import employee.tracker.service.UserService;
import employee.tracker.utility.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    public final UserService service;
    public final AuthenticationManager authenticationManager;
    public final JwtUtil jwtUtil;
    public final UserService userService;
    public final OtpService otpService;
    public final UsersRepo usersRepo;
    public final EmailService emailService;


    @PostMapping("/register/bulk")
    public ResponseEntity<Map<String, String>> bulkRegister(@RequestBody List<Users> users) {
        try {
            service.registerBulk(users);
            Map<String, String> response = new HashMap<>();
            response.put("message", users.size() + " users registered successfully with default password");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Bulk registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(@RequestBody Users user){
        try{
            service.register(user);
            Map<String,String> response = new HashMap<>();
            response.put("message","Registration Successful. Please login to continue");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            Map<String,String> response = new HashMap<>();
            response.put("message","Registration Failed. Please try again");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String> loginRequest){
//        String username = loginRequest.get("userName");
//        String password = loginRequest.get("password");
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(username, password)
//        );
//        String token = jwtUtil.generateToken(username);
//        Map<String,String> response = new HashMap<>();
//
//        // Fetch the user
//        Users user = userService.findByUserName(username);
//        if(user==null) throw new RuntimeException("User not Found");
//
//        Role role = user.getRole();
//        response.put("token",token);
//        response.put("role",role.name());
//        return ResponseEntity.ok(response);
//    }


    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String> loginRequest){
        String username = loginRequest.get("userName");
        String password = loginRequest.get("password");

        System.out.println("=== LOGIN DEBUG ===");
        System.out.println("Username: " + username);
        System.out.println("Password provided: " + (password != null));
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            System.out.println("Authentication successful");
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        Users user = userService.findByUserName(username);
        if(user==null) throw new RuntimeException("User not Found");

        // MODIFIED: Skip OTP, go directly to password reset
        if(user.isFirstLogin()){
            Map<String,String> response = new HashMap<>();
            response.put("message","First login detected. Please change your password.");
            response.put("status","PASSWORD_RESET_REQUIRED");
            response.put("userName", username); // Send username for password reset
            return ResponseEntity.ok(response);
        }

        // Normal login flow (already did firstLogin reset)
        String token = jwtUtil.generateToken(username);
        Map<String,String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().name());
        response.put("firstLogin","false");
        return ResponseEntity.ok(response);
    }





    @GetMapping("/api/auth/verify")
    public ResponseEntity<?> verifyToken(HttpServletRequest request){
        try{
            String token = request.getHeader("Authorization");
            if(token!=null && token.startsWith("Bearer ")){
                token = token.substring(7);
                if(jwtUtil.validateToken(token)){
                    return ResponseEntity.ok().build();
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("hasAnyRole('ZH', 'RH', 'ARH', 'AM', 'NH')")
    @GetMapping("/myTeam")
    @Transactional(readOnly = true)  // Add this annotation
    public ResponseEntity<List<Users>> getMyTeam(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            List<Users> zhTeam = userService.findMyTeam(username);
            return new ResponseEntity<>(zhTeam, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String,String>> verifyOtp(@RequestBody Map<String,String> request) {
        String username = request.get("userName");
        String otp = request.get("otp");

        boolean valid = otpService.validateOtp(username, otp);
        if(!valid){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid or expired OTP"));
        }
        return ResponseEntity.ok(Map.of("message","OTP verified. Please reset your password."));
    }


    @PostMapping("/request-password-reset")
    public ResponseEntity<Map<String,String>> requestPasswordReset(@RequestBody Map<String,String> request) {
        try {
            String username = request.get("userName");
            Users user = userService.findByUserName(username);
            if(user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message","User not found"));

            otpService.generateOtp(user.getUserName(), user.getPhoneNo());
            return ResponseEntity.ok(Map.of("message","OTP sent to your registered phone number."));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send OTP: " + e.getMessage()));
        }
    }


    @PostMapping("/verify-reset-otp")
    public ResponseEntity<Map<String,String>> verifyResetOtp(@RequestBody Map<String,String> request) {
        String username = request.get("userName");
        String otp = request.get("otp");

        Users user = userService.findByUserName(username);
        if(user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message","User not found"));

        boolean valid = otpService.validateOtp(user.getUserName(), otp);
        if(!valid)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message","Invalid or expired OTP"));

        return ResponseEntity.ok(Map.of("message","OTP verified. You can reset your password."));
    }



    @PostMapping("/reset-password")
    public ResponseEntity<Map<String,String>> resetPassword(@RequestBody Map<String,String> request) {
        String username = request.get("userName");
        String newPassword = request.get("newPassword");

        Users user = userService.findByUserName(username);
        if(user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message","User not found"));

        user.setPassword(new BCryptPasswordEncoder(12).encode(newPassword));
        user.setFirstLogin(false);
        usersRepo.save(user);

        return ResponseEntity.ok(Map.of("message","Password reset successful. You can now login."));
    }



    @PostMapping("/first-login-password-change")
    public ResponseEntity<Map<String,String>> firstLoginPasswordChange(@RequestBody Map<String,String> request) {
        String username = request.get("userName");
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if(username == null || currentPassword == null || newPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message","Missing required fields"));
        }

        Users user = userService.findByUserName(username);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message","User not found"));
        }

        // Verify current password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        if(!encoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message","Current password is incorrect"));
        }

        // Check if it's first login
        if(!user.isFirstLogin()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message","This endpoint is only for first-time password change"));
        }

        // Update password and set firstLogin to false
        user.setPassword(encoder.encode(newPassword));
        user.setFirstLogin(false);
        usersRepo.save(user);

        // Generate token for immediate login
        String token = jwtUtil.generateToken(username);
        Map<String,String> response = new HashMap<>();
        response.put("message","Password changed successfully");
        response.put("token", token);
        response.put("role", user.getRole().name());
        
        return ResponseEntity.ok(response);
    }

}

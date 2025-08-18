package employee.tracker.controller;

import employee.tracker.model.Sales;
import employee.tracker.model.Users;
import employee.tracker.service.UserService;
import employee.tracker.utility.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(@RequestBody Users user){
        try{
            service.register(user);
            Map<String,String> response = new HashMap<>();
            response.put("message","Registration Successful. Please login to continue");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            Map<String,String> response = new HashMap<>();
            response.put("message","Registration Failed. Please try again");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String> loginRequest){
        String username = loginRequest.get("userName");
        String password = loginRequest.get("password");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        String token = jwtUtil.generateToken(username);
        Map<String,String> response = new HashMap<>();
        response.put("token",token);
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

}

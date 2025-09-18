package employee.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String userName;
    public String otp;
    public LocalDateTime expiry;
    @ManyToOne
    @JoinColumn(name = "user_id")
    public Users user;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiry);
    }
}

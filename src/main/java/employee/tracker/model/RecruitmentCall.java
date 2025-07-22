package employee.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class RecruitmentCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isFollowUp;
    private LocalDateTime followUpData; // This should only show up if the above value is true

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Recruitment recruitment;
    private String notes;

    @Enumerated(EnumType.STRING)
    private Status status; //will become an enum: Pending, DONE
    @ManyToOne
    private User loggedBy;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}

enum Status {PENDING, CLOSED}

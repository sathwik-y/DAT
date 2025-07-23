package employee.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isFollowUp;
    private LocalDateTime followUpDate; // This should only show up if the above value is true

    @ManyToOne
    @JoinColumn(name = "sale_id")
    @JsonIgnoreProperties({"recruitmentCalls"})
    private Recruitment recruitment;
    private String notes;

    @Enumerated(EnumType.STRING)
    private Status status; // ENUM

    @ManyToOne
    private Users loggedBy;

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

enum Status {OPEN,PENDING, CLOSED}

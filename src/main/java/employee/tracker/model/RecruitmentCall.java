package employee.tracker.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import employee.tracker.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public LocalDateTime createdAt;

    public LocalDateTime updatedAt;

    public boolean isFollowUp;
    public LocalDateTime followUpDate; // This should only show up if the above value is true

    @ManyToOne
    @JoinColumn(name = "recruitment_id")
    @JsonBackReference("recruitment-recruitmentCalls")
    public Recruitment recruitment;


    @Enumerated(EnumType.STRING)
    public Status status; // ENUM
    public String notes;

    @ManyToOne
    @JsonBackReference("user-recruitmentCalls")
    public Users loggedBy;

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


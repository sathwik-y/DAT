package employee.tracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import employee.tracker.enums.Status;
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
public class SalesCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public LocalDateTime createdAt;
    public boolean isFollowUp;
    public LocalDateTime followUpDate;

    @ManyToOne
    @JoinColumn(name="sale_id")
    @JsonBackReference("sale-salesCall")
    public Sales sale;
    public String notes;
    @Enumerated(EnumType.STRING)
    public Status status; //ENUM


    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonBackReference("user-salesCall")
    public Users loggedBy;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }

//    @PreUpdate
//    protected void onUpdate(){
//        this.updatedAt = LocalDateTime.now();
//    }

    @Transient
    @JsonProperty("loggedByName")
    public String getLoggedByName() {
        return loggedBy != null ? loggedBy.getName() : null;
    }

}


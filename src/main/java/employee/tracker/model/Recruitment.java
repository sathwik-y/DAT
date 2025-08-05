package employee.tracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import employee.tracker.enums.IMOptedPosition;
import employee.tracker.enums.LeadSources;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phoneNo;
    private String gender;
    private int age;
    private LocalDateTime dob;
    private String maritalStatus;
    private String occupation;
    private String profession;
    private BigDecimal annualIncome;

    private boolean isCompetition;
    private String competingCompany; // Only if the above one is yes

    private IMOptedPosition optedPosition; // ENUM
    private LocalDateTime createdAt;
    private String referredBy; // Self/Other IMs

    @Enumerated(EnumType.STRING)
    private LeadSources leadSources; // ENUM

    @ManyToOne
    @JoinColumn(name="created_by_id")
    @JsonBackReference
    private Users createdBy;

    @OneToMany(mappedBy = "recruitment",cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<RecruitmentCall> recruitmentCalls;


    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }
}


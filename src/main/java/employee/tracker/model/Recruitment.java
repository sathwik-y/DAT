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
    public Long id;

    public String name;
    public String phoneNo;
    public String gender;
    public int age;
    public LocalDateTime dob;
    public String maritalStatus;
    public String occupation;
    public String profession;
    public BigDecimal annualIncome;

    public boolean isCompetition;
    public String competingCompany; // Only if the above one is yes

    public IMOptedPosition optedPosition; // ENUM
    public LocalDateTime createdAt;
    public String referredBy; // Self/Other IMs

    @Enumerated(EnumType.STRING)
    public LeadSources leadSources; // ENUM

    @ManyToOne
    @JoinColumn(name="created_by_id")
    @JsonBackReference("user-recruitments")
    public Users createdBy;

    @OneToMany(mappedBy = "recruitment",cascade = CascadeType.PERSIST)
    @JsonManagedReference("recruitment-recruitmentCalls")
    public List<RecruitmentCall> recruitmentCalls;


    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }
}


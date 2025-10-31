package employee.tracker.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import employee.tracker.enums.IMOptedPosition;
import employee.tracker.enums.LeadSources;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
// Add these fields to the Recruitment class

    @Column(name = "im_name")
    private String imName;

    @Column(name = "area_name")
    private String areaName;

// Add getters and setters (if not using Lombok)

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }
}


package employee.tracker.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Role;
import employee.tracker.enums.Territory;
import employee.tracker.enums.Zone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id; // This one is only with respect to the database, not the employee id
    public String name;
    public String phoneNo;
    @Column(unique = true,nullable = false)
    public String userName; // This will be the empId of the login
    @Column(nullable = false)
    public String password;
    public String emailId;

    @Enumerated(EnumType.STRING)
    public Role role;

    @Enumerated(EnumType.STRING)
    public Region region;

    @Enumerated(EnumType.STRING)
    public Territory territory;

    @Enumerated(EnumType.STRING)
    public Zone zone;

    @Enumerated(EnumType.STRING)
    public Area area;
    
    public String gender;

    @OneToMany(fetch=FetchType.LAZY,mappedBy = "createdBy",orphanRemoval = false)
    @JsonManagedReference("user-sales")
    public List<Sales> sales; // Because one user can make many sales

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "createdBy", orphanRemoval = false)
    @JsonManagedReference("user-recruitments")
    public List<Recruitment> recruitments; // Same here

    @OneToMany(fetch=FetchType.LAZY,mappedBy = "loggedBy",orphanRemoval = false)
    @JsonManagedReference("user-salesCall")
    public List<SalesCall> salesCalls;

    @OneToMany(fetch=FetchType.LAZY,mappedBy = "loggedBy",orphanRemoval = false)
    @JsonManagedReference("user-recruitmentCalls")
    public List<RecruitmentCall> recruitmentCalls;

    @Column(nullable = true)
    public boolean firstLogin = true;  // 👈 new field


}

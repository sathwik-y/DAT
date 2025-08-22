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
    private Long id; // This one is only with respect to the database, not the employee id
    private String name;
    private String phoneNo;
    @Column(unique = true,nullable = false)
    private String userName; // This will be the empId of the login
    @Column(nullable = false)
    private String password;
    private String emailId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private Territory territory;

    @Enumerated(EnumType.STRING)
    private Zone zone;

    @Enumerated(EnumType.STRING)
    private Area area;
    
    private String gender;

    @OneToMany(fetch=FetchType.LAZY,mappedBy = "createdBy",orphanRemoval = false)
    @JsonManagedReference("user-sales")
    private List<Sales> sales; // Because one user can make many sales

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "createdBy", orphanRemoval = false)
    @JsonManagedReference("user-recruitments")
    private List<Recruitment> recruitments; // Same here

    @OneToMany(fetch=FetchType.LAZY,mappedBy = "loggedBy",orphanRemoval = false)
    @JsonManagedReference("user-salesCall")
    private List<SalesCall> salesCalls;

    @OneToMany(fetch=FetchType.LAZY,mappedBy = "loggedBy",orphanRemoval = false)
    @JsonManagedReference("user-recruitmentCalls")
    private List<RecruitmentCall> recruitmentCalls;


}

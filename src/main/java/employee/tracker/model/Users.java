package employee.tracker.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This one is only with respect to the database, not the employee id. Or we can also switch this with the employee id again later
    private String name;
    private String phoneNo;
    private int empId;

    @Enumerated(EnumType.STRING)
    private Role role; // ENUM

    private String region;
    private String gender;

    @OneToMany(fetch=FetchType.LAZY,mappedBy = "createdBy",orphanRemoval = false)
    private List<Sales> sales; // Because one user can make many sales

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "createdBy", orphanRemoval = false)
    @JsonIgnoreProperties({"createdBy"})
    private List<Recruitment> recruitments; // Same here

    // TODO: Either seperate the tables based on the role, becaue those who are at the higher position might not enter anything on their own and they might just track the data. So we can seperate them or leave them be.

}
enum Role{NH,ZH,RH,ARH,AM,TM,IM,SIM,CIM}
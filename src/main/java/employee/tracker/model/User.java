package employee.tracker.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // This one is only with respect to the database, not the employee id. Or we can also switch this with the employee id again later
    private String name;
    private String phoneNo;
    private int empId;
    private String role; // this is for the rba
    private String designation; // this is with respect to the company
    private String gender;

    @OneToMany(fetch=FetchType.LAZY,mappedBy = "createdBy",orphanRemoval = false)
    private List<Sales> sales; // Because one user can make many sales

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "createdBy", orphanRemoval = false)
    @JsonIgnoreProperties({"createdBy"})
    private List<Recruitment> recruitments; // Same here

    // TODO: Either seperate the tables based on the role, becaue those who are at the higher position might not enter anything on their own and they might just track the data. So we can seperate them or leave them be.

}

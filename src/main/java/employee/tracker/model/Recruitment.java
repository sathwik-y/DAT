package employee.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String phoneNo;
    private String Gender;
    private int age;
    private Date dob;
    private String maritalStatus;
    private String occupation;
    private String profession;
    private int annualIncome;

    private String isCompetition; // ENUM: Yes or No
    private String competingCompany; // Only if the above one is yes

    private String optedPosition; // ENUM: IM/SIM/CIM

    private String referredBy; // Self/Other IMs
    private String leadSources; // If self in the above, ENUM: Tele calling/Lane Mapping/BOP

//    private Date followUp;
//    private String Note;

    @ManyToOne
    @JoinColumn(name="created_by_id")
    @JsonIgnoreProperties({"sales", "recruitments"})
    private User createdBy;

    @OneToMany(mappedBy = "recruitment")
    private List<RecruitmentCall> recruitmentCalls;

}


package employee.tracker.dto;

import employee.tracker.enums.IMOptedPosition;
import employee.tracker.enums.LeadSources;
import employee.tracker.enums.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class NewRecruitmentDTO {

    // Recruitment
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
    private String referredBy; // Self/Other IMs
    private LeadSources leadSources; // ENUM


    // Recruitment Call
    private LocalDateTime followUpDate;
    private Status status; // ENUM
    private String notes;
}


/*
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
 */
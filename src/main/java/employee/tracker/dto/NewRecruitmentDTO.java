package employee.tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import employee.tracker.enums.IMOptedPosition;
import employee.tracker.enums.LeadSources;
import employee.tracker.enums.Status;
import lombok.Data;

@Data
public class NewRecruitmentDTO {

    // Recruitment
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
    public String referredBy; // Self/Other IMs
    public LeadSources leadSources; // ENUM

   private String imName;
   private String areaName;
    // Recruitment Call
    public LocalDateTime followUpDate;
    public Status status; // ENUM
    public String notes;
}


/*
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
 */
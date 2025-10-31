package employee.tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import employee.tracker.enums.Status;
import lombok.Data;

@Data
public class NewSalesDTO {
   // Sales Entity Fields
   public String name;
   public String phoneNo;
   public String gender;
   public int age;
   public LocalDateTime dob;
   public String maritalStatus;
   public String occupation;
   public BigDecimal annualIncome;
   public String product;
   // Created user will be set by the login,
   // And we add the new salesCall from the object we create
   // Add these fields to NewSalesDTO

   private String imName;
   private String areaName;
   // Sales Call Entity Fields
   public LocalDateTime followUpDate;
   // Set the sale based on the above sale created
   public String notes;
   public Status status; // ENUM
   // The user will be set by the login here as well
   public BigDecimal premium;
}
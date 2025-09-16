package employee.tracker.dto;

import employee.tracker.enums.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class NewSalesDTO {
   // Sales Entity Fields
   private String name;
   private String phoneNo;
   private String gender;
   private int age;
   private LocalDateTime dob;
   private String maritalStatus;
   private String occupation;
   private BigDecimal annualIncome;
   private String product;
   // Created user will be set by the login,
   // And we add the new salesCall from the object we create

   // Sales Call Entity Fields
   private LocalDateTime followUpDate;
   // Set the sale based on the above sale created
   private String notes;
   private Status status; // ENUM
   // The user will be set by the login here as well
   private Integer premium;
}
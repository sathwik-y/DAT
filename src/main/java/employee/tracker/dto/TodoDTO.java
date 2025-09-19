package employee.tracker.dto;

import java.time.LocalDateTime;

import employee.tracker.enums.Region;
import employee.tracker.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoDTO {
    private Long callId;
    private String callType; // "SALES" or "RECRUITMENT"
    private LocalDateTime followUpDate;
    private String loggedByName;
    private String notes;
    private String contactName; // Sales or Recruitment name
    private String contactPhone;
    private Status status;
    // private LocalDateTime createdAt;
    
    // Additional context fields
    // private String loggedByUserName;
    // private Role loggedByRole;
    private Region region;
    // private Area area;
    // private Territory territory;
}
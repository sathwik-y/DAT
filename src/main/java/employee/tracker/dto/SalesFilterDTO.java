package employee.tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import employee.tracker.enums.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SalesFilterDTO {
    // Date filters -> Will be sent from the frontend based on the user selection
//    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss\n")
    private LocalDateTime startDate;
//    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss\n")
    private LocalDateTime endDate;

    // Location filters
    private Zone zone;
    private Region region;
    private Territory territory;
    private Area area;



    // User filters
//    private String createdByUsername;
//    private String roleName; TODO: Need to add this if users get to filter by the role as well.

    private Status status;
    private Boolean isFollowUp;

    // TODO: Add pagination

    // Default Date range
    public LocalDateTime getStartDate(){
        if(this.startDate ==null){
            return LocalDateTime.now().withDayOfMonth(1);
        }
        return this.startDate;
    }

    public LocalDateTime getEndDate(){
        if(this.endDate==null){
            return LocalDateTime.now();
        }
        return this.endDate;
    }
}

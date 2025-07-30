package employee.tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SalesFilterDTO {
    // Date filters -> Will be sent from the frontend based on the user selection
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    // Location filters
    private String zone;
    private String region;
    private String territory;
    private String area;

    // User filters
    private String createdByUsername;
    private String roleName;

    private String status;
    private boolean isFollowUp;

    // TODO: Add pagination

    // Default Date range
    public LocalDate getStartDate(){
        if(this.startDate ==null){
            return LocalDate.now().withDayOfMonth(1);
        }
        return this.startDate;
    }

    public LocalDate getEndDate(){
        if(this.endDate==null){
            return LocalDate.now();
        }
        return this.endDate;
    }
}

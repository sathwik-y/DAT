package employee.tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import employee.tracker.enums.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RecruitmentFilterDTO {

    // TODO: As of now, this is similar to SalesFilterDTO, if needed we can add recruitment specific filters. Else merge this and SalesFilterDTO

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime endDate;

    // Location filters
    private Zone zone;
    private Region region;
    private Territory territory;
    private Area area;


    private Status status;
    private Boolean isFollowUp;

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

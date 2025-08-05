package employee.tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import employee.tracker.enums.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RecruitmentFilterDTO {

    // TODO: As of now, this is similar to SalesFilterDTO, if needed we can add recruitment specific filters. Else merge this and SalesFilterDTO

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    // Location filters
    private String zone;
    private String region;
    private String territory;
    private String area;


    private Status status;
    private Boolean isFollowUp;

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

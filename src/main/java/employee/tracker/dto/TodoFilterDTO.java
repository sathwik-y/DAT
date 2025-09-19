package employee.tracker.dto;

import java.time.LocalDateTime;

import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Status;
import employee.tracker.enums.Territory;
import employee.tracker.enums.Zone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoFilterDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Zone zone;
    private Region region;
    private Territory territory;
    private Area area;
    private Status status;
    private String callType; // "SALES", "RECRUITMENT", or null for both
    
    public boolean hasStartDate() {
        return startDate != null;
    }
    
    public boolean hasEndDate() {
        return endDate != null;
    }
    
    public boolean hasZone() {
        return zone != null;
    }
    
    public boolean hasRegion() {
        return region != null;
    }
    
    public boolean hasArea() {
        return area != null;
    }
    
    public boolean hasTerritory() {
        return territory != null;
    }
    
    public boolean hasStatus() {
        return status != null;
    }
    
    public boolean hasCallType() {
        return callType != null && !callType.trim().isEmpty();
    }
}
package employee.tracker.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class RecruitmentFilterDTO {
    public LocalDateTime startDate;
    public LocalDateTime endDate;
    public String zone;
    public String region;
    public String territory;
    public String area;
    public String status;
    public Boolean isFollowUp;

    // Constructors
    public RecruitmentFilterDTO() {}

    public RecruitmentFilterDTO(LocalDateTime startDate, LocalDateTime endDate, String zone, String region, 
                               String territory, String area, String status, Boolean isFollowUp) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.zone = zone;
        this.region = region;
        this.territory = territory;
        this.area = area;
        this.status = status;
        this.isFollowUp = isFollowUp;
    }

    // Helper methods to check if values are meaningful
    public boolean hasStartDate() {
        return startDate != null;
    }

    public boolean hasEndDate() {
        return endDate != null;
    }

    public boolean hasZone() {
        return zone != null && !zone.trim().isEmpty();
    }

    public boolean hasRegion() {
        return region != null && !region.trim().isEmpty();
    }

    public boolean hasTerritory() {
        return territory != null && !territory.trim().isEmpty();
    }

    public boolean hasArea() {
        return area != null && !area.trim().isEmpty();
    }

    public boolean hasStatus() {
        return status != null && !status.trim().isEmpty();
    }

    public boolean hasIsFollowUp() {
        return isFollowUp != null;
    }

    // Getters and setters
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getTerritory() { return territory; }
    public void setTerritory(String territory) { this.territory = territory; }
    
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getIsFollowUp() { return isFollowUp; }
    public void setIsFollowUp(Boolean isFollowUp) { this.isFollowUp = isFollowUp; }
}

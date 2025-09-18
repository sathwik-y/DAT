package employee.tracker.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    
    // Overall metrics
    private long totalSalesCalls;
    private long totalRecruitmentCalls;
    private BigDecimal totalPremiumCollected;
    
    // Regional breakdown
    private List<RegionalMetrics> regionMetrics;
    private List<AreaMetrics> areaMetrics;
    private List<TerritoryMetrics> territoryMetrics;
    private StatusMetrics statusMetrics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusMetrics {
        // Sales calls by status
        private Long salesFollowUp;
        private Long salesDropped;
        private Long salesClosed;

        // Recruitment calls by status
        private Long recruitmentFollowUp;
        private Long recruitmentDropped;
        private Long recruitmentClosed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionalMetrics {
        private String regionName;
        private long salesCalls;
        private long recruitmentCalls;
        private BigDecimal premiumCollected;
        private List<UserMetrics> users;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AreaMetrics {
        private String areaName;
        private long salesCalls;
        private long recruitmentCalls;
        private BigDecimal premiumCollected;
        private List<UserMetrics> users;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TerritoryMetrics {
        private String territoryName;
        private long salesCalls;
        private long recruitmentCalls;
        private BigDecimal premiumCollected;
        private List<UserMetrics> users;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserMetrics {
        private String userName;
        private String name;
        private String role;
        private long salesCalls;
        private long recruitmentCalls;
        private BigDecimal premiumCollected;
    }
}
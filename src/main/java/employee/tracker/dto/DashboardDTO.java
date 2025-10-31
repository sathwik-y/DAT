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
    
    // Unified team structure - replaces regionMetrics, areaMetrics, territoryMetrics
    private List<TeamMetrics> teams;
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
    public static class TeamMetrics {
        private String teamName;        // e.g., "East Region", "Kolkata Area", "Salt Lake Territory"
        private String teamType;        // "REGION", "AREA", or "TERRITORY"
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
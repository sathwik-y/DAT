package employee.tracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employee.tracker.dto.DashboardDTO;
import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @PostMapping("/data")
    public ResponseEntity<DashboardDTO> getDashboardData(@RequestBody DashboardRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        try {
            DashboardDTO dashboardData = dashboardService.getDashboardData(
                username, 
                request.getSalesFilters(), 
                request.getRecruitmentFilters()
            );
            log.info("Fetched Dashboard data for the user: {}",username);
            log.debug("Fetched Dashboard data for the user {} : {}",username,dashboardData);
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            log.error("Failed to fetch  Dashboard for the user: {} | Message: {}",username, ExceptionUtils.getRootCauseMessage(e));
            log.debug("Trace for User {} : ",username,e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Request DTO for dashboard
    @lombok.Data
    public static class DashboardRequestDTO {
        private SalesFilterDTO salesFilters;
        private RecruitmentFilterDTO recruitmentFilters;
    }
}
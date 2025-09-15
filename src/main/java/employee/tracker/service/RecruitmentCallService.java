package employee.tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Status;
import employee.tracker.enums.Territory;
import employee.tracker.enums.Zone;
import employee.tracker.model.Recruitment;
import employee.tracker.model.RecruitmentCall;
import employee.tracker.model.Users;
import employee.tracker.repository.RecruitmentCallRepo;
import employee.tracker.repository.RecruitmentRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentCallService {
    private final UsersRepo usersRepo;
    private final RecruitmentRepo recruitmentRepo;
    private final RecruitmentCallRepo recruitmentCallRepo;

    // Helper method to convert String to Zone enum
    private Zone getZoneEnum(String zoneString) {
        if (zoneString == null || zoneString.trim().isEmpty()) return null;
        try {
            return Zone.valueOf(zoneString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Zone value: " + zoneString);
            return null;
        }
    }
    @Transactional
    public RecruitmentCall createRecruitmentCall(RecruitmentCall recruitmentCall, Long recruitmentId, String username) {
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

        Recruitment recruitment = recruitmentRepo.findById(recruitmentId)
                .orElseThrow(()-> new RuntimeException("Recruitment not found: "+ recruitmentId));

        recruitmentCall.setLoggedBy(user);
        recruitmentCall.setRecruitment(recruitment);

        RecruitmentCall savedCall = recruitmentCallRepo.save(recruitmentCall);
        recruitment.getRecruitmentCalls().add(savedCall);
        user.getRecruitmentCalls().add(savedCall);

        return savedCall;
    }
    // Helper method to convert String to Region enum
    private Region getRegionEnum(String regionString) {
        if (regionString == null || regionString.trim().isEmpty()) return null;
        try {
            return Region.valueOf(regionString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Region value: " + regionString);
            return null;
        }
    }

    // Helper method to convert String to Territory enum
    private Territory getTerritoryEnum(String territoryString) {
        if (territoryString == null || territoryString.trim().isEmpty()) return null;
        try {
            return Territory.valueOf(territoryString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Territory value: " + territoryString);
            return null;
        }
    }

    // Helper method to convert String to Area enum
    private Area getAreaEnum(String areaString) {
        if (areaString == null || areaString.trim().isEmpty()) return null;
        try {
            return Area.valueOf(areaString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Area value: " + areaString);
            return null;
        }
    }

    // Helper method to convert String to Status enum
    private Status getStatusEnum(String statusString) {
        if (statusString == null || statusString.trim().isEmpty()) return null;
        try {
            return Status.valueOf(statusString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Status value: " + statusString);
            return null;
        }
    }

    // Method to get zonal recruitment calls with filters
    public List<RecruitmentCall> getZonalRecruitmentCalls(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? 
            filters.getStartDate() : 
            LocalDateTime.of(1900, 1, 1, 0, 0);
            
        LocalDateTime endDate = filters.hasEndDate() ? 
            filters.getEndDate() : 
            LocalDateTime.of(2100, 12, 31, 23, 59);
        
        System.out.println("RecruitmentCallService: getZonalRecruitmentCalls - startDate=" + startDate + ", endDate=" + endDate);
        
        return recruitmentCallRepo.findZonalRecruitmentCalls(
                user.getZone(),
                user.getRole(),
                startDate,
                endDate,
                filters.hasRegion() ? getRegionEnum(filters.getRegion()) : null,
                filters.hasTerritory() ? getTerritoryEnum(filters.getTerritory()) : null,
                filters.hasArea() ? getAreaEnum(filters.getArea()) : null,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get regional recruitment calls with filters
    public List<RecruitmentCall> getRegionalRecruitmentCalls(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? 
            filters.getStartDate() : 
            LocalDateTime.of(1900, 1, 1, 0, 0);
            
        LocalDateTime endDate = filters.hasEndDate() ? 
            filters.getEndDate() : 
            LocalDateTime.of(2100, 12, 31, 23, 59);
        
        System.out.println("RecruitmentCallService: getRegionalRecruitmentCalls - startDate=" + startDate + ", endDate=" + endDate);
        
        return recruitmentCallRepo.findRegionalRecruitmentCalls(
                user.getRegion(),
                user.getRole(),
                startDate,
                endDate,
                filters.hasArea() ? getAreaEnum(filters.getArea()) : null,
                filters.hasTerritory() ? getTerritoryEnum(filters.getTerritory()) : null,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get territorial recruitment calls with filters
    public List<RecruitmentCall> getTerritorialRecruitmentCalls(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? 
            filters.getStartDate() : 
            LocalDateTime.of(1900, 1, 1, 0, 0);
            
        LocalDateTime endDate = filters.hasEndDate() ? 
            filters.getEndDate() : 
            LocalDateTime.of(2100, 12, 31, 23, 59);
        
        System.out.println("RecruitmentCallService: getTerritorialRecruitmentCalls - startDate=" + startDate + ", endDate=" + endDate);
        
        return recruitmentCallRepo.findTerritorialRecruitmentCalls(
                user.getTerritory(),
                startDate,
                endDate,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get area recruitment calls with filters
    public List<RecruitmentCall> getAreaRecruitmentCalls(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? 
            filters.getStartDate() : 
            LocalDateTime.of(1900, 1, 1, 0, 0);
            
        LocalDateTime endDate = filters.hasEndDate() ? 
            filters.getEndDate() : 
            LocalDateTime.of(2100, 12, 31, 23, 59);
        
        System.out.println("RecruitmentCallService: getAreaRecruitmentCalls - startDate=" + startDate + ", endDate=" + endDate);
        
        return recruitmentCallRepo.findAreaRecruitmentCalls(
                user.getArea(),
                user.getRole(),
                startDate,
                endDate,
                filters.hasTerritory() ? getTerritoryEnum(filters.getTerritory()) : null,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get all national recruitment calls with filters
    public List<RecruitmentCall> getAllRecruitmentCalls(RecruitmentFilterDTO filters) {
        LocalDateTime startDate = filters.hasStartDate() ? 
            filters.getStartDate() : 
            LocalDateTime.of(1900, 1, 1, 0, 0);
            
        LocalDateTime endDate = filters.hasEndDate() ? 
            filters.getEndDate() : 
            LocalDateTime.of(2100, 12, 31, 23, 59);
        
        System.out.println("RecruitmentCallService: getAllRecruitmentCalls - startDate=" + startDate + ", endDate=" + endDate);
        
        return recruitmentCallRepo.findNationalRecruitmentCalls(
                filters.hasZone() ? getZoneEnum(filters.getZone()) : null,
                startDate,
                endDate,
                filters.hasRegion() ? getRegionEnum(filters.getRegion()) : null,
                filters.hasTerritory() ? getTerritoryEnum(filters.getTerritory()) : null,
                filters.hasArea() ? getAreaEnum(filters.getArea()) : null,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get user's own recruitment calls
    public List<RecruitmentCall> getMyRecruitmentCalls(String username) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentCallRepo.findByLoggedBy(user);
    }
}

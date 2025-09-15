package employee.tracker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import employee.tracker.dto.NewRecruitmentDTO;
import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Status;
import employee.tracker.enums.Territory;
import employee.tracker.enums.Zone;
import employee.tracker.model.Recruitment;
import employee.tracker.model.RecruitmentCall;
import employee.tracker.model.Users;
import employee.tracker.repository.RecruitmentRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepo recruitmentRepo;
    private final UsersRepo usersRepo;

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

    public Recruitment createNewRecruitment(NewRecruitmentDTO newRecruitmentDTO, String username) {
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

            // Build the Recruitment object from the DTO
        Recruitment newRecruitment = Recruitment.builder()
                    .name(newRecruitmentDTO.getName())
                    .phoneNo(newRecruitmentDTO.getPhoneNo())
                    .gender(newRecruitmentDTO.getGender())
                    .age(newRecruitmentDTO.getAge())
                    .dob(newRecruitmentDTO.getDob())
                    .maritalStatus(newRecruitmentDTO.getMaritalStatus())
                    .occupation(newRecruitmentDTO.getOccupation())
                    .profession(newRecruitmentDTO.getProfession())
                    .annualIncome(newRecruitmentDTO.getAnnualIncome())
                    .isCompetition(newRecruitmentDTO.isCompetition())
                    .competingCompany(newRecruitmentDTO.getCompetingCompany())
                    .optedPosition(newRecruitmentDTO.getOptedPosition())
                    .referredBy(newRecruitmentDTO.getReferredBy())
                    .leadSources(newRecruitmentDTO.getLeadSources())
                    .createdBy(user)
                    .build();

            // Build the RecruitmentCall object from the DTO
        RecruitmentCall newRecruitmentCall = RecruitmentCall.builder()
                    .followUpDate(newRecruitmentDTO.getFollowUpDate())
                    .notes(newRecruitmentDTO.getNotes())
                    .status(newRecruitmentDTO.getStatus())
                    .isFollowUp(false)
                    .loggedBy(user)
                    .recruitment(newRecruitment)
                    .build();

        newRecruitment.setRecruitmentCalls(new ArrayList<>(List.of(newRecruitmentCall)));
        Recruitment savedRecruitment = recruitmentRepo.save(newRecruitment);

        if(user.getRecruitments()==null) user.setRecruitments(new ArrayList<>());
        user.getRecruitments().add(savedRecruitment);

        if(user.getRecruitmentCalls()==null) user.setRecruitmentCalls(new ArrayList<>());
        user.getRecruitmentCalls().add(savedRecruitment.getRecruitmentCalls().getFirst());
        return savedRecruitment;
        }

    // Method to get zonal recruitments with filters
    public List<Recruitment> getZonalRecruitments(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        // DON'T use wide date ranges - only pass dates when they exist
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("RecruitmentService: getZonalRecruitments - startDate=" + startDate + ", endDate=" + endDate);
        System.out.println("RecruitmentService: hasStartDate=" + filters.hasStartDate() + ", hasEndDate=" + filters.hasEndDate());
        
        return recruitmentRepo.findZonalRecruitments(
                user.getZone(),
                user.getRole(),
                startDate,  // Pass null if not provided
                endDate,    // Pass null if not provided
                filters.hasRegion() ? getRegionEnum(filters.getRegion()) : null,
                filters.hasTerritory() ? getTerritoryEnum(filters.getTerritory()) : null,
                filters.hasArea() ? getAreaEnum(filters.getArea()) : null,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get regional recruitments with filters
    public List<Recruitment> getRegionalRecruitments(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("RecruitmentService: getRegionalRecruitments - startDate=" + startDate + ", endDate=" + endDate);
        
        return recruitmentRepo.findRegionalRecruitments(
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

    // Method to get territorial recruitments with filters
    public List<Recruitment> getTerritorialRecruitments(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("RecruitmentService: getTerritorialRecruitments - startDate=" + startDate + ", endDate=" + endDate);
        
        return recruitmentRepo.findTerritorialRecruitments(
                user.getTerritory(),
                startDate,
                endDate,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get area recruitments with filters
    public List<Recruitment> getAreaRecruitments(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("RecruitmentService: getAreaRecruitments - startDate=" + startDate + ", endDate=" + endDate);
        
        return recruitmentRepo.findAreaRecruitments(
                user.getArea(),
                user.getRole(),
                startDate,
                endDate,
                filters.hasTerritory() ? getTerritoryEnum(filters.getTerritory()) : null,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get all national recruitments with filters
    public List<Recruitment> getAllRecruitments(RecruitmentFilterDTO filters) {
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("RecruitmentService: getAllRecruitments - startDate=" + startDate + ", endDate=" + endDate);
        
        return recruitmentRepo.findNationalRecruitments(
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

    // Method to get user's own recruitments
    public List<Recruitment> findMyRecruitments(String username) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentRepo.findByCreatedBy(user);
    }

}


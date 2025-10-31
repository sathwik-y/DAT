package employee.tracker.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import employee.tracker.dto.DashboardDTO;
import employee.tracker.dto.DashboardDTO.StatusMetrics;
import employee.tracker.dto.DashboardDTO.TeamMetrics;
import employee.tracker.dto.DashboardDTO.UserMetrics;
import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.enums.Role;
import employee.tracker.enums.Status;
import employee.tracker.model.Recruitment;
import employee.tracker.model.RecruitmentCall;
import employee.tracker.model.Sales;
import employee.tracker.model.SalesCall;
import employee.tracker.model.Users;
import employee.tracker.repository.RecruitmentCallRepo;
import employee.tracker.repository.SalesCallRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SalesService salesService;
    private final RecruitmentService recruitmentService;
    private final SalesCallRepo salesCallRepo;
    private final RecruitmentCallRepo recruitmentCallRepo;
    private final UsersRepo usersRepo;

    @Transactional(readOnly = true)
    @Cacheable(value = "dashboardData", key = "#username + '-' + #salesFilters.hashCode() + '-' + #recruitmentFilters.hashCode()")
    public DashboardDTO getDashboardData(String username, SalesFilterDTO salesFilters, RecruitmentFilterDTO recruitmentFilters) {
        Users user = usersRepo.findByUserName(username);

        System.out.println("DashboardService: getDashboardData - user: " + username + ", role: " + user.getRole());

        // Get sales and recruitment data based on user role
        List<Sales> salesList = getSalesByRole(user, salesFilters);
        List<Recruitment> recruitmentList = getRecruitmentsByRole(user, recruitmentFilters);

        // Calculate overall metrics
        long totalSalesCalls = salesList.stream()
                .mapToLong(sale -> sale.getSalesCalls().size())
                .sum();

        long totalRecruitmentCalls = recruitmentList.stream()
                .mapToLong(recruitment -> recruitment.getRecruitmentCalls().size())
                .sum();

        BigDecimal totalPremiumCollected = salesList.stream()
                .map(Sales::getPremium)
                .filter(premium -> premium != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get status metrics
        StatusMetrics statusMetrics = getStatusMetrics(salesList, recruitmentList);

        // Get team metrics based on role
        List<TeamMetrics> teams = getTeamMetricsByRole(user, salesList, recruitmentList);

        System.out.println("DashboardService: Total sales calls: " + totalSalesCalls + ", recruitment calls: " + totalRecruitmentCalls);
        System.out.println("DashboardService: Total teams: " + teams.size());

        return DashboardDTO.builder()
                .totalSalesCalls(totalSalesCalls)
                .totalRecruitmentCalls(totalRecruitmentCalls)
                .totalPremiumCollected(totalPremiumCollected)
                .teams(teams)
                .statusMetrics(statusMetrics)
                .build();
    }

    private List<TeamMetrics> getTeamMetricsByRole(Users user, List<Sales> salesList, List<Recruitment> recruitmentList) {
        List<TeamMetrics> teams = new ArrayList<>();

        switch (user.getRole()) {
            case ZH: // Zonal Head - show each region in their zone as separate teams
                teams = getRegionTeamsForZH(user, salesList, recruitmentList);
                break;

            case RH:
            case ARH: // Regional Head - show region team + optional area/territory team
                teams = getTeamsForRH(user, salesList, recruitmentList);
                break;

            case AM: // Area Manager - show all TMs in their area as one team
                teams = getTeamForAM(user, salesList, recruitmentList);
                break;

            case TM: // Territory Manager - no team to manage
                teams = new ArrayList<>();
                break;

            case NH: // National Head - show all zones as separate teams
                teams = getZoneTeamsForNH(salesList, recruitmentList);
                break;

            default:
                teams = new ArrayList<>();
        }

        return teams;
    }

    // ZH: Get all regions in their zone as separate teams
    private List<TeamMetrics> getRegionTeamsForZH(Users user, List<Sales> salesList, List<Recruitment> recruitmentList) {
        Map<String, TeamMetrics> regionTeamsMap = new HashMap<>();

        // Get all users in the same zone
        List<Users> zoneUsers = usersRepo.findAll().stream()
                .filter(u -> u.getZone() == user.getZone() && u.getRole() != Role.ZH)
                .collect(Collectors.toList());

        // Group by region
        for (Users teamUser : zoneUsers) {
            if (teamUser.getRegion() == null) continue;

            String regionKey = teamUser.getRegion().name();
            
            if (!regionTeamsMap.containsKey(regionKey)) {
                regionTeamsMap.put(regionKey, TeamMetrics.builder()
                        .teamName(formatEnumValue(teamUser.getRegion().name()) + " Region")
                        .teamType("REGION")
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .users(new ArrayList<>())
                        .build());
            }

            // Add user metrics
            UserMetrics userMetrics = calculateUserMetrics(teamUser, salesList, recruitmentList);
            regionTeamsMap.get(regionKey).getUsers().add(userMetrics);
            
            // Update team totals
            TeamMetrics team = regionTeamsMap.get(regionKey);
            team.setSalesCalls(team.getSalesCalls() + userMetrics.getSalesCalls());
            team.setRecruitmentCalls(team.getRecruitmentCalls() + userMetrics.getRecruitmentCalls());
            team.setPremiumCollected(team.getPremiumCollected().add(userMetrics.getPremiumCollected()));
        }

        return new ArrayList<>(regionTeamsMap.values());
    }

    // RH/ARH: Get region team + optional area/territory team
    private List<TeamMetrics> getTeamsForRH(Users user, List<Sales> salesList, List<Recruitment> recruitmentList) {
        List<TeamMetrics> teams = new ArrayList<>();

        // Get all users in the same region (excluding RH/ARH role)
        List<Users> regionUsers = usersRepo.findAll().stream()
                .filter(u -> u.getRegion() == user.getRegion() && u.getRole() != Role.RH && u.getRole() != Role.ARH)
                .collect(Collectors.toList());

        // Create region team
        TeamMetrics regionTeam = TeamMetrics.builder()
                .teamName(formatEnumValue(user.getRegion().name()) + " Region")
                .teamType("REGION")
                .salesCalls(0)
                .recruitmentCalls(0)
                .premiumCollected(BigDecimal.ZERO)
                .users(new ArrayList<>())
                .build();

        for (Users teamUser : regionUsers) {
            UserMetrics userMetrics = calculateUserMetrics(teamUser, salesList, recruitmentList);
            regionTeam.getUsers().add(userMetrics);
            regionTeam.setSalesCalls(regionTeam.getSalesCalls() + userMetrics.getSalesCalls());
            regionTeam.setRecruitmentCalls(regionTeam.getRecruitmentCalls() + userMetrics.getRecruitmentCalls());
            regionTeam.setPremiumCollected(regionTeam.getPremiumCollected().add(userMetrics.getPremiumCollected()));
        }

        teams.add(regionTeam);

        // If RH/ARH has an area assigned, add area team
        if (user.getArea() != null) {
            List<Users> areaUsers = usersRepo.findAll().stream()
                    .filter(u -> u.getArea() == user.getArea() && u.getRole() == Role.TM)
                    .collect(Collectors.toList());

            if (!areaUsers.isEmpty()) {
                TeamMetrics areaTeam = TeamMetrics.builder()
                        .teamName(formatEnumValue(user.getArea().name()) + " Area")
                        .teamType("AREA")
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .users(new ArrayList<>())
                        .build();

                for (Users teamUser : areaUsers) {
                    UserMetrics userMetrics = calculateUserMetrics(teamUser, salesList, recruitmentList);
                    areaTeam.getUsers().add(userMetrics);
                    areaTeam.setSalesCalls(areaTeam.getSalesCalls() + userMetrics.getSalesCalls());
                    areaTeam.setRecruitmentCalls(areaTeam.getRecruitmentCalls() + userMetrics.getRecruitmentCalls());
                    areaTeam.setPremiumCollected(areaTeam.getPremiumCollected().add(userMetrics.getPremiumCollected()));
                }

                teams.add(areaTeam);
            }
        }

        // If RH/ARH has a territory assigned, add territory team
        if (user.getTerritory() != null) {
            List<Users> territoryUsers = usersRepo.findAll().stream()
                    .filter(u -> u.getTerritory() == user.getTerritory() && u.getRole() == Role.TM)
                    .collect(Collectors.toList());

            if (!territoryUsers.isEmpty()) {
                TeamMetrics territoryTeam = TeamMetrics.builder()
                        .teamName(formatEnumValue(user.getTerritory().name()) + " Territory")
                        .teamType("TERRITORY")
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .users(new ArrayList<>())
                        .build();

                for (Users teamUser : territoryUsers) {
                    UserMetrics userMetrics = calculateUserMetrics(teamUser, salesList, recruitmentList);
                    territoryTeam.getUsers().add(userMetrics);
                    territoryTeam.setSalesCalls(territoryTeam.getSalesCalls() + userMetrics.getSalesCalls());
                    territoryTeam.setRecruitmentCalls(territoryTeam.getRecruitmentCalls() + userMetrics.getRecruitmentCalls());
                    territoryTeam.setPremiumCollected(territoryTeam.getPremiumCollected().add(userMetrics.getPremiumCollected()));
                }

                teams.add(territoryTeam);
            }
        }

        return teams;
    }

    // AM: Get all TMs in their area as one team
    private List<TeamMetrics> getTeamForAM(Users user, List<Sales> salesList, List<Recruitment> recruitmentList) {
        List<TeamMetrics> teams = new ArrayList<>();

        // Get all TMs in the same area
        List<Users> areaUsers = usersRepo.findAll().stream()
                .filter(u -> u.getArea() == user.getArea() && u.getRole() == Role.TM)
                .collect(Collectors.toList());

        if (!areaUsers.isEmpty()) {
            TeamMetrics areaTeam = TeamMetrics.builder()
                    .teamName(formatEnumValue(user.getArea().name()) + " Area")
                    .teamType("AREA")
                    .salesCalls(0)
                    .recruitmentCalls(0)
                    .premiumCollected(BigDecimal.ZERO)
                    .users(new ArrayList<>())
                    .build();

            for (Users teamUser : areaUsers) {
                UserMetrics userMetrics = calculateUserMetrics(teamUser, salesList, recruitmentList);
                areaTeam.getUsers().add(userMetrics);
                areaTeam.setSalesCalls(areaTeam.getSalesCalls() + userMetrics.getSalesCalls());
                areaTeam.setRecruitmentCalls(areaTeam.getRecruitmentCalls() + userMetrics.getRecruitmentCalls());
                areaTeam.setPremiumCollected(areaTeam.getPremiumCollected().add(userMetrics.getPremiumCollected()));
            }

            teams.add(areaTeam);
        }

        return teams;
    }

    // NH: Get all zones as separate teams
    private List<TeamMetrics> getZoneTeamsForNH(List<Sales> salesList, List<Recruitment> recruitmentList) {
        Map<String, TeamMetrics> zoneTeamsMap = new HashMap<>();

        // Get all users
        List<Users> allUsers = usersRepo.findAll();

        // Group by zone
        for (Users teamUser : allUsers) {
            if (teamUser.getZone() == null || teamUser.getRole() == Role.NH) continue;

            String zoneKey = teamUser.getZone().name();
            
            if (!zoneTeamsMap.containsKey(zoneKey)) {
                zoneTeamsMap.put(zoneKey, TeamMetrics.builder()
                        .teamName(formatEnumValue(teamUser.getZone().name()) + " Zone")
                        .teamType("ZONE")
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .users(new ArrayList<>())
                        .build());
            }

            // Add user metrics
            UserMetrics userMetrics = calculateUserMetrics(teamUser, salesList, recruitmentList);
            zoneTeamsMap.get(zoneKey).getUsers().add(userMetrics);
            
            // Update team totals
            TeamMetrics team = zoneTeamsMap.get(zoneKey);
            team.setSalesCalls(team.getSalesCalls() + userMetrics.getSalesCalls());
            team.setRecruitmentCalls(team.getRecruitmentCalls() + userMetrics.getRecruitmentCalls());
            team.setPremiumCollected(team.getPremiumCollected().add(userMetrics.getPremiumCollected()));
        }

        return new ArrayList<>(zoneTeamsMap.values());
    }

    private UserMetrics calculateUserMetrics(Users user, List<Sales> salesList, List<Recruitment> recruitmentList) {
        // Filter sales and recruitments created by this user
        long userSalesCalls = salesList.stream()
                .filter(sale -> sale.getCreatedBy().getUserName().equals(user.getUserName()))
                .mapToLong(sale -> sale.getSalesCalls().size())
                .sum();

        long userRecruitmentCalls = recruitmentList.stream()
                .filter(recruitment -> recruitment.getCreatedBy().getUserName().equals(user.getUserName()))
                .mapToLong(recruitment -> recruitment.getRecruitmentCalls().size())
                .sum();

        BigDecimal userPremium = salesList.stream()
                .filter(sale -> sale.getCreatedBy().getUserName().equals(user.getUserName()))
                .map(Sales::getPremium)
                .filter(premium -> premium != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return UserMetrics.builder()
                .userName(user.getUserName())
                .name(user.getName())
                .role(user.getRole().name())
                .salesCalls(userSalesCalls)
                .recruitmentCalls(userRecruitmentCalls)
                .premiumCollected(userPremium)
                .build();
    }

    private String formatEnumValue(String enumValue) {
        if (enumValue == null) return "";

        return java.util.Arrays.stream(enumValue.replace("_", " ").toLowerCase().split(" "))
                .map(word -> word.isEmpty() ? "" : Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(java.util.stream.Collectors.joining(" "));
    }

    // ... rest of the existing methods (getSalesByRole, getRecruitmentsByRole, getStatusMetrics) remain the same
    
    private List<Sales> getSalesByRole(Users user, SalesFilterDTO filters) {
        try {
            switch (user.getRole()) {
                case NH:
                    return salesService.getAllSales(filters);
                case ZH:
                    return salesService.getZonalSales(user.getUserName(), filters);
                case RH:
                case ARH:
                    return salesService.getRegionalSales(user.getUserName(), filters);
                case AM:
                    return salesService.getAreaSales(user.getUserName(), filters);
                case TM:
                    return salesService.getTerritorialSales(user.getUserName(), filters);
                default:
                    return salesService.findMySales(user.getUserName());
            }
        } catch (Exception e) {
            System.err.println("Error getting sales for user " + user.getUserName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Recruitment> getRecruitmentsByRole(Users user, RecruitmentFilterDTO filters) {
        try {
            switch (user.getRole()) {
                case NH:
                    return recruitmentService.getAllRecruitments(filters);
                case ZH:
                    return recruitmentService.getZonalRecruitments(user.getUserName(), filters);
                case RH:
                case ARH:
                    return recruitmentService.getRegionalRecruitments(user.getUserName(), filters);
                case AM:
                    return recruitmentService.getAreaRecruitments(user.getUserName(), filters);
                case TM:
                    return recruitmentService.getTerritorialRecruitments(user.getUserName(), filters);
                default:
                    return recruitmentService.findMyRecruitments(user.getUserName());
            }
        } catch (Exception e) {
            System.err.println("Error getting recruitments for user " + user.getUserName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private StatusMetrics getStatusMetrics(List<Sales> salesList, List<Recruitment> recruitmentList) {
        // Count sales calls by status
        long salesFollowUp = salesList.stream()
                .flatMap(sale -> sale.getSalesCalls().stream())
                .filter(call -> call.getStatus() == Status.FOLLOWUP)
                .count();

        long salesDropped = salesList.stream()
                .flatMap(sale -> sale.getSalesCalls().stream())
                .filter(call -> call.getStatus() == Status.DROPPED)
                .count();

        long salesClosed = salesList.stream()
                .flatMap(sale -> sale.getSalesCalls().stream())
                .filter(call -> call.getStatus() == Status.CLOSED)
                .count();

        // Count recruitment calls by status
        long recruitmentFollowUp = recruitmentList.stream()
                .flatMap(recruitment -> recruitment.getRecruitmentCalls().stream())
                .filter(call -> call.getStatus() == Status.FOLLOWUP)
                .count();

        long recruitmentDropped = recruitmentList.stream()
                .flatMap(recruitment -> recruitment.getRecruitmentCalls().stream())
                .filter(call -> call.getStatus() == Status.DROPPED)
                .count();

        long recruitmentClosed = recruitmentList.stream()
                .flatMap(recruitment -> recruitment.getRecruitmentCalls().stream())
                .filter(call -> call.getStatus() == Status.CLOSED)
                .count();

        return StatusMetrics.builder()
                .salesFollowUp(salesFollowUp)
                .salesDropped(salesDropped)
                .salesClosed(salesClosed)
                .recruitmentFollowUp(recruitmentFollowUp)
                .recruitmentDropped(recruitmentDropped)
                .recruitmentClosed(recruitmentClosed)
                .build();
    }
}
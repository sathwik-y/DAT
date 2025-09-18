package employee.tracker.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import employee.tracker.dto.DashboardDTO;
import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.enums.Role;
import employee.tracker.model.Recruitment;
import employee.tracker.model.Sales;
import employee.tracker.model.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final SalesService salesService;
    private final RecruitmentService recruitmentService;
    private final UserService userService;

    @Cacheable(value="dashboardData",key="#username + '-' + #salesFilters.hashCode() + '-' +  #recruitmentFilters.hashCode()")
    public DashboardDTO getDashboardData(String username, SalesFilterDTO salesFilters, RecruitmentFilterDTO recruitmentFilters) {
        Users currentUser = userService.findByUserName(username);
        
        // Get data based on user role
        List<Sales> salesData = getSalesDataByRole(username, currentUser.getRole(), salesFilters);
        List<Recruitment> recruitmentData = getRecruitmentDataByRole(username, currentUser.getRole(), recruitmentFilters);
        List<Users> allUsers = userService.getAllUsers();


        // Calculate overall metrics
        long totalSalesCalls = salesData.stream()
            .mapToLong(sale -> sale.getSalesCalls().size())
            .sum();
            
        long totalRecruitmentCalls = recruitmentData.stream()
            .mapToLong(recruitment -> recruitment.getRecruitmentCalls().size())
            .sum();
            
        BigDecimal totalPremium = salesData.stream()
            .map(sale -> sale.getPremium() != null ? sale.getPremium() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate regional breakdown
        List<DashboardDTO.RegionalMetrics> regionMetrics = calculateRegionalMetrics(salesData, recruitmentData,allUsers);
        List<DashboardDTO.AreaMetrics> areaMetrics = calculateAreaMetrics(salesData, recruitmentData,allUsers);
        List<DashboardDTO.TerritoryMetrics> territoryMetrics = calculateTerritoryMetrics(salesData, recruitmentData,allUsers);
        
        return DashboardDTO.builder()
            .totalSalesCalls(totalSalesCalls)
            .totalRecruitmentCalls(totalRecruitmentCalls)
            .totalPremiumCollected(totalPremium)
            .regionMetrics(regionMetrics)
            .areaMetrics(areaMetrics)
            .territoryMetrics(territoryMetrics)
            .build();
    }
    
    private List<Sales> getSalesDataByRole(String username, Role role, SalesFilterDTO filters) {
        return switch (role) {
            case ZH -> salesService.getZonalSales(username, filters);
            case RH, ARH -> salesService.getRegionalSales(username, filters);
            case TM -> salesService.getTerritorialSales(username, filters);
            case AM -> salesService.getAreaSales(username, filters);
            case NH -> salesService.getAllSales(filters);
            default -> salesService.findMySales(username);
        };
    }
    
    private List<Recruitment> getRecruitmentDataByRole(String username, Role role, RecruitmentFilterDTO filters) {
        return switch (role) {
            case ZH -> recruitmentService.getZonalRecruitments(username, filters);
            case RH, ARH -> recruitmentService.getRegionalRecruitments(username, filters);
            case TM -> recruitmentService.getTerritorialRecruitments(username, filters);
            case AM -> recruitmentService.getAreaRecruitments(username, filters);
            case NH -> recruitmentService.getAllRecruitments(filters);
            default -> recruitmentService.findMyRecruitments(username);
        };
    }
    
    private List<DashboardDTO.RegionalMetrics> calculateRegionalMetrics(List<Sales> salesData, List<Recruitment> recruitmentData,List<Users> allUsers) {

        Map<String, DashboardDTO.RegionalMetrics> regionMap = new HashMap<>();
        
        // First, get all possible regions from Users table to ensure we show 0 counts
//        List<Users> allUsers = userService.getAllUsers();
        for (Users user : allUsers) {
            if (user.getRegion() != null) {
                String regionName = user.getRegion().name();
                regionMap.computeIfAbsent(regionName, 
                    k -> DashboardDTO.RegionalMetrics.builder()
                        .regionName(regionName)
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .users(new ArrayList<>())
                        .build());
            }
        }
        
        // Process sales data
        for (Sales sale : salesData) {
            Users user = sale.getCreatedBy();
            if (user.getRegion() != null) {
                String regionName = user.getRegion().name();
                DashboardDTO.RegionalMetrics metrics = regionMap.get(regionName);
                if (metrics != null) {
                    metrics.setSalesCalls(metrics.getSalesCalls() + sale.getSalesCalls().size());
                    metrics.setPremiumCollected(metrics.getPremiumCollected().add(
                        sale.getPremium() != null ? sale.getPremium() : BigDecimal.ZERO));
                }
            }
        }
        
        // Process recruitment data
        for (Recruitment recruitment : recruitmentData) {
            Users user = recruitment.getCreatedBy();
            if (user.getRegion() != null) {
                String regionName = user.getRegion().name();
                DashboardDTO.RegionalMetrics metrics = regionMap.get(regionName);
                if (metrics != null) {
                    metrics.setRecruitmentCalls(metrics.getRecruitmentCalls() + recruitment.getRecruitmentCalls().size());
                }
            }
        }
        
        // Calculate user metrics for each region (including users with 0 calls)
        for (DashboardDTO.RegionalMetrics metrics : regionMap.values()) {
            metrics.setUsers(calculateAllUserMetricsForRegion(metrics.getRegionName(), salesData, recruitmentData, allUsers));
        }
        
        return new ArrayList<>(regionMap.values());
    }
    
    private List<DashboardDTO.AreaMetrics> calculateAreaMetrics(List<Sales> salesData, List<Recruitment> recruitmentData,List<Users> allUsers) {
        Map<String, DashboardDTO.AreaMetrics> areaMap = new HashMap<>();
        
        // First, get all possible areas from Users table to ensure we show 0 counts
//        List<Users> allUsers = userService.getAllUsers();
        for (Users user : allUsers) {
            if (user.getArea() != null) {
                String areaName = user.getArea().name();
                areaMap.computeIfAbsent(areaName, 
                    k -> DashboardDTO.AreaMetrics.builder()
                        .areaName(areaName)
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .users(new ArrayList<>())
                        .build());
            }
        }
        
        // Process sales data
        for (Sales sale : salesData) {
            Users user = sale.getCreatedBy();
            if (user.getArea() != null) {
                String areaName = user.getArea().name();
                DashboardDTO.AreaMetrics metrics = areaMap.get(areaName);
                if (metrics != null) {
                    metrics.setSalesCalls(metrics.getSalesCalls() + sale.getSalesCalls().size());
                    metrics.setPremiumCollected(metrics.getPremiumCollected().add(
                        sale.getPremium() != null ? sale.getPremium() : BigDecimal.ZERO));
                }
            }
        }
        
        // Process recruitment data
        for (Recruitment recruitment : recruitmentData) {
            Users user = recruitment.getCreatedBy();
            if (user.getArea() != null) {
                String areaName = user.getArea().name();
                DashboardDTO.AreaMetrics metrics = areaMap.get(areaName);
                if (metrics != null) {
                    metrics.setRecruitmentCalls(metrics.getRecruitmentCalls() + recruitment.getRecruitmentCalls().size());
                }
            }
        }
        
        // Calculate user metrics for each area
        for (DashboardDTO.AreaMetrics metrics : areaMap.values()) {
            metrics.setUsers(calculateAllUserMetricsForArea(metrics.getAreaName(), salesData, recruitmentData, allUsers));
        }
        
        return new ArrayList<>(areaMap.values());
    }
    
    private List<DashboardDTO.TerritoryMetrics> calculateTerritoryMetrics(List<Sales> salesData, List<Recruitment> recruitmentData,List<Users> allUsers) {
        Map<String, DashboardDTO.TerritoryMetrics> territoryMap = new HashMap<>();
        
        // First, get all possible territories from Users table to ensure we show 0 counts
//        List<Users> allUsers = userService.getAllUsers();
        for (Users user : allUsers) {
            if (user.getTerritory() != null) {
                String territoryName = user.getTerritory().name();
                territoryMap.computeIfAbsent(territoryName, 
                    k -> DashboardDTO.TerritoryMetrics.builder()
                        .territoryName(territoryName)
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .users(new ArrayList<>())
                        .build());
            }
        }
        
        // Process sales data
        for (Sales sale : salesData) {
            Users user = sale.getCreatedBy();
            if (user.getTerritory() != null) {
                String territoryName = user.getTerritory().name();
                DashboardDTO.TerritoryMetrics metrics = territoryMap.get(territoryName);
                if (metrics != null) {
                    metrics.setSalesCalls(metrics.getSalesCalls() + sale.getSalesCalls().size());
                    metrics.setPremiumCollected(metrics.getPremiumCollected().add(
                        sale.getPremium() != null ? sale.getPremium() : BigDecimal.ZERO));
                }
            }
        }
        
        // Process recruitment data
        for (Recruitment recruitment : recruitmentData) {
            Users user = recruitment.getCreatedBy();
            if (user.getTerritory() != null) {
                String territoryName = user.getTerritory().name();
                DashboardDTO.TerritoryMetrics metrics = territoryMap.get(territoryName);
                if (metrics != null) {
                    metrics.setRecruitmentCalls(metrics.getRecruitmentCalls() + recruitment.getRecruitmentCalls().size());
                }
            }
        }
        
        // Calculate user metrics for each territory
        for (DashboardDTO.TerritoryMetrics metrics : territoryMap.values()) {
            metrics.setUsers(calculateAllUserMetricsForTerritory(metrics.getTerritoryName(), salesData, recruitmentData, allUsers));
        }
        
        return new ArrayList<>(territoryMap.values());
    }
    
    private List<DashboardDTO.UserMetrics> calculateAllUserMetricsForRegion(String regionName, List<Sales> salesData, List<Recruitment> recruitmentData, List<Users> allUsers) {
        Map<String, DashboardDTO.UserMetrics> userMap = new HashMap<>();
        
        // First, add all users in this region with 0 counts
        for (Users user : allUsers) {
            if (user.getRegion() != null && user.getRegion().name().equals(regionName)) {
                userMap.put(user.getUserName(), 
                    DashboardDTO.UserMetrics.builder()
                        .userName(user.getUserName())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .build());
            }
        }
        
        // Then update with actual data
        for (Sales sale : salesData) {
            Users user = sale.getCreatedBy();
            if (user.getRegion() != null && user.getRegion().name().equals(regionName)) {
                DashboardDTO.UserMetrics metrics = userMap.get(user.getUserName());
                if (metrics != null) {
                    metrics.setSalesCalls(metrics.getSalesCalls() + sale.getSalesCalls().size());
                    metrics.setPremiumCollected(metrics.getPremiumCollected().add(
                        sale.getPremium() != null ? sale.getPremium() : BigDecimal.ZERO));
                }
            }
        }
        
        for (Recruitment recruitment : recruitmentData) {
            Users user = recruitment.getCreatedBy();
            if (user.getRegion() != null && user.getRegion().name().equals(regionName)) {
                DashboardDTO.UserMetrics metrics = userMap.get(user.getUserName());
                if (metrics != null) {
                    metrics.setRecruitmentCalls(metrics.getRecruitmentCalls() + recruitment.getRecruitmentCalls().size());
                }
            }
        }
        
        return new ArrayList<>(userMap.values());
    }
    
    private List<DashboardDTO.UserMetrics> calculateAllUserMetricsForArea(String areaName, List<Sales> salesData, List<Recruitment> recruitmentData, List<Users> allUsers) {
        Map<String, DashboardDTO.UserMetrics> userMap = new HashMap<>();
        
        // First, add all users in this area with 0 counts
        for (Users user : allUsers) {
            if (user.getArea() != null && user.getArea().name().equals(areaName)) {
                userMap.put(user.getUserName(), 
                    DashboardDTO.UserMetrics.builder()
                        .userName(user.getUserName())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .build());
            }
        }
        
        // Then update with actual data
        for (Sales sale : salesData) {
            Users user = sale.getCreatedBy();
            if (user.getArea() != null && user.getArea().name().equals(areaName)) {
                DashboardDTO.UserMetrics metrics = userMap.get(user.getUserName());
                if (metrics != null) {
                    metrics.setSalesCalls(metrics.getSalesCalls() + sale.getSalesCalls().size());
                    metrics.setPremiumCollected(metrics.getPremiumCollected().add(
                        sale.getPremium() != null ? sale.getPremium() : BigDecimal.ZERO));
                }
            }
        }
        
        for (Recruitment recruitment : recruitmentData) {
            Users user = recruitment.getCreatedBy();
            if (user.getArea() != null && user.getArea().name().equals(areaName)) {
                DashboardDTO.UserMetrics metrics = userMap.get(user.getUserName());
                if (metrics != null) {
                    metrics.setRecruitmentCalls(metrics.getRecruitmentCalls() + recruitment.getRecruitmentCalls().size());
                }
            }
        }
        
        return new ArrayList<>(userMap.values());
    }
    
    private List<DashboardDTO.UserMetrics> calculateAllUserMetricsForTerritory(String territoryName, List<Sales> salesData, List<Recruitment> recruitmentData, List<Users> allUsers) {
        Map<String, DashboardDTO.UserMetrics> userMap = new HashMap<>();
        
        // First, add all users in this territory with 0 counts
        for (Users user : allUsers) {
            if (user.getTerritory() != null && user.getTerritory().name().equals(territoryName)) {
                userMap.put(user.getUserName(), 
                    DashboardDTO.UserMetrics.builder()
                        .userName(user.getUserName())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .salesCalls(0)
                        .recruitmentCalls(0)
                        .premiumCollected(BigDecimal.ZERO)
                        .build());
            }
        }
        
        // Then update with actual data
        for (Sales sale : salesData) {
            Users user = sale.getCreatedBy();
            if (user.getTerritory() != null && user.getTerritory().name().equals(territoryName)) {
                DashboardDTO.UserMetrics metrics = userMap.get(user.getUserName());
                if (metrics != null) {
                    metrics.setSalesCalls(metrics.getSalesCalls() + sale.getSalesCalls().size());
                    metrics.setPremiumCollected(metrics.getPremiumCollected().add(
                        sale.getPremium() != null ? sale.getPremium() : BigDecimal.ZERO));
                }
            }
        }
        
        for (Recruitment recruitment : recruitmentData) {
            Users user = recruitment.getCreatedBy();
            if (user.getTerritory() != null && user.getTerritory().name().equals(territoryName)) {
                DashboardDTO.UserMetrics metrics = userMap.get(user.getUserName());
                if (metrics != null) {
                    metrics.setRecruitmentCalls(metrics.getRecruitmentCalls() + recruitment.getRecruitmentCalls().size());
                }
            }
        }
        
        return new ArrayList<>(userMap.values());
    }
}
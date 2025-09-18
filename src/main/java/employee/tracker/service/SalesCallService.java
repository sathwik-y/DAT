package employee.tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Status;
import employee.tracker.enums.Territory;
import employee.tracker.enums.Zone;
import employee.tracker.model.Sales;
import employee.tracker.model.SalesCall;
import employee.tracker.model.Users;
import employee.tracker.repository.SalesCallRepo;
import employee.tracker.repository.SalesRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesCallService {
    public final SalesCallRepo salesCallRepo;
    public final UsersRepo usersRepo;
    public final SalesRepo salesRepo;

    // Helper method to convert String to Zone enum
    public Zone getZoneEnum(String zoneString) {
        if (zoneString == null || zoneString.trim().isEmpty()) return null;
        try {
            return Zone.valueOf(zoneString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Zone value: " + zoneString);
            return null;
        }
    }

    // Helper method to convert String to Region enum
    public Region getRegionEnum(String regionString) {
        if (regionString == null || regionString.trim().isEmpty()) return null;
        try {
            return Region.valueOf(regionString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Region value: " + regionString);
            return null;
        }
    }

    // Helper method to convert String to Territory enum
    public Territory getTerritoryEnum(String territoryString) {
        if (territoryString == null || territoryString.trim().isEmpty()) return null;
        try {
            return Territory.valueOf(territoryString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Territory value: " + territoryString);
            return null;
        }
    }

    // Helper method to convert String to Area enum
    public Area getAreaEnum(String areaString) {
        if (areaString == null || areaString.trim().isEmpty()) return null;
        try {
            return Area.valueOf(areaString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Area value: " + areaString);
            return null;
        }
    }

    // Helper method to convert String to Status enum
    public Status getStatusEnum(String statusString) {
        if (statusString == null || statusString.trim().isEmpty()) return null;
        try {
            return Status.valueOf(statusString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Status value: " + statusString);
            return null;
        }
    }

    @Transactional
    @CacheEvict(value = {"zoneSaleCall", "regionSaleCall", "territorySaleCall", "areaSaleCall", "allSaleCall", "mySaleCall", "dashboardData"}, allEntries = true)
    public SalesCall createSalesCall(SalesCall salesCall, Long saleId, String username) {
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

        Sales sale = salesRepo.findById(saleId).orElseThrow(()-> new RuntimeException("Sale not found: "+ saleId));

        salesCall.setLoggedBy(user);
        salesCall.setSale(sale);

        SalesCall savedCall = salesCallRepo.save(salesCall);

        user.getSalesCalls().add(savedCall);
        sale.getSalesCalls().add(savedCall);

        return savedCall;
    }

    // Method to get zonal sales calls with filters
    @Cacheable(value="zoneSaleCall",key = "#username + '-' + #filters.hashCode()")
    public List<SalesCall> getZonalSalesCalls(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        // FIXED: Pass null instead of wide date ranges for PostgreSQL compatibility
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesCallService: getZonalSalesCalls - startDate=" + startDate + ", endDate=" + endDate);
        System.out.println("SalesCallService: hasStartDate=" + filters.hasStartDate() + ", hasEndDate=" + filters.hasEndDate());
        
        return salesCallRepo.findZonalSalesCalls(
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

    // Method to get regional sales calls with filters
    @Cacheable(value="regionSaleCall",key = "#username + '-' + #filters.hashCode()")
    public List<SalesCall> getRegionalSalesCalls(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesCallService: getRegionalSalesCalls - startDate=" + startDate + ", endDate=" + endDate);
        
        return salesCallRepo.findRegionalSalesCalls(
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

    // Method to get territorial sales calls with filters
    @Cacheable(value="territorySaleCall",key = "#username + '-' + #filters.hashCode()")
    public List<SalesCall> getTerritorialSalesCalls(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesCallService: getTerritorialSalesCalls - startDate=" + startDate + ", endDate=" + endDate);
        
        return salesCallRepo.findTerritorialSalesCalls(
                user.getTerritory(),
                startDate,
                endDate,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get area sales calls with filters
    @Cacheable(value="areaSaleCall",key = "#username + '-' + #filters.hashCode()")
    public List<SalesCall> getAreaSalesCalls(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesCallService: getAreaSalesCalls - startDate=" + startDate + ", endDate=" + endDate);
        
        return salesCallRepo.findAreaSalesCalls(
                user.getArea(),
                user.getRole(),
                startDate,
                endDate,
                filters.hasTerritory() ? getTerritoryEnum(filters.getTerritory()) : null,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get all national sales calls with filters
    @Cacheable(value="allSaleCall",key = "#filters.hashCode()")
    public List<SalesCall> getAllSalesCalls(SalesFilterDTO filters) {
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesCallService: getAllSalesCalls - startDate=" + startDate + ", endDate=" + endDate);
        
        return salesCallRepo.findNationalSalesCalls(
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

    // Method to get user's own sales calls
    @Cacheable(value="mySaleCall",key = "#username")
    public List<SalesCall> getMySalesCalls(String username) {
        Users user = usersRepo.findByUserName(username);
        return salesCallRepo.findByLoggedBy(user);
    }
}

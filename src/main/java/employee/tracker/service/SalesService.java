package employee.tracker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List; // Import all enums

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import employee.tracker.dto.NewSalesDTO;
import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Status;
import employee.tracker.enums.Territory;
import employee.tracker.enums.Zone;
import employee.tracker.model.Sales;
import employee.tracker.model.SalesCall;
import employee.tracker.model.Users;
import employee.tracker.repository.ProductRepo;
import employee.tracker.repository.SalesRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final SalesRepo salesRepo;
    private final UsersRepo usersRepo;
    private final ProductRepo productRepo;

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

    @Transactional
    public Sales createNewSale(NewSalesDTO newSalesDTO, String username) {
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

        // Build the Sale object from the DTO
        Sales newSale = Sales.builder()
                .name(newSalesDTO.getName())
                .phoneNo(newSalesDTO.getPhoneNo())
                .annualIncome(newSalesDTO.getAnnualIncome())
                .gender(newSalesDTO.getGender())
                .age(newSalesDTO.getAge())
                .dob(newSalesDTO.getDob())
                .maritalStatus(newSalesDTO.getMaritalStatus())
                .occupation(newSalesDTO.getOccupation())
                .product(productRepo.findById(newSalesDTO.getProductId()).orElseThrow(() -> new RuntimeException("Product not found")))
                .createdBy(user)
                .build();

        // Build the SalesCall object from the DTO
        SalesCall newSalesCall = SalesCall.builder()
                .followUpDate(newSalesDTO.getFollowUpDate())
                .notes(newSalesDTO.getNotes())
                .status(newSalesDTO.getStatus())
                .isFollowUp(false)
                .loggedBy(user)
                .sale(newSale)
                .build();

        newSale.setSalesCalls(new ArrayList<>(List.of(newSalesCall)));
        Sales savedSale = salesRepo.save(newSale);

        if (user.getSales() == null) {
            user.setSales(new ArrayList<>());  // Initialize if null
        }
        user.getSales().add(savedSale);

        if(user.getSalesCalls()==null){
            user.setSalesCalls(new ArrayList<>());
        }
        user.getSalesCalls().add(savedSale.getSalesCalls().getFirst());
        return savedSale;
    }

    // Method to get zonal sales with filters
    public List<Sales> getZonalSales(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        // FIXED: Pass null instead of wide date ranges for PostgreSQL compatibility
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesService: getZonalSales - startDate=" + startDate + ", endDate=" + endDate);
        System.out.println("SalesService: hasStartDate=" + filters.hasStartDate() + ", hasEndDate=" + filters.hasEndDate());
        
        return salesRepo.findZonalSales(
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

    // Method to get regional sales with filters
    public List<Sales> getRegionalSales(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesService: getRegionalSales - startDate=" + startDate + ", endDate=" + endDate);
        
        return salesRepo.findRegionalSales(
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

    // Method to get territorial sales with filters
    public List<Sales> getTerritorialSales(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesService: getTerritorialSales - startDate=" + startDate + ", endDate=" + endDate);
        
        return salesRepo.findTerritorialSales(
                user.getTerritory(),
                startDate,
                endDate,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get area sales with filters
    public List<Sales> getAreaSales(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesService: getAreaSales - startDate=" + startDate + ", endDate=" + endDate);
        
        return salesRepo.findAreaSales(
                user.getArea(),
                user.getRole(),
                startDate,
                endDate,
                filters.hasTerritory() ? getTerritoryEnum(filters.getTerritory()) : null,
                filters.hasStatus() ? getStatusEnum(filters.getStatus()) : null,
                filters.hasIsFollowUp() ? filters.getIsFollowUp() : null
        );
    }

    // Method to get all national sales with filters
    public List<Sales> getAllSales(SalesFilterDTO filters) {
        LocalDateTime startDate = filters.hasStartDate() ? filters.getStartDate() : null;
        LocalDateTime endDate = filters.hasEndDate() ? filters.getEndDate() : null;
        
        System.out.println("SalesService: getAllSales - startDate=" + startDate + ", endDate=" + endDate);
        
        return salesRepo.findNationalSales(
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

    // Method to get user's own sales
    public List<Sales> findMySales(String username) {
        Users user = usersRepo.findByUserName(username);
        return salesRepo.findByCreatedBy(user);
    }
}

package employee.tracker.service;

import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.model.Sales;
import employee.tracker.model.SalesCall;
import employee.tracker.model.Users;
import employee.tracker.repository.SalesCallRepo;
import employee.tracker.repository.SalesRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesCallService {
    private final SalesCallRepo salesCallRepo;
    private final UsersRepo usersRepo;
    private final SalesRepo salesRepo;

    @Transactional
    public SalesCall createSalesCall(SalesCall salesCall,Long saleId,String username) {
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


    @Transactional
    public void createNewCall(SalesCall newSalesCall, String username) {
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

        newSalesCall.setLoggedBy(user);
        SalesCall newCall = salesCallRepo.save(newSalesCall);
        if (user.getSalesCalls() == null) {
            user.setSalesCalls(new ArrayList<>());  // Initialize if null
        }
        user.getSalesCalls().add(newCall);
    }

    public List<SalesCall> getZonalSalesCalls(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return salesCallRepo.findZonalSalesCalls(
                user.getZone(),
                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getRegion(),
                filters.getTerritory(),
                filters.getArea(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );

    }

    public List<SalesCall> getRegionalSalesCalls(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return salesCallRepo.findRegionalSalesCalls(
                user.getRegion(),
                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getArea(),
                filters.getTerritory(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<SalesCall> getTerritorialSalesCalls(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return salesCallRepo.findTerritorialSalesCalls(
                user.getTerritory(),
//                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<SalesCall> getAreaSalesCalls(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return salesCallRepo.findAreaSalesCalls(
                user.getArea(),
                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getTerritory(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<SalesCall> getAllSalesCalls(SalesFilterDTO filters) {
        return salesCallRepo.findNationalSalesCalls(
                filters.getZone(),
//                "NH",
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getRegion(),
                filters.getTerritory(),
                filters.getArea(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }
}

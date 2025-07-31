package employee.tracker.service;

import employee.tracker.dto.SalesCallFilterDTO;
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

        // TODO: Replace the save with -> Fetch User (and fetch the sale)-> Add Sales to that user -> Persist Changes
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

        newSalesCall.setLoggedBy(user);
        SalesCall newCall = salesCallRepo.save(newSalesCall);
        if (user.getSalesCalls() == null) {
            user.setSalesCalls(new ArrayList<>());  // Initialize if null
        }
        user.getSalesCalls().add(newCall);
    }

    public List<Sales> getZonalSalesCalls(String username, SalesCallFilterDTO filters) {
        return
    }

    public List<Sales> getRegionalSalesCalls(String username, SalesCallFilterDTO filters) {
    }

    public List<Sales> getTerritorialSalesCalls(String username, SalesCallFilterDTO filters) {
    }

    public List<Sales> getAreaSalesCalls(String username, SalesCallFilterDTO filters) {
    }

    public List<Sales> getAllSalesCalls(SalesCallFilterDTO filters) {
    }
}

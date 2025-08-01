package employee.tracker.service;

import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.model.Sales;
import employee.tracker.model.Users;
import employee.tracker.repository.SalesRepo;
import employee.tracker.repository.UsersRepo;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final SalesRepo salesRepo;
    private final UsersRepo usersRepo;

    @Transactional
    public Sales createNewSale(Sales newSale, String username) {
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);
        newSale.setCreatedBy(user);

        Sales savedSale = salesRepo.save(newSale);

        if (user.getSales() == null) {
            user.setSales(new ArrayList<>());  // Initialize if null
        }
        user.getSales().add(savedSale);

        return savedSale;
    }

    // TODO: Will there be a filter by role?

    public List<Sales> getZonalSales(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return salesRepo.findZonalSales(
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

    public List<Sales> getRegionalSales(String username,SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return salesRepo.findRegionalSales(
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
// TODO: The thing with areas, how do we reverse map them, we can probably just do it by the region, each area manager can get their own region, but to fetch all that we need to create a new endpoint or something which will get do this for us.
    // Because in the frontend, how do we know what areas are mapped to this region? We need to select the areas who have this region. We need an endpoint for that.
    // As for the database, it is already handling this in the query it self. So it might just work.
    public List<Sales> getTerritorialSales(String username, SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return salesRepo.findTerritorialSales(
                user.getTerritory(),
//                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<Sales> getAreaSales(String username,SalesFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return salesRepo.findAreaSales(
                user.getArea(),
                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getTerritory(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<Sales> getAllSales(SalesFilterDTO filters) {
        return salesRepo.findNationalSales(
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

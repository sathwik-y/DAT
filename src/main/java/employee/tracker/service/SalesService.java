package employee.tracker.service;

import employee.tracker.model.Sales;
import employee.tracker.repository.SalesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final SalesRepo salesRepo;
    public Sales createNewSale(Sales newSale) {
        // TODO: Replace the save with -> Fetch User-> Add Sales to that user -> Persist Changes
        return salesRepo.save(newSale);
    }
}

package employee.tracker.service;

import employee.tracker.model.Sales;
import employee.tracker.model.SalesCall;
import employee.tracker.repository.SalesCallRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesCallService {
    private final SalesCallRepo salesCallRepo;

    public SalesCall createNewCall(SalesCall newSalesCall) {
        // TODO: Replace the save with -> Fetch User (and fetch the sale)-> Add Sales to that user -> Persist Changes
        return salesCallRepo.save(newSalesCall);
    }
}

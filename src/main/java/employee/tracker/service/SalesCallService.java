package employee.tracker.service;

import employee.tracker.model.SalesCall;
import employee.tracker.model.Users;
import employee.tracker.repository.SalesCallRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SalesCallService {
    private final SalesCallRepo salesCallRepo;
    private final UsersRepo usersRepo;


    @Transactional
    public void createNewCall(SalesCall newSalesCall, String username) {

        // TODO: Replace the save with -> Fetch User (and fetch the sale)-> Add Sales to that user -> Persist Changes
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

        newSalesCall.setLoggedBy(user);

        if (user.getSalesCalls() == null) {
            user.setSalesCalls(new ArrayList<>());  // Initialize if null
        }
        user.getSalesCalls().add(newSalesCall);

        salesCallRepo.save(newSalesCall);
    }
}

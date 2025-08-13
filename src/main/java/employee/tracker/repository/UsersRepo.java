package employee.tracker.repository;
import employee.tracker.model.Sales;
import employee.tracker.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users,Long> {
    Users findByUserName(String userName);
}

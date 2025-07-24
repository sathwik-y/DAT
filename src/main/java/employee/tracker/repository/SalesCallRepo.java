package employee.tracker.repository;

import employee.tracker.model.SalesCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesCallRepo extends JpaRepository<SalesCall,Long> {
}

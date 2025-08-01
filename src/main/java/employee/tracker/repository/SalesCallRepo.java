package employee.tracker.repository;

import employee.tracker.enums.Role;
import employee.tracker.enums.Zone;
import employee.tracker.model.Sales;
import employee.tracker.model.SalesCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesCallRepo extends JpaRepository<SalesCall,Long> {
  List<Sales> findZonalSalesCalls(Zone zone, Role role, LocalDate startDate, LocalDate endDate, String region, String territory, String area);
}

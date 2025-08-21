package employee.tracker.repository;
import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Role;
import employee.tracker.enums.Zone;
import employee.tracker.model.Sales;
import employee.tracker.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users,Long> {
    Users findByUserName(String userName);

    List<Users> findByZoneAndUserNameNot(Zone zone, String username);

    List<Users> findByRegionAndUserNameNot(Region region, String username);

    List<Users> findByAreaAndUserNameNot(Area area, String username);

    List<Users> findByUserNameNot(String username);

    List<Users> findByRegionAndUserNameNotAndRoleNot(Region region, String username, Role role);
}

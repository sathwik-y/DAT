package employee.tracker.repository;
import employee.tracker.enums.*;
import employee.tracker.model.Sales;
import employee.tracker.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users,Long> {
    Users findByUserName(String userName);


    @Query("""
    SELECT u FROM Users u
    WHERE u.userName <> :username
    AND u.zone = :zone
    """)
    List<Users> findZonalTeam(@Param("zone")Zone zone, @Param("username")String username);


    @Query("""
SELECT u FROM Users u
WHERE u.userName <> :username
AND (
      (u.region = :region AND u.area IS NULL AND u.territory IS NULL)
   OR (u.area = :area)
   OR (u.territory = :territory)
)
""")
    List<Users> findRHTeam(@Param("region")Region region, @Param("area")Area area, @Param("username")String username,
                           @Param("territory")Territory territory);


    @Query("""
    SELECT u FROM Users u
    WHERE u.userName <> :username
    AND u.role <> :role
    AND u.region = :region
    """)
    List<Users> findARHTeam(@Param("region") Region region,@Param("username")String username, @Param("area")Area area, @Param("role")Role role);


    @Query("""
    SELECT u FROM Users u
    WHERE u.userName <> :username
    AND u.area = :area
    AND u.role = :role
    """)
    List<Users> findAMTeam(@Param("area")Area area, @Param("username")String username,@Param("role")Role role);


    @Query("""
    SELECT u FROM Users u
    WHERE u.userName <> :username
    """)
    List<Users> findNHTeam(@Param("username")String username);
}


package employee.tracker.repository;

import employee.tracker.enums.Region;
import employee.tracker.enums.Role;
import employee.tracker.enums.Territory;
import employee.tracker.enums.Zone;
import employee.tracker.model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesRepo extends JpaRepository<Sales,Long> {

    // Fetch the Zonal Head Sales with filters (passed from the frontend)
    @Query("select s FROM Sales s " +
            "WHERE s.createdBy.zone=:zone " +
            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            "AND (:region is NULL or s.createdBy.region = :region) " +
            "AND (:territory is NULL or s.createdBy.territory= :territory)" +
            "AND  (:area is NULL or s.createdBy.area = :area) "
    )
    List<Sales> findZonalSales(
            @Param("zone") Zone zone,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("region") String region,
            @Param("territory") String territory,
            @Param("area") String area
            );


    // Fetch the Regional Head Sales
    @Query("select s FROM Sales s " +
            "WHERE s.createdBy.region=:region " +
            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            // TODO: Might have to change a few filters based on the hierarchy
            "AND  (:area is NULL or s.createdBy.area = :area) " +
            "AND (:territory is NULL or s.createdBy.territory = :territory)"
    )
    List<Sales> findRegionalSales(
            @Param("region") Region createdByRegion,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("area") String area,
            @Param("territory") String territory
            );




    // NOTE: If there are no one under the TM,this will be just about fetching his own sales details, which will be redundant once we add "My Profile" Section
    @Query("select s From Sales s " +
            "WHERE s.createdBy.territory=:territory " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate) " +
            "AND (:endDate is NULL or s.createdAt<=:endDate)")
    List<Sales> findTerritorialSales(
            @Param("territory") Territory createdByTerritory,
//            Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("select s FROM Sales s " +
            "WHERE s.createdBy.area=:area " +
            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            "AND (:territory is NULL or s.createdBy.territory = :territory)"
    )
    List<Sales> findAreaSales(
            @Param("area") String createdByArea,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("territory") String territory
    );

    // The NH can basically get everything
    @Query("select s FROM Sales s " +
            "WHERE (:zone is NULL OR s.createdBy.zone=:zone)" +
//            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            "AND (:region is NULL or s.createdBy.region = :region) " +
            "AND (:territory is NULL or s.createdBy.territory= :territory)" +
            "AND  (:area is NULL or s.createdBy.area = :area) "
    )
    List<Sales> findNationalSales(
            @Param("zone") String zone,
//            @Param("role") String role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("region") String region,
            @Param("territory") String territory,
            @Param("area") String area
    );
}

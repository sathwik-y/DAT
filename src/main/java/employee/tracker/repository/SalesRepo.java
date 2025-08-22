package employee.tracker.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import employee.tracker.enums.*;
import employee.tracker.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import employee.tracker.model.Sales;

@Repository
public interface SalesRepo extends JpaRepository<Sales,Long> {

    // Fetch the Zonal Head Sales with filters (passed from the frontend)
    @Query("""
    SELECT DISTINCT s FROM Sales s
    JOIN FETCH s.salesCalls sc
    JOIN FETCH s.createdBy u
    WHERE u.zone = :zone
      AND u.role != :role
      AND (sc.isFollowUp = COALESCE(:isFollowUp, sc.isFollowUp))
      AND (s.createdAt >= COALESCE(:startDate, s.createdAt))
      AND (s.createdAt <= COALESCE(:endDate, s.createdAt))
      AND (u.region    = COALESCE(:region, u.region))
      AND (u.territory = COALESCE(:territory, u.territory))
      AND (u.area      = COALESCE(:area, u.area))
      AND (sc.status   = COALESCE(:status, sc.status))
      AND sc.createdAt = (
          SELECT MAX(sc2.createdAt)
          FROM SalesCall sc2
          WHERE sc2.sale = s
      )
""")

    List<Sales> findZonalSales(
            @Param("zone") Zone zone,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("region") Region region,
            @Param("territory") Territory territory,
            @Param("area") Area area,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    // Fetch the Regional Head Sales
    @Query("SELECT DISTINCT s FROM Sales s " +
            "JOIN FETCH s.salesCalls sc " +
            "JOIN FETCH s.createdBy u " +
            "WHERE u.region = COALESCE(:region, u.region) " +
            "AND u.role != :role " +
            "AND s.createdAt >= COALESCE(:startDate, s.createdAt) " +
            "AND s.createdAt <= COALESCE(:endDate, s.createdAt) " +
            "AND sc.createdAt = (" +
            "  SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s" +
            ")" +
            "AND sc.status = COALESCE(:status, sc.status) " +
            "AND sc.isFollowUp = COALESCE(:isFollowUp, sc.isFollowUp) " +
            "AND u.area = COALESCE(:area, u.area) " +
            "AND u.territory = COALESCE(:territory, u.territory)"
    )
    List<Sales> findRegionalSales(
            @Param("region") Region createdByRegion,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("area") Area area,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    // NOTE: If there is no one under the TM, this will be just about fetching his own sales details, which will be redundant once we add "My Profile" Section
    @Query("SELECT DISTINCT s FROM Sales s " +
            "JOIN FETCH s.salesCalls sc " +
            "JOIN FETCH s.createdBy u " +
            "WHERE u.territory = COALESCE(:territory, u.territory) " +
            "AND sc.createdAt = (" +
            "  SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s" +
            ")" +
            "AND s.createdAt >= COALESCE(:startDate, s.createdAt) " +
            "AND s.createdAt <= COALESCE(:endDate, s.createdAt) " +
            "AND sc.status = COALESCE(:status, sc.status) " +
            "AND sc.isFollowUp = COALESCE(:isFollowUp, sc.isFollowUp)"
    )
    List<Sales> findTerritorialSales(
            @Param("territory") Territory createdByTerritory,
//            Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT DISTINCT s FROM Sales s " +
            "JOIN FETCH s.salesCalls sc " +
            "JOIN FETCH s.createdBy u " +
            "WHERE u.area = COALESCE(:area, u.area) " +
            "AND u.role != :role " +
            "AND s.createdAt >= COALESCE(:startDate, s.createdAt) " +
            "AND s.createdAt <= COALESCE(:endDate, s.createdAt) " +
            "AND sc.createdAt = (" +
            "  SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s" +
            ")" +
            "AND u.territory = COALESCE(:territory, u.territory) " +
            "AND sc.status = COALESCE(:status, sc.status) " +
            "AND sc.isFollowUp = COALESCE(:isFollowUp, sc.isFollowUp)"
    )

    List<Sales> findAreaSales(
            @Param("area") Area createdByArea,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    // The NH can basically get everything
    @Query("SELECT DISTINCT s FROM Sales s " +
            "JOIN FETCH s.salesCalls sc " +
            "JOIN FETCH s.createdBy u " +
            "WHERE u.zone = COALESCE(:zone, u.zone) " +
//     "AND u.role != :role " +   // uncomment if you really need this filter
            "AND s.createdAt >= COALESCE(:startDate, s.createdAt) " +
            "AND s.createdAt <= COALESCE(:endDate, s.createdAt) " +
            "AND u.region = COALESCE(:region, u.region) " +
            "AND u.territory = COALESCE(:territory, u.territory) " +
            "AND u.area = COALESCE(:area, u.area) " +
            "AND sc.createdAt = (" +
            "  SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s" +
            ")" +
            "AND sc.status = COALESCE(:status, sc.status) " +
            "AND sc.isFollowUp = COALESCE(:isFollowUp, sc.isFollowUp)"
    )
    List<Sales> findNationalSales(
            @Param("zone") Zone zone,
//            @Param("role") String role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("region") Region region,
            @Param("territory") Territory territory,
            @Param("area") Area area,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    List<Sales> findByCreatedBy(Users user);
}
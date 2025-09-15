package employee.tracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Role;
import employee.tracker.enums.Status;
import employee.tracker.enums.Territory;
import employee.tracker.enums.Zone;
import employee.tracker.model.Sales;
import employee.tracker.model.Users;

@Repository
public interface SalesRepo extends JpaRepository<Sales,Long> {

    // Fetch the Zonal Head Sales with filters (passed from the frontend)
    @Query("""
    SELECT DISTINCT s FROM Sales s
    JOIN FETCH s.salesCalls sc
    JOIN FETCH s.createdBy u
    WHERE u.zone = :zone
      AND u.role != :role
      AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp)
      AND (CAST(:startDate AS timestamp) IS NULL OR s.createdAt >= :startDate)
      AND (CAST(:endDate AS timestamp) IS NULL OR s.createdAt <= :endDate)
      AND (:region IS NULL OR u.region = :region)
      AND (:territory IS NULL OR u.territory = :territory)
      AND (:area IS NULL OR u.area = :area)
      AND (:status IS NULL OR sc.status = :status)
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
    @Query("""
    SELECT DISTINCT s FROM Sales s
    JOIN FETCH s.salesCalls sc
    JOIN FETCH s.createdBy u
    WHERE u.region = :region
      AND u.role != :role
      AND (CAST(:startDate AS timestamp) IS NULL OR s.createdAt >= :startDate)
      AND (CAST(:endDate AS timestamp) IS NULL OR s.createdAt <= :endDate)
      AND (:area IS NULL OR u.area = :area)
      AND (:territory IS NULL OR u.territory = :territory)
      AND (:status IS NULL OR sc.status = :status)
      AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp)
      AND sc.createdAt = (
          SELECT MAX(sc2.createdAt)
          FROM SalesCall sc2
          WHERE sc2.sale = s
      )
""")
    List<Sales> findRegionalSales(
            @Param("region") Region region,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("area") Area area,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    // Fetch the Territorial Sales
    @Query("""
    SELECT DISTINCT s FROM Sales s
    JOIN FETCH s.salesCalls sc
    JOIN FETCH s.createdBy u
    WHERE u.territory = :territory
      AND (CAST(:startDate AS timestamp) IS NULL OR s.createdAt >= :startDate)
      AND (CAST(:endDate AS timestamp) IS NULL OR s.createdAt <= :endDate)
      AND (:status IS NULL OR sc.status = :status)
      AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp)
      AND sc.createdAt = (
          SELECT MAX(sc2.createdAt)
          FROM SalesCall sc2
          WHERE sc2.sale = s
      )
""")
    List<Sales> findTerritorialSales(
            @Param("territory") Territory territory,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    // Fetch the Area Head Sales
    @Query("""
    SELECT DISTINCT s FROM Sales s
    JOIN FETCH s.salesCalls sc
    JOIN FETCH s.createdBy u
    WHERE u.area = :area
      AND u.role != :role
      AND (CAST(:startDate AS timestamp) IS NULL OR s.createdAt >= :startDate)
      AND (CAST(:endDate AS timestamp) IS NULL OR s.createdAt <= :endDate)
      AND (:territory IS NULL OR u.territory = :territory)
      AND (:status IS NULL OR sc.status = :status)
      AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp)
      AND sc.createdAt = (
          SELECT MAX(sc2.createdAt)
          FROM SalesCall sc2
          WHERE sc2.sale = s
      )
""")
    List<Sales> findAreaSales(
            @Param("area") Area area,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    // Fetch National Sales (all zones)
    @Query("""
    SELECT DISTINCT s FROM Sales s
    JOIN FETCH s.salesCalls sc
    JOIN FETCH s.createdBy u
    WHERE (:zone IS NULL OR u.zone = :zone)
      AND (CAST(:startDate AS timestamp) IS NULL OR s.createdAt >= :startDate)
      AND (CAST(:endDate AS timestamp) IS NULL OR s.createdAt <= :endDate)
      AND (:region IS NULL OR u.region = :region)
      AND (:territory IS NULL OR u.territory = :territory)
      AND (:area IS NULL OR u.area = :area)
      AND (:status IS NULL OR sc.status = :status)
      AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp)
      AND sc.createdAt = (
          SELECT MAX(sc2.createdAt)
          FROM SalesCall sc2
          WHERE sc2.sale = s
      )
""")
    List<Sales> findNationalSales(
            @Param("zone") Zone zone,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("region") Region region,
            @Param("territory") Territory territory,
            @Param("area") Area area,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    List<Sales> findByCreatedBy(Users createdBy);
}
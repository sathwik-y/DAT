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
import employee.tracker.model.SalesCall;
import employee.tracker.model.Users;

@Repository
public interface SalesCallRepo extends JpaRepository<SalesCall,Long> {

  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
          "WHERE (:zone IS NULL OR u.zone = :zone) " +
          "AND u.role != :role " +
          "AND (CAST(:startDate AS timestamp) IS NULL OR sc.createdAt >= :startDate) " +
          "AND (CAST(:endDate AS timestamp) IS NULL OR sc.createdAt <= :endDate) " +
          "AND (:region IS NULL OR u.region = :region) " +
          "AND (:territory IS NULL OR u.territory = :territory) " +
          "AND (:area IS NULL OR u.area = :area) " +
          "AND (:status IS NULL OR sc.status = :status) " +
          "AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findZonalSalesCalls(
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

  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
          "WHERE u.region = :region " +
          "AND u.role != :role " +
          "AND (CAST(:startDate AS timestamp) IS NULL OR sc.createdAt >= :startDate) " +
          "AND (CAST(:endDate AS timestamp) IS NULL OR sc.createdAt <= :endDate) " +
          "AND (:status IS NULL OR sc.status = :status) " +
          "AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp) " +
          "AND (:area IS NULL OR u.area = :area) " +
          "AND (:territory IS NULL OR u.territory = :territory)"
  )
  List<SalesCall> findRegionalSalesCalls(
          @Param("region") Region createdByRegion,
          @Param("role") Role role,
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate,
          @Param("area") Area area,
          @Param("territory") Territory territory,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );

  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
          "WHERE u.territory = :territory " +
          "AND (CAST(:startDate AS timestamp) IS NULL OR sc.createdAt >= :startDate) " +
          "AND (CAST(:endDate AS timestamp) IS NULL OR sc.createdAt <= :endDate) " +
          "AND (:status IS NULL OR sc.status = :status) " +
          "AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findTerritorialSalesCalls(
          @Param("territory") Territory createdByTerritory,
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );

  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
          "WHERE u.area = :area " +
          "AND u.role != :role " +
          "AND (CAST(:startDate AS timestamp) IS NULL OR sc.createdAt >= :startDate) " +
          "AND (CAST(:endDate AS timestamp) IS NULL OR sc.createdAt <= :endDate) " +
          "AND (:territory IS NULL OR u.territory = :territory) " +
          "AND (:status IS NULL OR sc.status = :status) " +
          "AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findAreaSalesCalls(
          @Param("area") Area createdByArea,
          @Param("role") Role role,
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate,
          @Param("territory") Territory territory,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );

  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
          "WHERE (:zone IS NULL OR u.zone = :zone) " +
          "AND (CAST(:startDate AS timestamp) IS NULL OR sc.createdAt >= :startDate) " +
          "AND (CAST(:endDate AS timestamp) IS NULL OR sc.createdAt <= :endDate) " +
          "AND (:region IS NULL OR u.region = :region) " +
          "AND (:territory IS NULL OR u.territory = :territory) " +
          "AND (:area IS NULL OR u.area = :area) " +
          "AND (:status IS NULL OR sc.status = :status) " +
          "AND (:isFollowUp IS NULL OR sc.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findNationalSalesCalls(
          @Param("zone") Zone zone,
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate,
          @Param("region") Region region,
          @Param("territory") Territory territory,
          @Param("area") Area area,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );

  List<SalesCall> findByLoggedBy(Users loggedBy);
}
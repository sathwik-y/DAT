package employee.tracker.repository;

import employee.tracker.enums.*;
import employee.tracker.model.Sales;
import employee.tracker.model.SalesCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesCallRepo extends JpaRepository<SalesCall,Long> {

  @Query("SELECT s FROM SalesCall s " +
          "WHERE s.loggedBy.zone=:zone " +
          "AND s.loggedBy.role!=:role " +
          "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
          "AND (:endDate is NULL or s.createdAt<=:endDate)" +
          "AND (:region is NULL or s.loggedBy.region = :region) " +
          "AND (:territory is NULL or s.loggedBy.territory= :territory)" +
          "AND  (:area is NULL or s.loggedBy.area = :area) " +
          "AND (:status is NULL or s.status = :status) " +
          "AND (:isFollowUp is NULL or s.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findZonalSalesCalls(
          @Param("zone") Zone zone,
          @Param("role") Role role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("region") String region,
          @Param("territory") String territory,
          @Param("area") String area,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );

  @Query("SELECT s FROM SalesCall s " +
          "WHERE s.loggedBy.region=:region " +
          "AND s.loggedBy.role!=:role " +
          "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
          "AND (:endDate is NULL or s.createdAt<=:endDate)" +
          // TODO: Might have to change a few filters based on the hierarchy
          "AND (:status is NULL or s.status = :status) " +
          "AND (:isFollowUp is NULL or s.isFollowUp = :isFollowUp)" +
          "AND (:area is NULL or s.loggedBy.area = :area) " +
          "AND (:territory is NULL or s.loggedBy.territory = :territory)"
  )
  List<SalesCall> findRegionalSalesCalls(
          @Param("region") Region createdByRegion,
          @Param("role") Role role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("area") String area,
          @Param("territory") String territory,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );


  @Query("SELECT s FROM SalesCall s " +
          "WHERE s.loggedBy.territory = :territory " +
          "AND (:startDate is NULL or s.createdAt>=:startDate)" +
          "AND (:endDate is NULL or s.createdAt<=:endDate)" +
          "AND (:status is NULL or s.status = :status)" +
          "AND (:isFollowUp is NULL or s.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findTerritorialSalesCalls(
          @Param("territory") Territory createdByTerritory,
//            Role role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
          );

  @Query("SELECT s FROM SalesCall s " +
          "WHERE s.loggedBy.area=:area " +
          "AND s.loggedBy.role!=:role " +
          "AND (:startDate is NULL OR s.createdAt>= :startDate) " +
          "AND (:endDate is NULL or s.createdAt<=:endDate)" +
          "AND (:territory is NULL or s.loggedBy.territory = :territory)" +
          "AND (:status is NULL or s.status = :status) " +
          "AND (:isFollowUp is NULL or s.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findAreaSalesCalls(
          @Param("area") String createdByArea,
          @Param("role") Role role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("territory") String territory,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );

  @Query("SELECT s FROM SalesCall s " +
          "WHERE (:zone is NULL OR s.loggedBy.zone=:zone)" +
//            "AND s.createdBy.role!=:role " +
          "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
          "AND (:endDate is NULL or s.createdAt<=:endDate)" +
          "AND (:region is NULL or s.loggedBy.region = :region) " +
          "AND (:territory is NULL or s.loggedBy.territory= :territory)" +
          "AND (:area is NULL or s.loggedBy.area = :area) " +
          "AND (:status is NULL or s.status = :status) " +
          "AND (:isFollowUp is NULL or s.isFollowUp = :isFollowUp) "
  )
  List<SalesCall> findNationalSalesCalls(
          @Param("zone") String zone,
//            @Param("role") String role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("region") String region,
          @Param("territory") String territory,
          @Param("area") String area,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );
}
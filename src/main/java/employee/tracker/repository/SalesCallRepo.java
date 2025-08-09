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

  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
//          "JOIN FETCH sc.sale s " + Can be added later if we need to fetch the Sale data of a particular call
          "WHERE u.zone=:zone " +
          "AND u.role!=:role " +
          "AND (:startDate is NULL OR sc.createdAt>= :startDate)" +
          "AND (:endDate is NULL OR sc.createdAt<=:endDate)" +
          "AND (:region is NULL OR u.region = :region) " +
          "AND (:territory is NULL OR u.territory= :territory)" +
          "AND  (:area is NULL OR u.area = :area) " +
          "AND (:status is NULL OR sc.status = :status) " +
          "AND (:isFollowUp is NULL OR sc.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findZonalSalesCalls(
          @Param("zone") Zone zone,
          @Param("role") Role role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("region") Region region,
          @Param("territory") Territory territory,
          @Param("area") Area area,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );

  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
          "WHERE u.region=:region " +
          "AND u.role!=:role " +
          "AND (:startDate is NULL OR sc.createdAt>= :startDate)" +
          "AND (:endDate is NULL or sc.createdAt<=:endDate)" +
          // TODO: Might have to change a few filters based on the hierarchy
          "AND (:status is NULL or sc.status = :status) " +
          "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)" +
          "AND (:area is NULL or u.area = :area) " +
          "AND (:territory is NULL or u.territory = :territory)"
  )
  List<SalesCall> findRegionalSalesCalls(
          @Param("region") Region createdByRegion,
          @Param("role") Role role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("area") Area area,
          @Param("territory") Territory territory,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );


  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
          "WHERE u.territory = :territory " +
          "AND (:startDate is NULL or sc.createdAt>=:startDate)" +
          "AND (:endDate is NULL or sc.createdAt<=:endDate)" +
          "AND (:status is NULL or sc.status = :status)" +
          "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findTerritorialSalesCalls(
          @Param("territory") Territory createdByTerritory,
//            Role role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
          );

  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
          "WHERE u.area=:area " +
          "AND u.role!=:role " +
          "AND (:startDate is NULL OR sc.createdAt>= :startDate) " +
          "AND (:endDate is NULL or sc.createdAt<=:endDate)" +
          "AND (:territory is NULL or u.territory = :territory)" +
          "AND (:status is NULL or sc.status = :status) " +
          "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)"
  )
  List<SalesCall> findAreaSalesCalls(
          @Param("area") Area createdByArea,
          @Param("role") Role role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("territory") Territory territory,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );

  @Query("SELECT DISTINCT sc FROM SalesCall sc " +
          "JOIN FETCH sc.loggedBy u " +
          "WHERE (:zone is NULL OR u.zone=:zone)" +
//          "AND s.createdBy.role!=:role " +
          "AND (:startDate is NULL OR sc.createdAt>= :startDate)" +
          "AND (:endDate is NULL or sc.createdAt<=:endDate)" +
          "AND (:region is NULL or u.region = :region) " +
          "AND (:territory is NULL or u.territory= :territory)" +
          "AND (:area is NULL or u.area = :area) " +
          "AND (:status is NULL or sc.status = :status) " +
          "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp) "
  )
  List<SalesCall> findNationalSalesCalls(
          @Param("zone") Zone zone,
//            @Param("role") String role,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("region") Region region,
          @Param("territory") Territory territory,
          @Param("area") Area area,
          @Param("status") Status status,
          @Param("isFollowUp") Boolean isFollowUp
  );
}
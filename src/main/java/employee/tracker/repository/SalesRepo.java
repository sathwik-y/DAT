package employee.tracker.repository;

import java.time.LocalDate;
import java.util.List;

import employee.tracker.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import employee.tracker.model.Sales;

@Repository
public interface SalesRepo extends JpaRepository<Sales,Long> {

    // Fetch the Zonal Head Sales with filters (passed from the frontend)
    @Query("SELECT DISTINCT s FROM Sales s " +
            "JOIN FETCH s.salesCalls sc " +
            "JOIN FETCH s.createdBy u " +
            "WHERE u.zone=:zone " +
            "AND u.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            "AND (:region is NULL or u.region = :region) " +
            "AND (:territory is NULL or u.territory= :territory)" +
            "AND  (:area is NULL or u.area = :area) " +
            "AND sc.createdAt = (" +
            "SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s" +
            ")" +
            "AND (:status is NULL or sc.status = :status) " +
            "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)"
    )
    List<Sales> findZonalSales(
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


    // Fetch the Regional Head Sales
    @Query("SELECT DISTINCT s FROM Sales s " +
            "JOIN FETCH s.salesCalls sc " +
            "JOIN FETCH s.createdBy u " +
            "WHERE u.region=:region " +
            "AND u.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            // TODO: Might have to change a few filters based on the hierarchy
            "AND sc.createdAt = (" +
            "SELECT MAX(sc2.createdAt) FROM SalesCall  sc2 WHERE sc2.sale = s" +
            ")" +
            "AND (:status is NULL or sc.status = :status) " +
            "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)" +
            "AND (:area is NULL or u.area = :area) " +
            "AND (:territory is NULL or u.territory = :territory)"
    )
    List<Sales> findRegionalSales(
            @Param("region") Region createdByRegion,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("area") Area area,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    // NOTE: If there is no one under the TM, this will be just about fetching his own sales details, which will be redundant once we add "My Profile" Section
    @Query("SELECT DISTINCT s From Sales s " +
            "JOIN FETCH s.salesCalls sc " +
            "JOIN FETCH s.createdBy u " +
            "WHERE u.territory=:territory " +
            "AND sc.createdAt = (" +
            "SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale=s" +
            ")" +
            "AND (:startDate is NULL OR s.createdAt>= :startDate) " +
            "AND (:status is NULL or sc.status = :status) " +
            "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)"+
            "AND (:endDate is NULL or s.createdAt<=:endDate)")
    List<Sales> findTerritorialSales(
            @Param("territory") Territory createdByTerritory,
//            Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT DISTINCT s FROM Sales s " +
            "JOIN FETCH s.salesCalls sc " +
            "JOIN FETCH s.createdBy u " +
            "WHERE u.area=:area " +
            "AND u.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate) " +
            "AND sc.createdAt = (" +
            "SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s" +
            ")" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            "AND (:territory is NULL or u.territory = :territory)" +
            "AND (:status is NULL or sc.status = :status) " +
            "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)"
    )
    List<Sales> findAreaSales(
            @Param("area") Area createdByArea,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    // The NH can basically get everything
    @Query("SELECT DISTINCT s FROM Sales s " +
            "JOIN FETCH s.salesCalls sc " +
            "JOIN FETCH s.createdBy u " +
            "WHERE (:zone is NULL OR u.zone=:zone)" +
//            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            "AND (:region is NULL or u.region = :region) " +
            "AND (:territory is NULL or u.territory= :territory)" +
            "AND (:area is NULL or u.area = :area) " +
            "AND sc.createdAt = (" +
            "SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s" +
            ")" +
            "AND (:status is NULL or sc.status = :status) " +
            "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp) "
    )
    List<Sales> findNationalSales(
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
package employee.tracker.repository;

import employee.tracker.enums.*;
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
    @Query("SELECT s FROM Sales s " +
            "JOIN s.salesCalls sc " +
            "WHERE s.createdBy.zone=:zone " +
            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            "AND (:region is NULL or s.createdBy.region = :region) " +
            "AND (:territory is NULL or s.createdBy.territory= :territory)" +
            "AND  (:area is NULL or s.createdBy.area = :area) " +
            "AND sc.createdAt = (SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s)" +
            "AND (:status is NULL or sc.status = :status) " +
            "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)"
    )
    List<Sales> findZonalSales(
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


    // Fetch the Regional Head Sales
    @Query("SELECT s FROM Sales s " +
            "JOIN s.salesCalls sc " +
            "WHERE s.createdBy.region=:region " +
            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            // TODO: Might have to change a few filters based on the hierarchy
            "AND sc.createdAt = (SELECT MAX(sc2.createdAt) FROM SalesCall  sc2 WHERE sc2.sale = s)" +
            "AND (:status is NULL or sc.status = :status) " +
            "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)" +
            "AND (:area is NULL or s.createdBy.area = :area) " +
            "AND (:territory is NULL or s.createdBy.territory = :territory)"
    )
    List<Sales> findRegionalSales(
            @Param("region") Region createdByRegion,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("area") String area,
            @Param("territory") String territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    // NOTE: If there are no one under the TM,this will be just about fetching his own sales details, which will be redundant once we add "My Profile" Section
    @Query("select s From Sales s " +
            "JOIN s.salesCalls sc " +
            "WHERE s.createdBy.territory=:territory " +
            "AND sc.createdAt = (SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale=s)" +
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


    @Query("SELECT s FROM Sales s " +
            "JOIN s.salesCalls sc " +
            "WHERE s.createdBy.area=:area " +
            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate) " +
            "AND sc.createdAt = (SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            "AND (:territory is NULL or s.createdBy.territory = :territory)" +
            "AND (:status is NULL or sc.status = :status) " +
            "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp)"
    )
    List<Sales> findAreaSales(
            @Param("area") String createdByArea,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("territory") String territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    // The NH can basically get everything
    @Query("SELECT s FROM Sales s " +
            "JOIN s.salesCalls sc " +
            "WHERE (:zone is NULL OR s.createdBy.zone=:zone)" +
//            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR s.createdAt>= :startDate)" +
            "AND (:endDate is NULL or s.createdAt<=:endDate)" +
            "AND (:region is NULL or s.createdBy.region = :region) " +
            "AND (:territory is NULL or s.createdBy.territory= :territory)" +
            "AND (:area is NULL or s.createdBy.area = :area) " +
            "AND sc.createdAt = (SELECT MAX(sc2.createdAt) FROM SalesCall sc2 WHERE sc2.sale = s)" +
            "AND (:status is NULL or sc.status = :status) " +
            "AND (:isFollowUp is NULL or sc.isFollowUp = :isFollowUp) "
    )
    List<Sales> findNationalSales(
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
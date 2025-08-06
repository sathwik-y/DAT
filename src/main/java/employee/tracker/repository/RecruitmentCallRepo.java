package employee.tracker.repository;

import employee.tracker.enums.*;
import employee.tracker.model.RecruitmentCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecruitmentCallRepo extends JpaRepository<RecruitmentCall,Long> {

    @Query("SELECT r FROM RecruitmentCall r " +
            "WHERE r.loggedBy.zone=:zone " +
            "AND r.loggedBy.role!=:role " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:region is NULL or r.loggedBy.region = :region) " +
            "AND (:territory is NULL or r.loggedBy.territory= :territory)" +
            "AND  (:area is NULL or r.loggedBy.area = :area) " +
            "AND (:status is NULL or r.status = :status) " +
            "AND (:isFollowUp is NULL or r.isFollowUp = :isFollowUp)"
    )
    List<RecruitmentCall> findZonalRecruitmentCalls(
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



    @Query("SELECT r FROM RecruitmentCall r " +
            "WHERE r.loggedBy.region=:region " +
            "AND r.loggedBy.role!=:role " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            // TODO: Might have to change a few filters based on the hierarchy
            "AND (:status is NULL or r.status = :status) " +
            "AND (:isFollowUp is NULL or r.isFollowUp = :isFollowUp)" +
            "AND (:area is NULL or r.loggedBy.area = :area) " +
            "AND (:territory is NULL or r.loggedBy.territory = :territory)"
    )
    List<RecruitmentCall> findRegionalRecruitmentCalls(
            @Param("region") Region createdByRegion,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("area") String area,
            @Param("territory") String territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    @Query("SELECT r FROM RecruitmentCall r " +
            "WHERE r.loggedBy.territory = :territory " +
            "AND (:startDate is NULL or r.createdAt>=:startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:status is NULL or r.status = :status)" +
            "AND (:isFollowUp is NULL or r.isFollowUp = :isFollowUp)"
    )
    List<RecruitmentCall> findTerritorialRecruitmentCalls(
            @Param("territory") Territory createdByTerritory,
//            Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    @Query("SELECT r FROM RecruitmentCall r " +
            "WHERE r.loggedBy.area=:area " +
            "AND r.loggedBy.role!=:role " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate) " +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:territory is NULL or r.loggedBy.territory = :territory)" +
            "AND (:status is NULL or r.status = :status) " +
            "AND (:isFollowUp is NULL or r.isFollowUp = :isFollowUp)"
    )
    List<RecruitmentCall> findAreaRecruitmentCalls(
            @Param("area") String createdByArea,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("territory") String territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT r FROM RecruitmentCall r " +
            "WHERE (:zone is NULL OR r.loggedBy.zone=:zone)" +
//            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:region is NULL or r.loggedBy.region = :region) " +
            "AND (:territory is NULL or r.loggedBy.territory= :territory)" +
            "AND (:area is NULL or r.loggedBy.area = :area) " +
            "AND (:status is NULL or r.status = :status) " +
            "AND (:isFollowUp is NULL or r.isFollowUp = :isFollowUp) "
    )
    List<RecruitmentCall> findNationalRecruitmentCalls(
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

package employee.tracker.repository;

import java.time.LocalDate;
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
import employee.tracker.model.RecruitmentCall;


// TODO: Need to update this with COALSEC like done in SalesRepo

@Repository
public interface RecruitmentCallRepo extends JpaRepository<RecruitmentCall,Long> {

    @Query("SELECT DISTINCT rc FROM RecruitmentCall rc " +
            "JOIN FETCH rc.loggedBy u " +
            "WHERE u.zone=:zone " +
            "AND u.role!=:role " +
            "AND (:startDate is NULL OR rc.createdAt>= :startDate)" +
            "AND (:endDate is NULL or rc.createdAt<=:endDate)" +
            "AND (:region is NULL or u.region = :region) " +
            "AND (:territory is NULL or u.territory= :territory)" +
            "AND  (:area is NULL or u.area = :area) " +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<RecruitmentCall> findZonalRecruitmentCalls(
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



    @Query("SELECT DISTINCT rc FROM RecruitmentCall rc " +
            "JOIN FETCH rc.loggedBy u " +
            "WHERE u.region=:region " +
            "AND u.role!=:role " +
            "AND (:startDate is NULL OR rc.createdAt>= :startDate)" +
            "AND (:endDate is NULL or rc.createdAt<=:endDate)" +
            // TODO: Might have to change a few filters based on the hierarchy
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)" +
            "AND (:area is NULL or u.area = :area) " +
            "AND (:territory is NULL or u.territory = :territory)"
    )
    List<RecruitmentCall> findRegionalRecruitmentCalls(
            @Param("region") Region createdByRegion,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("area") Area area,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    @Query("SELECT DISTINCT rc FROM RecruitmentCall rc " +
            "JOIN FETCH rc.loggedBy u " +
            "WHERE u.territory = :territory " +
            "AND (:startDate is NULL or rc.createdAt>=:startDate)" +
            "AND (:endDate is NULL or rc.createdAt<=:endDate)" +
            "AND (:status is NULL or rc.status = :status)" +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<RecruitmentCall> findTerritorialRecruitmentCalls(
            @Param("territory") Territory createdByTerritory,
//            Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    @Query("SELECT DISTINCT rc FROM RecruitmentCall rc " +
            "JOIN FETCH rc.loggedBy u " +
            "WHERE u.area=:area " +
            "AND u.role!=:role " +
            "AND (:startDate is NULL OR rc.createdAt>= :startDate) " +
            "AND (:endDate is NULL or rc.createdAt<=:endDate)" +
            "AND (:territory is NULL or u.territory = :territory)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<RecruitmentCall> findAreaRecruitmentCalls(
            @Param("area") Area createdByArea,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT DISTINCT rc FROM RecruitmentCall rc " +
            "JOIN FETCH rc.loggedBy u " +
            "WHERE (:zone is NULL OR u.zone=:zone)" +
//            "AND s.createdBy.role!=:role " +
            "AND (:startDate is NULL OR rc.createdAt>= :startDate)" +
            "AND (:endDate is NULL or rc.createdAt<=:endDate)" +
            "AND (:region is NULL or u.region = :region) " +
            "AND (:territory is NULL or u.territory= :territory)" +
            "AND (:area is NULL or u.area = :area) " +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp) "
    )
    List<RecruitmentCall> findNationalRecruitmentCalls(
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
}

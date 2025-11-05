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
import employee.tracker.model.RecruitmentCall;
import employee.tracker.model.Users;

@Repository
public interface RecruitmentCallRepo extends JpaRepository<RecruitmentCall,Long> {

    @Query("SELECT DISTINCT rc FROM RecruitmentCall rc " +
            "JOIN FETCH rc.loggedBy u " +
            "WHERE u.zone = :zone " +
            "AND u.role != :role " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR rc.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR rc.createdAt <= :endDate) " +
            "AND (:region IS NULL OR u.region = :region) " +
            "AND (:territory IS NULL OR u.territory = :territory) " +
            "AND (:area IS NULL OR u.area = :area) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp)"
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
            "WHERE u.region = :region " +
            "AND u.role != :role " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR rc.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR rc.createdAt <= :endDate) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp) " +
            "AND (:area IS NULL OR u.area = :area) " +
            "AND (:territory IS NULL OR u.territory = :territory)"
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
            "AND (CAST(:startDate AS timestamp) IS NULL OR rc.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR rc.createdAt <= :endDate) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp)"
    )
    List<RecruitmentCall> findTerritorialRecruitmentCalls(
            @Param("territory") Territory createdByTerritory,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    @Query("SELECT DISTINCT rc FROM RecruitmentCall rc " +
            "JOIN FETCH rc.loggedBy u " +
            "WHERE u.area = :area " +
            "AND u.role != :role " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR rc.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR rc.createdAt <= :endDate) " +
            "AND (:territory IS NULL OR u.territory = :territory) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp)"
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
            "WHERE (:zone IS NULL OR u.zone = :zone) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR rc.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR rc.createdAt <= :endDate) " +
            "AND (:region IS NULL OR u.region = :region) " +
            "AND (:territory IS NULL OR u.territory = :territory) " +
            "AND (:area IS NULL OR u.area = :area) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp)"
    )
    List<RecruitmentCall> findNationalRecruitmentCalls(
            @Param("zone") Zone zone,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("region") Region region,
            @Param("territory") Territory territory,
            @Param("area") Area area,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    List<RecruitmentCall> findByLoggedBy(Users loggedBy);

        @Query("SELECT rc FROM RecruitmentCall rc " +
           "JOIN FETCH rc.recruitment r " +
           "JOIN FETCH rc.loggedBy lb " +
           "WHERE lb.userName = :username " +
           "AND rc.followUpDate IS NOT NULL " +
           "AND rc.followUpDate BETWEEN :startDate AND :endDate")
    List<RecruitmentCall> findFollowUpsByUserAndDateRange(
        @Param("username") String username,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}

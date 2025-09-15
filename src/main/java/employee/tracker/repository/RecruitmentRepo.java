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
import employee.tracker.model.Recruitment;
import employee.tracker.model.Users;

@Repository
public interface RecruitmentRepo extends JpaRepository<Recruitment,Long> {

    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN FETCH r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE (:zone IS NULL OR u.zone = :zone) " +
            "AND u.role != :role " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR r.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR r.createdAt <= :endDate) " +
            "AND (:region IS NULL OR u.region = :region) " +
            "AND (:territory IS NULL OR u.territory = :territory) " +
            "AND (:area IS NULL OR u.area = :area) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findZonalRecruitments(
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

    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN FETCH r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE u.role != :role  " +
            "AND u.region = :region " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR r.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR r.createdAt <= :endDate) " +
            "AND (:territory IS NULL OR u.territory = :territory) " +
            "AND (:area IS NULL OR u.area = :area) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findRegionalRecruitments(
            @Param("region") Region createdByRegion,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("area") Area area,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN FETCH r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE u.territory = :territory " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR r.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR r.createdAt <= :endDate) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findTerritorialRecruitments(
            @Param("territory") Territory createdByTerritory,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN FETCH r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE u.role != :role " +
            "AND u.area = :area " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR r.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR r.createdAt <= :endDate) " +
            "AND (:territory IS NULL OR u.territory = :territory) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findAreaRecruitments(
            @Param("area") Area createdByArea,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN FETCH r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE (:zone IS NULL OR u.zone = :zone) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR r.createdAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR r.createdAt <= :endDate) " +
            "AND (:region IS NULL OR u.region = :region) " +
            "AND (:territory IS NULL OR u.territory = :territory) " +
            "AND (:area IS NULL OR u.area = :area) " +
            "AND (:status IS NULL OR rc.status = :status) " +
            "AND (:isFollowUp IS NULL OR rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findNationalRecruitments(
            @Param("zone") Zone zone,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("region") Region region,
            @Param("territory") Territory territory,
            @Param("area") Area area,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );

    List<Recruitment> findByCreatedBy(Users createdBy);
}


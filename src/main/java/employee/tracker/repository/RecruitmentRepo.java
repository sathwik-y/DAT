package employee.tracker.repository;

import employee.tracker.enums.*;
import employee.tracker.model.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecruitmentRepo extends JpaRepository<Recruitment,Long> {


    @Query("SELECT r FROM Recruitment r " +
            "JOIN r.recruitmentCalls rc " +
            "WHERE r.createdBy.zone=:zone " +
            "AND r.createdBy.role!=:role " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:territory is NULL or r.createdBy.territory= :territory)" +
            "AND (:region is NULL or r.createdBy.region = :region) " +
            "AND  (:area is NULL or r.createdBy.area = :area) " +
            "AND rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findZonalRecruitments(
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


    @Query("SELECT r FROM Recruitment r " +
            "JOIN r.recruitmentCalls rc " +
            "WHERE r.createdBy.role!=:role " +
            "AND r.createdBy.region = :region " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:territory is NULL or r.createdBy.territory= :territory)" +
            "AND  (:area is NULL or r.createdBy.area = :area) " +
            "AND rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findRegionalRecruitments(
            @Param("region") Region createdByRegion,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("area") String area,
            @Param("territory") String territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT r FROM Recruitment r " +
            "JOIN r.recruitmentCalls rc " +
            "WHERE r.createdBy.territory=:territory " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findTerritorialRecruitments(
            @Param("territory") Territory createdByTerritory,
//            Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT r FROM Recruitment r " +
            "JOIN r.recruitmentCalls rc " +
            "WHERE r.createdBy.role!=:role " +
            "AND r.createdBy.area= :area " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:territory is NULL or r.createdBy.territory= :territory)" +
            "AND rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findAreaRecruitments(
            @Param("area") String createdByArea,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("territory") String territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT r FROM Recruitment r " +
            "JOIN r.recruitmentCalls rc " +
            "WHERE (:zone is NULL OR r.createdBy.zone=:zone) " +
//            "AND r.createdBy.role!=:role " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:region is NULL or r.createdBy.region = :region) " +
            "AND (:territory is NULL or r.createdBy.territory= :territory)" +
            "AND  (:area is NULL or r.createdBy.area = :area) " +
            "AND rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findNationalRecruitments(
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


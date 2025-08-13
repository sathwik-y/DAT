package employee.tracker.repository;

import employee.tracker.enums.*;
import employee.tracker.model.Recruitment;
import employee.tracker.model.Users;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.spi.TransactionalWriter;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecruitmentRepo extends JpaRepository<Recruitment,Long> {


    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN FETCH r.createdBy u " +
            "JOIN FETCH r.recruitmentCalls rc " +
            "WHERE u.zone=:zone " +
            "AND u.role!=:role " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:territory is NULL or u.territory= :territory)" +
            "AND (:region is NULL or u.region = :region) " +
            "AND  (:area is NULL or u.area = :area) " +
            "AND rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findZonalRecruitments(
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


    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN FETCH r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE u.role!=:role " +
            "AND u.region = :region " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:territory is NULL or u.territory= :territory)" +
            "AND  (:area is NULL or u.area = :area) " +
            "AND rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findRegionalRecruitments(
            @Param("region") Region createdByRegion,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("area") Area area,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE u.territory=:territory " +
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


    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE u.role!=:role " +
            "AND u.area= :area " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:territory is NULL or u.territory= :territory)" +
            "AND rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findAreaRecruitments(
            @Param("area") Area createdByArea,
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("territory") Territory territory,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE (:zone is NULL OR r.createdBy.zone=:zone) " +
//            "AND r.createdBy.role!=:role " +
            "AND (:startDate is NULL OR r.createdAt>= :startDate)" +
            "AND (:endDate is NULL or r.createdAt<=:endDate)" +
            "AND (:region is NULL or u.region = :region) " +
            "AND (:territory is NULL or u.territory= :territory)" +
            "AND  (:area is NULL or u.area = :area) " +
            "AND rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r)" +
            "AND (:status is NULL or rc.status = :status) " +
            "AND (:isFollowUp is NULL or rc.isFollowUp = :isFollowUp)"
    )
    List<Recruitment> findNationalRecruitments(
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

    List<Recruitment> findByCreatedBy(Users createdBy);
}


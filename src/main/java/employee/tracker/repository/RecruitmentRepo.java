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
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecruitmentRepo extends JpaRepository<Recruitment,Long> {


    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN FETCH r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r) " +
            "AND u.zone = COALESCE(:zone, u.zone) " +
            "AND u.role != :role " +
            "AND r.createdAt >= COALESCE(:startDate, r.createdAt) " +
            "AND r.createdAt <= COALESCE(:endDate, r.createdAt) " +
            "AND u.region = COALESCE(:region, u.region) " +
            "AND u.territory = COALESCE(:territory, u.territory) " +
            "AND u.area = COALESCE(:area, u.area) " +
            "AND rc.status = COALESCE(:status, rc.status) " +
            "AND rc.isFollowUp = COALESCE(:isFollowUp, rc.isFollowUp)"
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
            "WHERE rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r) " +
            "AND u.role != :role " +
            "AND u.region = :region " +
            "AND r.createdAt >= COALESCE(:startDate, r.createdAt) " +
            "AND r.createdAt <= COALESCE(:endDate, r.createdAt) " +
            "AND u.territory = COALESCE(:territory, u.territory) " +
            "AND u.area = COALESCE(:area, u.area) " +
            "AND rc.status = COALESCE(:status, rc.status) " +
            "AND rc.isFollowUp = COALESCE(:isFollowUp, rc.isFollowUp)"
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
            "WHERE rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r) " +
            "AND u.territory = :territory " +
            "AND r.createdAt >= COALESCE(:startDate, r.createdAt) " +
            "AND r.createdAt <= COALESCE(:endDate, r.createdAt) " +
            "AND rc.status = COALESCE(:status, rc.status) " +
            "AND rc.isFollowUp = COALESCE(:isFollowUp, rc.isFollowUp)"
    )
    List<Recruitment> findTerritorialRecruitments(
            @Param("territory") Territory createdByTerritory,
//            Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") Status status,
            @Param("isFollowUp") Boolean isFollowUp
    );


    @Query("SELECT DISTINCT r FROM Recruitment r " +
            "JOIN FETCH r.recruitmentCalls rc " +
            "JOIN FETCH r.createdBy u " +
            "WHERE rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r) " +
            "AND u.role != :role " +
            "AND u.area = :area " +
            "AND r.createdAt >= COALESCE(:startDate, r.createdAt) " +
            "AND r.createdAt <= COALESCE(:endDate, r.createdAt) " +
            "AND u.territory = COALESCE(:territory, u.territory) " +
            "AND rc.status = COALESCE(:status, rc.status) " +
            "AND rc.isFollowUp = COALESCE(:isFollowUp, rc.isFollowUp)"
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
            "WHERE rc.createdAt = (SELECT MAX(rc2.createdAt) FROM RecruitmentCall rc2 WHERE rc2.recruitment = r) " +
            "AND u.zone = COALESCE(:zone, u.zone) " +
            "AND r.createdAt >= COALESCE(:startDate, r.createdAt) " +
            "AND r.createdAt <= COALESCE(:endDate, r.createdAt) " +
            "AND u.region = COALESCE(:region, u.region) " +
            "AND u.territory = COALESCE(:territory, u.territory) " +
            "AND u.area = COALESCE(:area, u.area) " +
            "AND rc.status = COALESCE(:status, rc.status) " +
            "AND rc.isFollowUp = COALESCE(:isFollowUp, rc.isFollowUp)"
    )
    List<Recruitment> findNationalRecruitments(
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

    List<Recruitment> findByCreatedBy(Users createdBy);
}


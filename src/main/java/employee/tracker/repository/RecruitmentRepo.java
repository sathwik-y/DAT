package employee.tracker.repository;

import employee.tracker.model.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentRepo extends JpaRepository<Recruitment,Long> {
}

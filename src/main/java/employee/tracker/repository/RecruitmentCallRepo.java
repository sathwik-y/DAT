package employee.tracker.repository;

import employee.tracker.model.RecruitmentCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentCallRepo extends JpaRepository<RecruitmentCall,Long> {
}

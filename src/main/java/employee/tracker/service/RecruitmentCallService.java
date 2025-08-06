package employee.tracker.service;

import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.model.Recruitment;
import employee.tracker.model.RecruitmentCall;
import employee.tracker.model.Users;
import employee.tracker.repository.RecruitmentCallRepo;
import employee.tracker.repository.RecruitmentRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentCallService {
    private final UsersRepo usersRepo;
    private final RecruitmentRepo recruitmentRepo;
    private final RecruitmentCallRepo recruitmentCallRepo;

    @Transactional
    public RecruitmentCall createRecruitmentCall(RecruitmentCall recruitmentCall, Long recruitmentId, String username) {
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

        Recruitment recruitment = recruitmentRepo.findById(recruitmentId)
                .orElseThrow(()-> new RuntimeException("Recruitment not found: "+ recruitmentId));

        recruitmentCall.setLoggedBy(user);
        recruitmentCall.setRecruitment(recruitment);

        RecruitmentCall savedCall = recruitmentCallRepo.save(recruitmentCall);
        recruitment.getRecruitmentCalls().add(savedCall);
        user.getRecruitmentCalls().add(savedCall);

        return savedCall;
    }


    public List<RecruitmentCall> getZonalRecruitmentCalls(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentCallRepo.findZonalRecruitmentCalls(
                user.getZone(),
                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getRegion(),
                filters.getTerritory(),
                filters.getArea(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );

    }

    public List<RecruitmentCall> getRegionalRecruitmentCalls(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentCallRepo.findRegionalRecruitmentCalls(
                user.getRegion(),
                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getArea(),
                filters.getTerritory(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<RecruitmentCall> getTerritorialRecruitmentCalls(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentCallRepo.findTerritorialRecruitmentCalls(
                user.getTerritory(),
//                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<RecruitmentCall> getAreaRecruitmentCalls(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentCallRepo.findAreaRecruitmentCalls(
                user.getArea(),
                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getTerritory(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<RecruitmentCall> getAllRecruitmentCalls(RecruitmentFilterDTO filters) {
        return recruitmentCallRepo.findNationalRecruitmentCalls(
                filters.getZone(),
//                "NH",
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getRegion(),
                filters.getTerritory(),
                filters.getArea(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }
}

package employee.tracker.service;

import employee.tracker.dto.NewRecruitmentDTO;
import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.model.Recruitment;
import employee.tracker.model.RecruitmentCall;
import employee.tracker.model.Users;
import employee.tracker.repository.RecruitmentRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepo recruitmentRepo;
    private final UsersRepo usersRepo;

    public Recruitment createNewRecruitment(NewRecruitmentDTO newRecruitmentDTO, String username) {
        Users user = usersRepo.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

            // Build the Recruitment object from the DTO
        Recruitment newRecruitment = Recruitment.builder()
                    .name(newRecruitmentDTO.getName())
                    .phoneNo(newRecruitmentDTO.getPhoneNo())
                    .gender(newRecruitmentDTO.getGender())
                    .age(newRecruitmentDTO.getAge())
                    .dob(newRecruitmentDTO.getDob())
                    .maritalStatus(newRecruitmentDTO.getMaritalStatus())
                    .occupation(newRecruitmentDTO.getOccupation())
                    .profession(newRecruitmentDTO.getProfession())
                    .annualIncome(newRecruitmentDTO.getAnnualIncome())
                    .isCompetition(newRecruitmentDTO.isCompetition())
                    .competingCompany(newRecruitmentDTO.getCompetingCompany())
                    .optedPosition(newRecruitmentDTO.getOptedPosition())
                    .referredBy(newRecruitmentDTO.getReferredBy())
                    .leadSources(newRecruitmentDTO.getLeadSources())
                    .createdBy(user)
                    .build();

            // Build the RecruitmentCall object from the DTO
        RecruitmentCall newRecruitmentCall = RecruitmentCall.builder()
                    .followUpDate(newRecruitmentDTO.getFollowUpDate())
                    .notes(newRecruitmentDTO.getNotes())
                    .status(newRecruitmentDTO.getStatus())
                    .isFollowUp(false)
                    .loggedBy(user)
                    .recruitment(newRecruitment)
                    .build();

        newRecruitment.setRecruitmentCalls(new ArrayList<>(List.of(newRecruitmentCall)));
        Recruitment savedRecruitment = recruitmentRepo.save(newRecruitment);

        if(user.getRecruitments()==null) user.setRecruitments(new ArrayList<>());
        user.getRecruitments().add(savedRecruitment);

        if(user.getRecruitmentCalls()==null) user.setRecruitmentCalls(new ArrayList<>());
        user.getRecruitmentCalls().add(savedRecruitment.getRecruitmentCalls().getFirst());
        return savedRecruitment;
        }

    public List<Recruitment> getZonalRecruitments(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentRepo.findZonalRecruitments(
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

    public List<Recruitment> getRegionalRecruitments(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentRepo.findRegionalRecruitments(
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

    public List<Recruitment> getTerritorialRecruitments(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentRepo.findTerritorialRecruitments(
                user.getTerritory(),
//                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<Recruitment> getAreaRecruitments(String username, RecruitmentFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentRepo.findAreaRecruitments(
                user.getArea(),
                user.getRole(),
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getTerritory(),
                filters.getStatus(),
                filters.getIsFollowUp()
        );
    }

    public List<Recruitment> getAllRecruitments(RecruitmentFilterDTO filters) {
        return recruitmentRepo.findNationalRecruitments(
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

    public List<Recruitment> findMyRecruitment(String username) {
        Users user = usersRepo.findByUserName(username);
        return recruitmentRepo.findByCreatedBy(user);
    }
}


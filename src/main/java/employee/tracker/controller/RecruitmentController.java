package employee.tracker.controller;

import employee.tracker.dto.NewRecruitmentDTO;
import employee.tracker.model.Recruitment;
import employee.tracker.model.RecruitmentCall;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitment")
public class RecruitmentController {
    @PostMapping("/new-call")
    public ResponseEntity<Recruitment> createNewRecruitment(@RequestBody NewRecruitmentDTO newRecruitmentDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            // Set the new Recruitment entity
            Recruitment newRecruiment = Recruitment.builder()
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
                    .build();

            // Set the new Recruitment Call entity
            RecruitmentCall newRecruitmentCall = RecruitmentCall.builder()
                    .followUpDate(newRecruitmentDTO.getFollowUpDate())
                    .notes(newRecruitmentDTO.getNotes())
                    .status(newRecruitmentDTO.getStatus())
                    .isFollowUp(false)
                    .build();

            newRecruiment.setRecruitmentCalls(List.of(newRecruitmentCall));
            return new ResponseEntity<>(newRecruiment,HttpStatus.CREATED);

        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
/*

    @PostMapping("/new-call")
    public ResponseEntity<Sales> createNewSale(@RequestBody NewSalesDTO newSalesDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{

            // Set the new sales entity
            Sales newSale = Sales.builder()
                    .name(newSalesDTO.getName())
                    .phoneNo(newSalesDTO.getPhoneNo())
                    .annualIncome(newSalesDTO.getAnnualIncome())
                    .gender(newSalesDTO.getGender())
                    .age(newSalesDTO.getAge())
                    .dob(newSalesDTO.getDob())
                    .maritalStatus(newSalesDTO.getMaritalStatus())
                    .occupation(newSalesDTO.getOccupation())
                    .product(productRepo.getReferenceById(newSalesDTO.getProductId()))
                    .build();

            // Set the new Sales Call entity
            SalesCall newSalesCall = SalesCall.builder()
                    .followUpDate(newSalesDTO.getFollowUpDate())
                    .notes(newSalesDTO.getNotes())
                    .status(newSalesDTO.getStatus())
                    .isFollowUp(false)
                    .build();

            newSale.setSalesCalls(List.of(newSalesCall)); // this will always create a new list as this is a new call
            newSalesCall.setSale(newSale);

            Sales savedSale = salesService.createNewSale(newSale,username);
            salesCallService.createNewCall(newSalesCall,username);
            return new ResponseEntity<>(savedSale,HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
 */
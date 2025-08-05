package employee.tracker.controller;

import employee.tracker.dto.NewRecruitmentDTO;
import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.model.Recruitment;
import employee.tracker.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;
    @PostMapping("/new-call")
    public ResponseEntity<Recruitment> createNewRecruitment(@RequestBody NewRecruitmentDTO newRecruitmentDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            Recruitment savedRecruitment = recruitmentService.createNewRecruitment(newRecruitmentDTO,username);
            return new ResponseEntity<>(savedRecruitment,HttpStatus.CREATED);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("hasRole('ZH')")
    @GetMapping("/zone")
    public ResponseEntity<List<Recruitment>> getAllRecruitmentsByZone(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Recruitment> allZonalRecruitments =  recruitmentService.getZonalRecruitments(username,filters);
            return new ResponseEntity<>(allZonalRecruitments,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('RH','ARH')")
    @GetMapping("/regional")
    public ResponseEntity<List<Recruitment>> getAllRecruitmentsByRegion(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Recruitment> allRegionalRecruitment = recruitmentService.getRegionalRecruitments(username,filters);
            return new ResponseEntity<>(allRegionalRecruitment,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('TM')")
    @GetMapping("/territorial")
    public ResponseEntity<List<Recruitment>> getAllRecruitmentsByTerritory(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Recruitment> allTerritorialRecruitments = recruitmentService.getTerritorialRecruitments(username,filters);
            return new ResponseEntity<>(allTerritorialRecruitments,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('AM')")
    @GetMapping("/area")
    public ResponseEntity<List<Recruitment>> getAllRecruitmentsByArea(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Recruitment> allAreaRecruitments = recruitmentService.getAreaRecruitments(username,filters);
            return new ResponseEntity<>(allAreaRecruitments,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('NH')")
    @GetMapping("/all")
    public ResponseEntity<List<Recruitment>> getAllRecruitments(@RequestBody RecruitmentFilterDTO filters){
        try{
            List<Recruitment> allRecruitments = recruitmentService.getAllRecruitments(filters);
            return new ResponseEntity<>(allRecruitments,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

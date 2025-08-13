package employee.tracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import employee.tracker.dto.NewRecruitmentDTO;
import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.model.Recruitment;
import employee.tracker.service.RecruitmentService;
import lombok.RequiredArgsConstructor;

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
    @PostMapping("/zone")
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
    @PostMapping("/regional")
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
    @PostMapping("/territorial")
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
    @PostMapping("/area")
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
    @PostMapping("/all")
    public ResponseEntity<List<Recruitment>> getAllRecruitments(@RequestBody RecruitmentFilterDTO filters){
        try{
            List<Recruitment> allRecruitments = recruitmentService.getAllRecruitments(filters);
            return new ResponseEntity<>(allRecruitments,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getMyRecruitments")
    public ResponseEntity<List<Recruitment>> findMyRecruitments(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Recruitment> recruitments = recruitmentService.findMyRecruitment(username);
            return new ResponseEntity<>(recruitments,HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

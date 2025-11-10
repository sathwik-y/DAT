package employee.tracker.controller;

import java.util.List;

import employee.tracker.model.RecruitmentCall;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
@Slf4j
public class RecruitmentController {
    public final RecruitmentService recruitmentService;
    @PostMapping("/new-call")
    public ResponseEntity<Recruitment> createNewRecruitment(@RequestBody NewRecruitmentDTO newRecruitmentDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            Recruitment savedRecruitment = recruitmentService.createNewRecruitment(newRecruitmentDTO,username);
            log.info("User : {} logged a new Recruitment",username);
            log.debug("User : {} logged a new Recruitment: {}",username,savedRecruitment);
            return new ResponseEntity<>(savedRecruitment,HttpStatus.CREATED);
        }catch(Exception e){
            log.error("Error occurred for user : {} | Message: {}",username, ExceptionUtils.getRootCauseMessage(e));
            log.debug("Trace for User {} : ",username,e);
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
            logFetchRecruitment("ZH",username,allZonalRecruitments,null);
            return new ResponseEntity<>(allZonalRecruitments,HttpStatus.OK);
        }catch(Exception e){
            logFetchRecruitment("ZH",username,null,e);
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
            logFetchRecruitment("RH/ARH",username,allRegionalRecruitment,null);
            return new ResponseEntity<>(allRegionalRecruitment,HttpStatus.OK);
        }catch(Exception e){
            logFetchRecruitment("RH/ARH",username,null,e);
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
            logFetchRecruitment("TM",username,allTerritorialRecruitments,null);
            return new ResponseEntity<>(allTerritorialRecruitments,HttpStatus.OK);
        }catch(Exception e){
            logFetchRecruitment("TM",username,null,e);
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
            logFetchRecruitment("AM",username,allAreaRecruitments,null);
            return new ResponseEntity<>(allAreaRecruitments,HttpStatus.OK);
        }catch(Exception e){
            logFetchRecruitment("AM",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('NH')")
    @PostMapping("/all")
    public ResponseEntity<List<Recruitment>> getAllRecruitments(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Recruitment> allRecruitments = recruitmentService.getAllRecruitments(filters);
            logFetchRecruitment("NH",username,allRecruitments,null);
            return new ResponseEntity<>(allRecruitments,HttpStatus.OK);
        } catch (Exception e) {
            logFetchRecruitment("NH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getMyRecruitments")
    public ResponseEntity<List<Recruitment>> findMyRecruitments(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Recruitment> recruitments = recruitmentService.findMyRecruitments(username);
            logFetchRecruitment("AM/TM",username,recruitments,null);
            return new ResponseEntity<>(recruitments,HttpStatus.OK);
        }catch(Exception e){
            logFetchRecruitment("AM/TM",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private void logFetchRecruitment(String role, String username, List<Recruitment> calls, Exception e){
        if(e==null){
            log.info("[{}] Recruitments fetched for user: {}",role,username);
            log.debug("[{}] Recruitments fetched for user: {} -> {}",role,username, calls);
        }else{
            log.error("[{}] Failed to fetch Recruitments for user: {} | Message: {}",role,username, ExceptionUtils.getRootCauseMessage(e));
            log.debug("[{}] Trace for the User {}",role,username,e);
        }
    }
}

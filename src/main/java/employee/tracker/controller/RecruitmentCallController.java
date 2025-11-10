package employee.tracker.controller;


import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.model.RecruitmentCall;
import employee.tracker.service.RecruitmentCallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitment/calls")
@RequiredArgsConstructor
@Slf4j
public class RecruitmentCallController {

    public final RecruitmentCallService recruitmentCallService;

    @PostMapping("/add")
    public ResponseEntity<RecruitmentCall> addNewRecruitmentCall(@RequestBody RecruitmentCall recruitmentCall, @RequestParam Long recruitmentId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            RecruitmentCall newCall = recruitmentCallService.createRecruitmentCall(recruitmentCall,recruitmentId,username);
            log.info("User : {} logged a new Recruitment Call",username);
            log.debug("User : {} logged a new Recruitment Call: {}",username,newCall);
            return new ResponseEntity<>(newCall, HttpStatus.CREATED);
        }catch(Exception e){
            log.error("Error occurred for user : {} | Message: {}",username, ExceptionUtils.getRootCauseMessage(e));
            log.debug("Trace for User {} : ",username,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasRole('ZH')")
    @PostMapping("/zone")
    public ResponseEntity<List<RecruitmentCall>> getAllRecruitmentCallsByZone(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<RecruitmentCall> allZonalRecruitmentCalls =  recruitmentCallService.getZonalRecruitmentCalls(username,filters);
            logFetchRecruitmentCall("ZH",username,allZonalRecruitmentCalls,null);
            return new ResponseEntity<>(allZonalRecruitmentCalls, HttpStatus.OK);
        }catch(Exception e){
            logFetchRecruitmentCall("ZH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('RH','ARH')")
    @PostMapping("/regional")
    public ResponseEntity<List<RecruitmentCall>> getAllRecruitmentCallsByRegion(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<RecruitmentCall> allRegionalRecruitmentCalls = recruitmentCallService.getRegionalRecruitmentCalls(username,filters);
            logFetchRecruitmentCall("RH/ARH",username,allRegionalRecruitmentCalls,null);
            return new ResponseEntity<>(allRegionalRecruitmentCalls,HttpStatus.OK);
        }catch(Exception e){
            logFetchRecruitmentCall("RH/ARH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('TM')")
    @PostMapping("/territorial")
    public ResponseEntity<List<RecruitmentCall>> getAllRecruitmentCallsByTerritory(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<RecruitmentCall> allTerritorialRecruitmentCalls = recruitmentCallService.getTerritorialRecruitmentCalls(username,filters);
            logFetchRecruitmentCall("TM",username,allTerritorialRecruitmentCalls,null);
            return new ResponseEntity<>(allTerritorialRecruitmentCalls,HttpStatus.OK);
        }catch(Exception e){
            logFetchRecruitmentCall("TM",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('AM')")
    @PostMapping("/area")
    public ResponseEntity<List<RecruitmentCall>> getAllRecruitmentCallsByArea(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<RecruitmentCall> allAreaRecruitmentCalls = recruitmentCallService.getAreaRecruitmentCalls(username,filters);
            logFetchRecruitmentCall("AM",username,allAreaRecruitmentCalls,null);
            return new ResponseEntity<>(allAreaRecruitmentCalls,HttpStatus.OK);
        }catch(Exception e){
            logFetchRecruitmentCall("AM",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('NH')")
    @PostMapping("/all")
    public ResponseEntity<List<RecruitmentCall>> getAllRecruitmentCalls(@RequestBody RecruitmentFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<RecruitmentCall> allRecruitmentCalls = recruitmentCallService.getAllRecruitmentCalls(filters);
            logFetchRecruitmentCall("NH",username,allRecruitmentCalls,null);
            return new ResponseEntity<>(allRecruitmentCalls,HttpStatus.OK);
        } catch (Exception e) {
            logFetchRecruitmentCall("NH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



    // Logging Methods
    private void logFetchRecruitmentCall(String role,String username,List<RecruitmentCall> calls, Exception e){
        if(e==null){
            log.info("[{}] Recruitment calls fetched for user: {}",role,username);
            log.debug("[{}] Recruitment calls fetched for user: {} -> {}",role,username, calls);
        }else{
            log.error("[{}] Failed to fetch Recruitment calls for user: {} | Message: {}",role,username,ExceptionUtils.getRootCauseMessage(e));
            log.debug("[{}] Trace for the User {}",role,username,e);
        }
    }

}

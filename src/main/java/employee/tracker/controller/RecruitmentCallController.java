package employee.tracker.controller;


import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.model.RecruitmentCall;
import employee.tracker.service.RecruitmentCallService;
import lombok.RequiredArgsConstructor;
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
public class RecruitmentCallController {

    private final RecruitmentCallService recruitmentCallService;

    @PostMapping("/add")
    public ResponseEntity<RecruitmentCall> addNewRecruitmentCall(@RequestBody RecruitmentCall recruitmentCall, @RequestParam Long recruitmentId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            RecruitmentCall newCall = recruitmentCallService.createRecruitmentCall(recruitmentCall,recruitmentId,username);
            return new ResponseEntity<>(newCall, HttpStatus.CREATED);
        }catch(Exception e){
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
            return new ResponseEntity<>(allZonalRecruitmentCalls, HttpStatus.OK);
        }catch(Exception e){
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
            return new ResponseEntity<>(allRegionalRecruitmentCalls,HttpStatus.OK);
        }catch(Exception e){
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
            return new ResponseEntity<>(allTerritorialRecruitmentCalls,HttpStatus.OK);
        }catch(Exception e){
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
            return new ResponseEntity<>(allAreaRecruitmentCalls,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('NH')")
    @PostMapping("/all")
    public ResponseEntity<List<RecruitmentCall>> getAllRecruitmentCalls(@RequestBody RecruitmentFilterDTO filters){
        try{
            List<RecruitmentCall> allRecruitmentCalls = recruitmentCallService.getAllRecruitmentCalls(filters);
            return new ResponseEntity<>(allRecruitmentCalls,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

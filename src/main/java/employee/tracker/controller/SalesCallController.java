package employee.tracker.controller;

import java.util.List;

import employee.tracker.model.RecruitmentCall;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.weaver.ast.Call;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.model.SalesCall;
import employee.tracker.service.SalesCallService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sales/calls")
@RequiredArgsConstructor
@Slf4j
public class SalesCallController {
    public final SalesCallService salesCallService;

    // New Sales call creation has already been handles with SalesController and is already present in SalesCallService
    @PostMapping("/add")
    public ResponseEntity<SalesCall> addNewSalesCall(@RequestBody SalesCall salesCall,@RequestParam Long saleId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            SalesCall newCall = salesCallService.createSalesCall(salesCall,saleId,username);
            log.info("User : {} logged a new Sales Call",username);
            log.debug("User : {} logged a new Sales Call: {}",username,newCall);
            return new ResponseEntity<>(newCall,HttpStatus.CREATED);
        }catch(Exception e){
            log.error("Error occurred for user : {} | Message: {}",username, ExceptionUtils.getRootCauseMessage(e));
            log.debug("Trace for User {} : ",username,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasRole('ZH')")
    @PostMapping("/zone")
    public ResponseEntity<List<SalesCall>> getAllSalesCallsByZone(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<SalesCall> allZonalSalesCalls =  salesCallService.getZonalSalesCalls(username,filters);
            logFetchSalesCall("ZH",username,allZonalSalesCalls,null);
            return new ResponseEntity<>(allZonalSalesCalls, HttpStatus.OK);
        }catch(Exception e){
            logFetchSalesCall("ZH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('RH','ARH')")
    @PostMapping("/regional")
    public ResponseEntity<List<SalesCall>> getAllSalesCallsByRegion(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<SalesCall> allRegionalSalesCalls = salesCallService.getRegionalSalesCalls(username,filters);
            logFetchSalesCall("RH/ARH",username,allRegionalSalesCalls,null);
            return new ResponseEntity<>(allRegionalSalesCalls,HttpStatus.OK);
        }catch(Exception e){
            logFetchSalesCall("RH/ARH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('TM')")
    @PostMapping("/territorial")
    public ResponseEntity<List<SalesCall>> getAllSalesCallByTerritory(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<SalesCall> allTerritorialSalesCalls = salesCallService.getTerritorialSalesCalls(username,filters);
            logFetchSalesCall("TM",username,allTerritorialSalesCalls,null);
            return new ResponseEntity<>(allTerritorialSalesCalls,HttpStatus.OK);
        }catch(Exception e){
            logFetchSalesCall("TM",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('AM')")
    @PostMapping("/area")
    public ResponseEntity<List<SalesCall>> getAllSalesCallsByArea(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<SalesCall> allAreaSalesCalls = salesCallService.getAreaSalesCalls(username,filters);
            logFetchSalesCall("AM",username,allAreaSalesCalls,null);
            return new ResponseEntity<>(allAreaSalesCalls,HttpStatus.OK);
        }catch(Exception e){
            logFetchSalesCall("AM",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('NH')")
    @PostMapping("/all")
    public ResponseEntity<List<SalesCall>> getAllSales(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<SalesCall> allSalesCalls = salesCallService.getAllSalesCalls(filters);
            logFetchSalesCall("NH",username,allSalesCalls,null);
            return new ResponseEntity<>(allSalesCalls,HttpStatus.OK);
        } catch (Exception e) {
            logFetchSalesCall("NH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private void logFetchSalesCall(String role, String username, List<SalesCall> calls, Exception e){
        if(e==null){
            log.info("[{}] Sales calls fetched for user: {}",role,username);
            log.debug("[{}] Sales calls fetched for user: {} -> {}",role,username, calls);
        }else{
            log.error("[{}] Failed to fetch Sales calls for user: {} | Message: {}",role,username,ExceptionUtils.getRootCauseMessage(e));
            log.debug("[{}] Trace for the User {}",role,username,e);
        }
    }
}

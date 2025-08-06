package employee.tracker.controller;

import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.model.SalesCall;
import employee.tracker.service.SalesCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales/calls")
@RequiredArgsConstructor
public class SalesCallController {
    private final SalesCallService salesCallService;

    // New Sales call creation has already been handles with SalesController and is already present in SalesCallService

    @PostMapping("/add")
    public ResponseEntity<SalesCall> addNewSalesCall(@RequestBody SalesCall salesCall,@RequestParam Long saleId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            SalesCall newCall = salesCallService.createSalesCall(salesCall,saleId,username);
            return new ResponseEntity<>(newCall,HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasRole('ZH')")
    @GetMapping("/zone")
    public ResponseEntity<List<SalesCall>> getAllSalesCallsByZone(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<SalesCall> allZonalSalesCalls =  salesCallService.getZonalSalesCalls(username,filters);
            return new ResponseEntity<>(allZonalSalesCalls, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('RH','ARH')")
    @GetMapping("/regional")
    public ResponseEntity<List<SalesCall>> getAllSalesCallsByRegion(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<SalesCall> allRegionalSalesCalls = salesCallService.getRegionalSalesCalls(username,filters);
            return new ResponseEntity<>(allRegionalSalesCalls,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('TM')")
    @GetMapping("/territorial")
    public ResponseEntity<List<SalesCall>> getAllSalesCallByTerritory(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<SalesCall> allTerritorialSalesCalls = salesCallService.getTerritorialSalesCalls(username,filters);
            return new ResponseEntity<>(allTerritorialSalesCalls,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('AM')")
    @GetMapping("/area")
    public ResponseEntity<List<SalesCall>> getAllSalesCallsByArea(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<SalesCall> allAreaSalesCalls = salesCallService.getAreaSalesCalls(username,filters);
            return new ResponseEntity<>(allAreaSalesCalls,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('NH')")
    @GetMapping("/all")
    public ResponseEntity<List<SalesCall>> getAllSales(@RequestBody SalesFilterDTO filters){
        try{
            List<SalesCall> allSalesCalls = salesCallService.getAllSalesCalls(filters);
            return new ResponseEntity<>(allSalesCalls,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // TODO: Get sales call By user
}

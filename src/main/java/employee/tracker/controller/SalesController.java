package employee.tracker.controller;

import java.util.List;

import employee.tracker.model.Recruitment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import employee.tracker.dto.NewSalesDTO;
import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.model.Sales;
import employee.tracker.service.SalesService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Slf4j
public class SalesController{
    public final SalesService salesService;

    @PostMapping("/new-call")
    public ResponseEntity<Sales> createNewSale(@RequestBody NewSalesDTO newSalesDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try{
            Sales savedSale = salesService.createNewSale(newSalesDTO,username);
            log.info("User : {} logged a new Sale",username);
            log.debug("User : {} logged a new Sale: {}",username,savedSale);
            return new ResponseEntity<>(savedSale,HttpStatus.CREATED);
        }catch(Exception e){
            log.error("Error occurred for user : {} | Message: {}",username, ExceptionUtils.getRootCauseMessage(e));
            log.debug("Trace for User {} : ",username,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // This to get all the sales of the logged-in user
    @PreAuthorize("hasRole('ZH')")
    @PostMapping("/zone")
    public ResponseEntity<List<Sales>> getAllSalesByZone(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Sales> allZonalSales =  salesService.getZonalSales(username,filters);
            logFetchSales("ZH",username,allZonalSales,null);
            return new ResponseEntity<>(allZonalSales,HttpStatus.OK);
        }catch(Exception e){
            logFetchSales("ZH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('RH','ARH')")
    @PostMapping("/regional")
    public ResponseEntity<List<Sales>> getAllSalesByRegion(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Sales> allRegionalSales = salesService.getRegionalSales(username,filters);
            logFetchSales("RH/ARH",username,allRegionalSales,null);
            return new ResponseEntity<>(allRegionalSales,HttpStatus.OK);
        }catch(Exception e){
            logFetchSales("RH/ARH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('TM')")
    @PostMapping("/territorial")
    public ResponseEntity<List<Sales>> getAllSalesByTerritory(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Sales> allTerritorialSales = salesService.getTerritorialSales(username,filters);
            logFetchSales("TM",username,allTerritorialSales,null);
            return new ResponseEntity<>(allTerritorialSales,HttpStatus.OK);
        }catch(Exception e){
            logFetchSales("TM",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('AM')")
    @PostMapping("/area")
    public ResponseEntity<List<Sales>> getAllSalesByArea(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Sales> allAreaSales = salesService.getAreaSales(username,filters);
            logFetchSales("TM",username,allAreaSales,null);
            return new ResponseEntity<>(allAreaSales,HttpStatus.OK);
        }catch(Exception e){
            logFetchSales("TM",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('NH')")
    @PostMapping("/all")
    public ResponseEntity<List<Sales>> getAllSales(@RequestBody SalesFilterDTO filters){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Sales> allSales = salesService.getAllSales(filters);
            logFetchSales("NH",username,allSales,null);
            return new ResponseEntity<>(allSales,HttpStatus.OK);
        } catch (Exception e) {
            logFetchSales("NH",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getMySales")
    public ResponseEntity<List<Sales>> findMySales(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Sales> sale = salesService.findMySales(username);
            logFetchSales("AM/TM",username,sale,null);
            return new ResponseEntity<>(sale,HttpStatus.OK);
        }catch(Exception e){
            logFetchSales("AM/TM",username,null,e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    private void logFetchSales(String role, String username, List<Sales> calls, Exception e){
        if(e==null){
            log.info("[{}] Sales fetched for user: {}",role,username);
            log.debug("[{}] Sales calls fetched for user: {} -> {}",role,username, calls);
        }else{
            log.error("[{}] Failed to fetch Recruitments for user: {} | Message: {}",role,username, ExceptionUtils.getRootCauseMessage(e));
            log.debug("[{}] Trace for the User {}",role,username,e);
        }
    }
}

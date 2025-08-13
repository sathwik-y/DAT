package employee.tracker.controller;

import java.util.List;

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
public class SalesController{
    private final SalesService salesService;

    @PostMapping("/new-call")
    public ResponseEntity<Sales> createNewSale(@RequestBody NewSalesDTO newSalesDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try{
            Sales savedSale = salesService.createNewSale(newSalesDTO,username);
            return new ResponseEntity<>(savedSale,HttpStatus.CREATED);
        }catch(Exception e){
            e.printStackTrace();
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
            return new ResponseEntity<>(allZonalSales,HttpStatus.OK);
        }catch(Exception e){
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
            return new ResponseEntity<>(allRegionalSales,HttpStatus.OK);
        }catch(Exception e){
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
            return new ResponseEntity<>(allTerritorialSales,HttpStatus.OK);
        }catch(Exception e){
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
            return new ResponseEntity<>(allAreaSales,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('NH')")
    @PostMapping("/all")
    public ResponseEntity<List<Sales>> getAllSales(@RequestBody SalesFilterDTO filters){
        try{
            List<Sales> allSales = salesService.getAllSales(filters);
            return new ResponseEntity<>(allSales,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //TODO: Get sales by user

    @GetMapping("/getMySales")
    public ResponseEntity<List<Sales>> findMySales(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try{
            List<Sales> sale = salesService.findMySales(username);
            return new ResponseEntity<>(sale,HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

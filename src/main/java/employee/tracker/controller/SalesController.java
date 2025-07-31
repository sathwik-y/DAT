package employee.tracker.controller;

import employee.tracker.dto.NewSalesDTO;
import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.model.Sales;
import employee.tracker.model.SalesCall;
import employee.tracker.repository.ProductRepo;
import employee.tracker.service.SalesCallService;
import employee.tracker.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController{
    private final SalesService salesService;
    private final SalesCallService salesCallService;
    private final ProductRepo productRepo;

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

    // This to get all the sales of the logged-in user
    @PreAuthorize("hasRole('ZH')")
    @GetMapping("/zone")
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
    @GetMapping("/regional")
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
    @GetMapping("/territorial")
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
    @GetMapping("/area")
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
    @GetMapping("/all")
    public ResponseEntity<List<Sales>> getAllSales(@RequestBody SalesFilterDTO filters){
        try{
            List<Sales> allSales = salesService.getAllSales(filters);
            return new ResponseEntity<>(allSales,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //TODO: Get sales by user
}

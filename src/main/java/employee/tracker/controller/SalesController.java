package employee.tracker.controller;

import employee.tracker.dto.NewSalesDTO;
import employee.tracker.model.Sales;
import employee.tracker.model.SalesCall;
import employee.tracker.repository.ProductRepo;
import employee.tracker.service.SalesCallService;
import employee.tracker.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        // TODO: need to get the username and pass it when the JWT is implemented
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
//                    .product(productRepo.getReferenceById(newSalesDTO.getProductId()))
                    // .createdBy(username) TODO: Replace it with the actual username when implemented
                    .build();

            // Set the new Sales Call entity
            SalesCall newSalesCall = SalesCall.builder()
                    .followUpDate(newSalesDTO.getFollowUpDate())
                    .notes(newSalesDTO.getNotes())
                    .status(newSalesDTO.getStatus())
                    .isFollowUp(false)
                    .build();

            newSale.setSalesCalls(List.of(newSalesCall));
            newSalesCall.setSale(newSale);

            salesService.createNewSale(newSale);
            salesCallService.createNewCall(newSalesCall);
            return new ResponseEntity<>(newSale, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

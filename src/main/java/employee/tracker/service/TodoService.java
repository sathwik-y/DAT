package employee.tracker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import employee.tracker.dto.RecruitmentFilterDTO;
import employee.tracker.dto.SalesFilterDTO;
import employee.tracker.dto.TodoDTO;
import employee.tracker.dto.TodoFilterDTO;
import employee.tracker.model.Recruitment;
import employee.tracker.model.Sales;
import employee.tracker.model.Users;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final SalesService salesService;
    private final RecruitmentService recruitmentService;
    private final UsersRepo usersRepo;

    @Transactional(readOnly = true)
    @Cacheable(value="todoData", key="#username + '-' + #filters.hashCode()")
    public List<TodoDTO> getTodoList(String username, TodoFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        System.out.println("TodoService: getTodoList - user: " + username + ", role: " + user.getRole());

        List<TodoDTO> todoList = new ArrayList<>();
        
        // Get sales todo items if needed
        if (!filters.hasCallType() || "SALES".equals(filters.getCallType())) {
            List<Sales> salesList = getSalesByRole(user, filters);
            List<TodoDTO> salesTodos = extractSalesTodos(salesList, filters);
            todoList.addAll(salesTodos);
        }
        
        // Get recruitment todo items if needed
        if (!filters.hasCallType() || "RECRUITMENT".equals(filters.getCallType())) {
            List<Recruitment> recruitmentList = getRecruitmentsByRole(user, filters);
            List<TodoDTO> recruitmentTodos = extractRecruitmentTodos(recruitmentList, filters);
            todoList.addAll(recruitmentTodos);
        }
        
        // Sort by follow-up date
        todoList.sort((a, b) -> a.getFollowUpDate().compareTo(b.getFollowUpDate()));
        
        System.out.println("TodoService: Total todo items found: " + todoList.size());
        
        return todoList;
    }

    private List<Sales> getSalesByRole(Users user, TodoFilterDTO filters) {
        // Convert TodoFilterDTO to SalesFilterDTO
        SalesFilterDTO salesFilter = SalesFilterDTO.builder()
            .startDate(filters.getStartDate())
            .endDate(filters.getEndDate())
            .zone(filters.hasZone() ? filters.getZone().name() : null)
            .region(filters.hasRegion() ? filters.getRegion().name() : null)
            .territory(filters.hasTerritory() ? filters.getTerritory().name() : null)
            .area(filters.hasArea() ? filters.getArea().name() : null)
            .status(filters.hasStatus() ? filters.getStatus().name() : null)
            .build();

        try {
            switch (user.getRole()) {
                case NH: // National Head sees ALL zones
                    return salesService.getAllSales(salesFilter);
                    
                case ZH: // Zonal Head sees their zone only
                    return salesService.getZonalSales(user.getUserName(), salesFilter);
                    
                case RH:
                case ARH: // Regional Head sees their region only
                    return salesService.getRegionalSales(user.getUserName(), salesFilter);
                    
                case AM: // Area Manager sees their area only
                    return salesService.getAreaSales(user.getUserName(), salesFilter);
                    
                case TM: // Territory Manager sees their territory only
                    return salesService.getTerritorialSales(user.getUserName(), salesFilter);
                    
                default: // Individual users see only their own data
                    return salesService.findMySales(user.getUserName());
            }
        } catch (Exception e) {
            System.err.println("Error getting sales for user " + user.getUserName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Recruitment> getRecruitmentsByRole(Users user, TodoFilterDTO filters) {
        // Convert TodoFilterDTO to RecruitmentFilterDTO
        RecruitmentFilterDTO recruitmentFilter = RecruitmentFilterDTO.builder()
            .startDate(filters.getStartDate())
            .endDate(filters.getEndDate())
            .zone(filters.hasZone() ? filters.getZone().name() : null)
            .region(filters.hasRegion() ? filters.getRegion().name() : null)
            .territory(filters.hasTerritory() ? filters.getTerritory().name() : null)
            .area(filters.hasArea() ? filters.getArea().name() : null)
            .status(filters.hasStatus() ? filters.getStatus().name() : null)
            .build();

        try {
            switch (user.getRole()) {
                case NH: // National Head sees ALL zones
                    return recruitmentService.getAllRecruitments(recruitmentFilter);
                    
                case ZH: // Zonal Head sees their zone only
                    return recruitmentService.getZonalRecruitments(user.getUserName(), recruitmentFilter);
                    
                case RH:
                case ARH: // Regional Head sees their region only
                    return recruitmentService.getRegionalRecruitments(user.getUserName(), recruitmentFilter);
                    
                case AM: // Area Manager sees their area only
                    return recruitmentService.getAreaRecruitments(user.getUserName(), recruitmentFilter);
                    
                case TM: // Territory Manager sees their territory only
                    return recruitmentService.getTerritorialRecruitments(user.getUserName(), recruitmentFilter);
                    
                default: // Individual users see only their own data
                    return recruitmentService.findMyRecruitments(user.getUserName());
            }
        } catch (Exception e) {
            System.err.println("Error getting recruitments for user " + user.getUserName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<TodoDTO> extractSalesTodos(List<Sales> salesList, TodoFilterDTO filters) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get date range from filters
        LocalDateTime startDate = filters.getStartDate() != null ? filters.getStartDate() : now;
        LocalDateTime endDate = filters.getEndDate() != null ? filters.getEndDate() : now.plusYears(10);
        
        return salesList.stream()
            .flatMap(sales -> {
                // Force initialization of lazy collections inside transaction
                sales.getSalesCalls().size(); // This triggers lazy loading
                return sales.getSalesCalls().stream();
            })
            .filter(sc -> sc.getFollowUpDate() != null) // Must have follow-up date
            .filter(sc -> sc.getFollowUpDate().isAfter(now)) // Future follow-ups only
            // ADD THIS: Filter by date range from filters
            .filter(sc -> !sc.getFollowUpDate().isBefore(startDate) && !sc.getFollowUpDate().isAfter(endDate))
            .filter(sc -> !filters.hasStatus() || sc.getStatus() == filters.getStatus())
            .filter(sc -> !filters.hasRegion() || sc.getLoggedBy().getRegion() == filters.getRegion())
            .map(sc -> {
                // Ensure all data is loaded before mapping
                TodoDTO dto = TodoDTO.builder()
                    .callId(sc.getId())
                    .callType("SALES")
                    .followUpDate(sc.getFollowUpDate())
                    .loggedByName(sc.getLoggedBy().getName())
                    .notes(sc.getNotes())
                    .contactName(sc.getSale().getName())
                    .contactPhone(sc.getSale().getPhoneNo())
                    .status(sc.getStatus())
                    .region(sc.getLoggedBy().getRegion())
                    .build();
                return dto;
            })
            .collect(Collectors.toList());
    }

    private List<TodoDTO> extractRecruitmentTodos(List<Recruitment> recruitmentList, TodoFilterDTO filters) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get date range from filters
        LocalDateTime startDate = filters.getStartDate() != null ? filters.getStartDate() : now;
        LocalDateTime endDate = filters.getEndDate() != null ? filters.getEndDate() : now.plusYears(10);
        
        return recruitmentList.stream()
            .flatMap(recruitment -> {
                // Force initialization of lazy collections inside transaction
                recruitment.getRecruitmentCalls().size(); // This triggers lazy loading
                return recruitment.getRecruitmentCalls().stream();
            })
            .filter(rc -> rc.getFollowUpDate() != null) // Must have follow-up date
            .filter(rc -> rc.getFollowUpDate().isAfter(now)) // Future follow-ups only
            // ADD THIS: Filter by date range from filters
            .filter(rc -> !rc.getFollowUpDate().isBefore(startDate) && !rc.getFollowUpDate().isAfter(endDate))
            .filter(rc -> !filters.hasStatus() || rc.getStatus() == filters.getStatus())
            .filter(rc -> !filters.hasRegion() || rc.getLoggedBy().getRegion() == filters.getRegion())
            .map(rc -> {
                // Ensure all data is loaded before mapping
                TodoDTO dto = TodoDTO.builder()
                    .callId(rc.getId())
                    .callType("RECRUITMENT")
                    .followUpDate(rc.getFollowUpDate())
                    .loggedByName(rc.getLoggedBy().getName())
                    .notes(rc.getNotes())
                    .contactName(rc.getRecruitment().getName())
                    .contactPhone(rc.getRecruitment().getPhoneNo())
                    .status(rc.getStatus())
                    .region(rc.getLoggedBy().getRegion())
                    .build();
                return dto;
            })
            .collect(Collectors.toList());
    }
}
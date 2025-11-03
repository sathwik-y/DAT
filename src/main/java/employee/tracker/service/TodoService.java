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
        System.out.println("TodoService: Date filters - startDate: " + filters.getStartDate() + ", endDate: " + filters.getEndDate());

        List<TodoDTO> todoList = new ArrayList<>();
        
        // Don't pass date filters to sales/recruitment services
        // We'll filter by followUpDate later
        TodoFilterDTO serviceFilters = TodoFilterDTO.builder()
            .zone(filters.getZone())
            .region(filters.getRegion())
            .territory(filters.getTerritory())
            .area(filters.getArea())
            .status(filters.getStatus())
            .callType(filters.getCallType())
            .build();
        
        // Get sales todo items if needed
        if (!filters.hasCallType() || "SALES".equals(filters.getCallType())) {
            List<Sales> salesList = getSalesByRole(user, serviceFilters);
            List<TodoDTO> salesTodos = extractSalesTodos(salesList, filters);
            todoList.addAll(salesTodos);
        }
        
        // Get recruitment todo items if needed
        if (!filters.hasCallType() || "RECRUITMENT".equals(filters.getCallType())) {
            List<Recruitment> recruitmentList = getRecruitmentsByRole(user, serviceFilters);
            List<TodoDTO> recruitmentTodos = extractRecruitmentTodos(recruitmentList, filters);
            todoList.addAll(recruitmentTodos);
        }
        
        // Sort by follow-up date
        todoList.sort((a, b) -> a.getFollowUpDate().compareTo(b.getFollowUpDate()));
        
        System.out.println("TodoService: Total todo items found: " + todoList.size());
        
        return todoList;
    }

    private List<Sales> getSalesByRole(Users user, TodoFilterDTO filters) {
        SalesFilterDTO salesFilter = SalesFilterDTO.builder()
            .zone(filters.hasZone() ? filters.getZone().name() : null)
            .region(filters.hasRegion() ? filters.getRegion().name() : null)
            .territory(filters.hasTerritory() ? filters.getTerritory().name() : null)
            .area(filters.hasArea() ? filters.getArea().name() : null)
            .status(filters.hasStatus() ? filters.getStatus().name() : null)
            .build();

        try {
            switch (user.getRole()) {
                case NH:
                    return salesService.getAllSales(salesFilter);
                case ZH:
                    return salesService.getZonalSales(user.getUserName(), salesFilter);
                case RH:
                case ARH:
                    return salesService.getRegionalSales(user.getUserName(), salesFilter);
                case AM:
                    return salesService.getAreaSales(user.getUserName(), salesFilter);
                case TM:
                    return salesService.getTerritorialSales(user.getUserName(), salesFilter);
                default:
                    return salesService.findMySales(user.getUserName());
            }
        } catch (Exception e) {
            System.err.println("Error getting sales for user " + user.getUserName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Recruitment> getRecruitmentsByRole(Users user, TodoFilterDTO filters) {
        RecruitmentFilterDTO recruitmentFilter = RecruitmentFilterDTO.builder()
            .zone(filters.hasZone() ? filters.getZone().name() : null)
            .region(filters.hasRegion() ? filters.getRegion().name() : null)
            .territory(filters.hasTerritory() ? filters.getTerritory().name() : null)
            .area(filters.hasArea() ? filters.getArea().name() : null)
            .status(filters.hasStatus() ? filters.getStatus().name() : null)
            .build();

        try {
            switch (user.getRole()) {
                case NH:
                    return recruitmentService.getAllRecruitments(recruitmentFilter);
                case ZH:
                    return recruitmentService.getZonalRecruitments(user.getUserName(), recruitmentFilter);
                case RH:
                case ARH:
                    return recruitmentService.getRegionalRecruitments(user.getUserName(), recruitmentFilter);
                case AM:
                    return recruitmentService.getAreaRecruitments(user.getUserName(), recruitmentFilter);
                case TM:
                    return recruitmentService.getTerritorialRecruitments(user.getUserName(), recruitmentFilter);
                default:
                    return recruitmentService.findMyRecruitments(user.getUserName());
            }
        } catch (Exception e) {
            System.err.println("Error getting recruitments for user " + user.getUserName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<TodoDTO> extractSalesTodos(List<Sales> salesList, TodoFilterDTO filters) {
        // REMOVED: LocalDateTime now = LocalDateTime.now();
        
        LocalDateTime startDate = filters.getStartDate();
        LocalDateTime endDate = filters.getEndDate();
        
        System.out.println("TodoService: Extracting sales todos with followUpDate range: " + startDate + " to " + endDate);
        
        return salesList.stream()
            .flatMap(sales -> {
                sales.getSalesCalls().size(); // Trigger lazy loading
                return sales.getSalesCalls().stream();
            })
            .filter(sc -> sc.getFollowUpDate() != null) // Must have follow-up date
            // REMOVED: .filter(sc -> sc.getFollowUpDate().isAfter(now)) // Show ALL follow-ups, including past
            // Filter by followUpDate within date range
            .filter(sc -> {
                boolean inRange = true;
                if (startDate != null) {
                    inRange = inRange && !sc.getFollowUpDate().isBefore(startDate);
                }
                if (endDate != null) {
                    inRange = inRange && !sc.getFollowUpDate().isAfter(endDate);
                }
                System.out.println("TodoService: Sales call " + sc.getId() + " followUpDate: " + sc.getFollowUpDate() + " inRange: " + inRange);
                return inRange;
            })
            .filter(sc -> !filters.hasStatus() || sc.getStatus() == filters.getStatus())
            .filter(sc -> !filters.hasRegion() || sc.getLoggedBy().getRegion() == filters.getRegion())
            .map(sc -> TodoDTO.builder()
                .callId(sc.getId())
                .callType("SALES")
                .followUpDate(sc.getFollowUpDate())
                .loggedByName(sc.getLoggedBy().getName())
                .notes(sc.getNotes())
                .contactName(sc.getSale().getName())
                .contactPhone(sc.getSale().getPhoneNo())
                .status(sc.getStatus())
                .region(sc.getLoggedBy().getRegion())
                .build())
            .collect(Collectors.toList());
    }

    private List<TodoDTO> extractRecruitmentTodos(List<Recruitment> recruitmentList, TodoFilterDTO filters) {
        // REMOVED: LocalDateTime now = LocalDateTime.now();
        
        LocalDateTime startDate = filters.getStartDate();
        LocalDateTime endDate = filters.getEndDate();
        
        System.out.println("TodoService: Extracting recruitment todos with followUpDate range: " + startDate + " to " + endDate);
        
        return recruitmentList.stream()
            .flatMap(recruitment -> {
                recruitment.getRecruitmentCalls().size(); // Trigger lazy loading
                return recruitment.getRecruitmentCalls().stream();
            })
            .filter(rc -> rc.getFollowUpDate() != null) // Must have follow-up date
            // REMOVED: .filter(rc -> rc.getFollowUpDate().isAfter(now)) // Show ALL follow-ups, including past
            // Filter by followUpDate within date range
            .filter(rc -> {
                boolean inRange = true;
                if (startDate != null) {
                    inRange = inRange && !rc.getFollowUpDate().isBefore(startDate);
                }
                if (endDate != null) {
                    inRange = inRange && !rc.getFollowUpDate().isAfter(endDate);
                }
                System.out.println("TodoService: Recruitment call " + rc.getId() + " followUpDate: " + rc.getFollowUpDate() + " inRange: " + inRange);
                return inRange;
            })
            .filter(rc -> !filters.hasStatus() || rc.getStatus() == filters.getStatus())
            .filter(rc -> !filters.hasRegion() || rc.getLoggedBy().getRegion() == filters.getRegion())
            .map(rc -> TodoDTO.builder()
                .callId(rc.getId())
                .callType("RECRUITMENT")
                .followUpDate(rc.getFollowUpDate())
                .loggedByName(rc.getLoggedBy().getName())
                .notes(rc.getNotes())
                .contactName(rc.getRecruitment().getName())
                .contactPhone(rc.getRecruitment().getPhoneNo())
                .status(rc.getStatus())
                .region(rc.getLoggedBy().getRegion())
                .build())
            .collect(Collectors.toList());
    }
}
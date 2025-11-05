package employee.tracker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import employee.tracker.dto.TodoDTO;
import employee.tracker.dto.TodoFilterDTO;
import employee.tracker.model.RecruitmentCall;
import employee.tracker.model.SalesCall;
import employee.tracker.model.Users;
import employee.tracker.repository.RecruitmentCallRepo;
import employee.tracker.repository.SalesCallRepo;
import employee.tracker.repository.UsersRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final SalesCallRepo salesCallRepo;
    private final RecruitmentCallRepo recruitmentCallRepo;
    private final UsersRepo usersRepo;

    @Transactional(readOnly = true)
    @Cacheable(value="todoData", key="#username + '-' + #filters.hashCode()")
    public List<TodoDTO> getTodoList(String username, TodoFilterDTO filters) {
        Users user = usersRepo.findByUserName(username);
        
        if (user == null) {
            System.err.println("TodoService: User not found: " + username);
            return new ArrayList<>();
        }
        
        LocalDateTime startDate = filters.getStartDate();
        LocalDateTime endDate = filters.getEndDate();
        
        System.out.println("TodoService: getTodoList - user: " + username);
        System.out.println("TodoService: Date range: " + startDate + " to " + endDate);

        List<TodoDTO> todoList = new ArrayList<>();
        
        // Get sales todo items if needed
        if (!filters.hasCallType() || "SALES".equals(filters.getCallType())) {
            List<SalesCall> salesCalls = salesCallRepo.findFollowUpsByUserAndDateRange(
                username, startDate, endDate
            );
            System.out.println("TodoService: Found " + salesCalls.size() + " sales calls for " + username);
            
            List<TodoDTO> salesTodos = salesCalls.stream()
                .filter(sc -> !filters.hasStatus() || sc.getStatus() == filters.getStatus())
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
            
            todoList.addAll(salesTodos);
        }
        
        // Get recruitment todo items if needed
        if (!filters.hasCallType() || "RECRUITMENT".equals(filters.getCallType())) {
            List<RecruitmentCall> recruitmentCalls = recruitmentCallRepo.findFollowUpsByUserAndDateRange(
                username, startDate, endDate
            );
            System.out.println("TodoService: Found " + recruitmentCalls.size() + " recruitment calls for " + username);
            
            List<TodoDTO> recruitmentTodos = recruitmentCalls.stream()  
                .filter(rc -> !filters.hasStatus() || rc.getStatus() == filters.getStatus())
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
            
            todoList.addAll(recruitmentTodos);
        }
        
        // Sort by follow-up date (earliest first)
        todoList.sort((a, b) -> a.getFollowUpDate().compareTo(b.getFollowUpDate()));
        
        System.out.println("TodoService: Total todo items: " + todoList.size());
        
        return todoList;
    }
}
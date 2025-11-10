package employee.tracker.controller;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import employee.tracker.dto.TodoDTO;
import employee.tracker.dto.TodoFilterDTO;
import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Status;
import employee.tracker.enums.Territory;
import employee.tracker.service.TodoService;
import employee.tracker.utility.JwtUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    private final TodoService todoService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<TodoDTO>> getTodoList(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String targetUser, // NEW: specific user to get todos for
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Region region,
            @RequestParam(required = false) Area area,
            @RequestParam(required = false) Territory territory,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String callType) {
        
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            
            // Use targetUser if provided, otherwise use logged-in user
            String userToQuery = (targetUser != null && !targetUser.isEmpty()) ? targetUser : username;

            log.info("Requesting user: {}, target user: {}, startDate: {}, endDate: {}", username, userToQuery, startDate, endDate);

            TodoFilterDTO filters = TodoFilterDTO.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .region(region)
                    .area(area)
                    .territory(territory)
                    .status(status)
                    .callType(callType)
                    .build();

            List<TodoDTO> todoList = todoService.getTodoList(userToQuery, filters);
            log.info("Requesting user: {}, target user: {}, startDate: {}, endDate: {}", username, userToQuery, startDate, endDate);
            log.debug("Requesting user: {}, Data returned: {}",username,todoList);
            return ResponseEntity.ok(todoList);

        } catch (Exception e) {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            log.info("Error fetching Todo List for the user: {} | Message: {}",username, ExceptionUtils.getRootCauseMessage(e));
            log.debug("Trace for the user: {}",username,e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
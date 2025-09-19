package employee.tracker.controller;

import java.time.LocalDateTime;
import java.util.List;

import employee.tracker.enums.Area;
import employee.tracker.enums.Region;
import employee.tracker.enums.Status;
import employee.tracker.enums.Territory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import employee.tracker.dto.TodoDTO;
import employee.tracker.dto.TodoFilterDTO;
import employee.tracker.service.TodoService;
import employee.tracker.utility.JwtUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<TodoDTO>> getTodoList(
            @RequestHeader("Authorization") String authHeader,
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
            
            System.out.println("TodoController: getTodoList - user: " + username + 
                              ", startDate: " + startDate + ", endDate: " + endDate);

            TodoFilterDTO filters = TodoFilterDTO.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .region(region)
                    .area(area)
                    .territory(territory)
                    .status(status)
                    .callType(callType)
                    .build();

            List<TodoDTO> todoList = todoService.getTodoList(username, filters);
            return ResponseEntity.ok(todoList);

        } catch (Exception e) {
            System.err.println("TodoController: Error getting todo list: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
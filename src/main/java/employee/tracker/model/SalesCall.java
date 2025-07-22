package employee.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class SalesCall {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date createdAt;
    private Date closedAt;

    private boolean isFollowUp;
    private Date followUpData;

    @ManyToOne
    @JoinColumn(name="sale_id")
    private Sales sale;
    private String notes;
    private String status; //will become an enum
    @ManyToOne
    private User loggedBy;

}

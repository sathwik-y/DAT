package employee.tracker.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private BigDecimal value;
    private LocalDateTime launchedAt;
    private LocalDateTime stoppedAt;

    private String note;
    // TODO: Can map each product to the customers who purchased this product

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<Sales> sale;
}

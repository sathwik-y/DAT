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
    public Long id;

    public String name;
    public String type;
    public BigDecimal value;
    public LocalDateTime launchedAt;
    public LocalDateTime stoppedAt;

    public String note;
    // TODO: Can map each product to the customers who purchased this product

    @OneToMany(mappedBy = "product")
    @JsonManagedReference("product-sales")
    public List<Sales> sale;
}

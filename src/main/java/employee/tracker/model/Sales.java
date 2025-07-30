package employee.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Sales {
    // This contains information about the clients who are reached out for sales call, Meaning for the product they are buying.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phoneNo;
    private String gender;
    private int age;
    private LocalDateTime dob;
    private String maritalStatus;
    private String occupation;
    private BigDecimal annualIncome;
    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name="product_id")
    private Products product; // Need to ask
    // Can be kept because one customer can purchase many products. Might have to remove it if each new sales call is a different entry.

    @ManyToOne
    @JoinColumn(name="created_by_id") // set nullable= false later
    @JsonIgnoreProperties({"sales", "recruitments"})
    private Users createdBy;

    @OneToMany(mappedBy = "sale")
    @JsonIgnoreProperties({"sale"})
    private List<SalesCall> salesCalls;


    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }

//    @PreUpdate
//    protected void onUpdate(){
//        this.updatedAt = LocalDateTime.now();
//    }
}


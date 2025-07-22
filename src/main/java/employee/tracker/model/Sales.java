package employee.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Sales {
    // This contains information about the clients who are reached out for sales call, Meaning for the product they are buying.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String phoneNo;
    private String Gender;
    private int age;
    private Date dob;
    private String maritalStatus;
    private String occupation;
    private int annualIncome;
    @OneToMany
    private List<Products> products;
    // Can be kept because one customer can purchase many products. Might have to remove it if each new sales call is a different entry.

//    private Date followUp;
//    private String Note;

    @ManyToOne
    @JoinColumn(name="created_by_id",nullable = false)
    @JsonIgnoreProperties({"sales", "recruitments"})
    private User createdBy;

    @OneToMany(mappedBy = "sale")
    private List<SalesCall> salesCalls;
}

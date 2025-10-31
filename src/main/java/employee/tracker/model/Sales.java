package employee.tracker.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sales {
    // This contains information about the clients who are reached out for sales call, Meaning for the product they are buying.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public String phoneNo;
    public String gender;
    public int age;
    public LocalDateTime dob;
    public String maritalStatus;
    public String occupation;
    public BigDecimal annualIncome;
    public LocalDateTime createdAt;
//    public LocalDateTime updatedAt;
//    @ManyToOne
//    @JoinColumn(name="product_id")
//    @JsonBackReference("product-sales")
//    public Products product;
    // Need to ask
    // Can be kept because one customer can purchase many products. Might have to remove it if each new sales call is a different entry.

    public String product;
    @ManyToOne
    @JoinColumn(name="created_by_id") // set nullable= false later
    @JsonBackReference("user-sales")
    public Users createdBy;

// Add these fields to the Sales class

        @Column(name = "im_name")
        private String imName;

        @Column(name = "area_name")
        private String areaName;

// Add getters and setters (if not using Lombok)
    @OneToMany(mappedBy = "sale", cascade = CascadeType.PERSIST)
    @JsonManagedReference("sale-salesCall")
    public List<SalesCall> salesCalls;

    //TODO: Add Premium Pitched
    // TODO: Referred by
    public BigDecimal premium;
    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }

    //  TODO: Sale should have an updated at whenever a new salescall for this sale is added
//    @PreUpdate
//    protected void onUpdate(){
//        this.updatedAt = LocalDateTime.now();
//    }

    @Transient
    @JsonProperty("createdByName")
    public String getCreatedByName() {
        return createdBy != null ? createdBy.getName() : null;
    }

}


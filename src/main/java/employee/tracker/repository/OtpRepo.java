package employee.tracker.repository;


import employee.tracker.model.Otp;
import employee.tracker.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepo extends JpaRepository<Otp, Long> {
    Optional<Otp> findByUserName(String userName);

// FIXED VERSION - parameter names match
    @Query("DELETE FROM Otp o WHERE o.userName = :userName")
    @Modifying
    void deleteByUserName(@Param("userName") String userName);
    Otp findByUserAndOtp(Users user, String otp);

}

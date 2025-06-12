package com.flight.repository;

import com.flight.entity.UserPassenger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface UserPassengerRepository extends JpaRepository<UserPassenger, Long> {
    List<UserPassenger> findByUserId(Long userId);
    
    Optional<UserPassenger> findByUserIdAndIsDefaultTrue(Long userId);
    
    @Modifying
    @Query("UPDATE UserPassenger p SET p.isDefault = false WHERE p.user.id = :userId AND p.id != :passengerId")
    void unsetDefaultForOthers(Long userId, Long passengerId);
    
    boolean existsByUserIdAndId(Long userId, Long passengerId);

    @Modifying
    @Query("DELETE FROM UserPassenger p WHERE p.user.id = :userId")
    void deleteByUserId(Long userId);
}

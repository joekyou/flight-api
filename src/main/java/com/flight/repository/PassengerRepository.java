package com.flight.repository;

import com.flight.entity.Passenger;
import com.flight.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * 乘客数据访问接口
 */
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    /**
     * 查找用户的所有乘客
     */
    List<Passenger> findByUser(User user);

    /**
     * 查找用户的特定乘客
     */
    Optional<Passenger> findByIdAndUser(Long id, User user);

    /**
     * 统计用户的乘客数量
     */
    long countByUser(User user);

    /**
     * 查找用户的其他乘客（排除指定ID的乘客）
     */
    List<Passenger> findByUserAndIdNot(User user, Long id);

    /**
     * 清除用户的默认乘客设置
     */
    @Modifying
    @Query("UPDATE Passenger p SET p.isDefault = false WHERE p.user.id = :userId AND p.isDefault = true")
    void clearDefaultPassenger(@Param("userId") Long userId);

    /**
     * 查找用户的默认乘客
     */
    Optional<Passenger> findByUserAndIsDefaultTrue(User user);
}

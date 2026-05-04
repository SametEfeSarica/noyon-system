package com.noyon.system.repository;

import com.noyon.system.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long userId);

    // DÜZELTME: isDeleted -> deleted yapıldı
    List<Subscription> findByUserIdAndDeletedFalse(Long userId);
    List<Subscription> findByUserIdAndDeletedTrue(Long userId);

    // DÜZELTME: Query içinde s.isDeleted yerine s.deleted kullanıldı
    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.user.id = :userId AND s.deleted = false")
    Double getTotalSubscriptionCostByUserId(@Param("userId") Long userId);
}
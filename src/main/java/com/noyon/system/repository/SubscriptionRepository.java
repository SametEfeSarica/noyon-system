package com.noyon.system.repository;

import com.noyon.system.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Kullanıcının tüm aboneliklerini liste olarak getirir
    List<Subscription> findByUserId(Long userId);

    // Dashboard için: Kullanıcının tüm aktif aboneliklerinin toplam ücretini hesaplar
    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.user.id = :userId")
    Double getTotalSubscriptionCostByUserId(@Param("userId") Long userId);
}
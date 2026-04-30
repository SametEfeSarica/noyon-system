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

    // --- SOFT DELETE İÇİN YENİ EKLENENLER ---
    List<Subscription> findByUserIdAndIsDeletedFalse(Long userId); // Sadece aktifler
    List<Subscription> findByUserIdAndIsDeletedTrue(Long userId);  // Sadece çöpteki abonelikler

    // Dashboard için: Kullanıcının sadece SİLİNMEMİŞ aboneliklerinin toplam ücretini hesaplar
    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.user.id = :userId AND s.isDeleted = false")
    Double getTotalSubscriptionCostByUserId(@Param("userId") Long userId);
}
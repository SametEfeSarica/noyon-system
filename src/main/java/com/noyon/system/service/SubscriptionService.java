package com.noyon.system.service;

import com.noyon.system.entity.Subscription;
import com.noyon.system.entity.User;
import com.noyon.system.repository.SubscriptionRepository;
import com.noyon.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Subscription addSubscription(Subscription subscription, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Hata: " + userId + " ID'li kullanıcı bulunamadı!"));
        subscription.setUser(user);
        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> getSubscriptionsByUserId(Long userId) {
        // Repository'deki yeni isme uyarlandı
        return subscriptionRepository.findByUserIdAndDeletedFalse(userId);
    }

    public List<Subscription> getTrashedSubscriptionsByUserId(Long userId) {
        // Repository'deki yeni isme uyarlandı
        return subscriptionRepository.findByUserIdAndDeletedTrue(userId);
    }

    @Transactional
    public void deleteSubscription(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abonelik bulunamadı!"));
        sub.setDeleted(true);
        subscriptionRepository.save(sub);
    }

    @Transactional
    public void restoreSubscription(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abonelik bulunamadı!"));
        sub.setDeleted(false);
        subscriptionRepository.save(sub);
    }

    // Aboneliği veritabanından tamamen yok eder
    public void permanentDelete(Long id) {
        subscriptionRepository.deleteById(id);
    }
}
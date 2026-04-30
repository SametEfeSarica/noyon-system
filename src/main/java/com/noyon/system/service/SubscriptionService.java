package com.noyon.system.service;

import com.noyon.system.entity.Subscription;
import com.noyon.system.entity.User;
import com.noyon.system.repository.SubscriptionRepository;
import com.noyon.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    public Subscription addSubscription(Subscription subscription, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Hata: " + userId + " ID'li kullanıcı bulunamadı!"));
        subscription.setUser(user);
        return subscriptionRepository.save(subscription);
    }

    // Sadece aktif (silinmemiş) abonelikleri listele
    public List<Subscription> getSubscriptionsByUserId(Long userId) {
        return subscriptionRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    // Çöp kutusundaki abonelikleri getir
    public List<Subscription> getTrashedSubscriptionsByUserId(Long userId) {
        return subscriptionRepository.findByUserIdAndIsDeletedTrue(userId);
    }

    // FİZİKSEL SİLME YERİNE isDeleted = true YAPIYORUZ
    public void deleteSubscription(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abonelik bulunamadı!"));
        sub.setDeleted(true);
        subscriptionRepository.save(sub);
    }

    // Çöpten Geri Yükle
    public void restoreSubscription(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abonelik bulunamadı!"));
        sub.setDeleted(false);
        subscriptionRepository.save(sub);
    }
}
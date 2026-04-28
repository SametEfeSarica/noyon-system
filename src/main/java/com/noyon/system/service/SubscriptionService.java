package com.noyon.system.service;

import com.noyon.system.entity.Subscription;
import com.noyon.system.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // Abonelik kaydetme ve güncelleme
    public Subscription saveSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    // Kullanıcıya göre abonelikleri getirme
    public List<Subscription> getSubscriptionsByUser(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    // Abonelik silme
    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }
}
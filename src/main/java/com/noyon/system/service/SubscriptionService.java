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
    private UserRepository userRepository; // Talha'nın kullanıcı deposunu buraya bağladık.

    // Yeni abonelik ekleme (Kullanıcı ID'si ile birlikte)
    public Subscription addSubscription(Subscription subscription, Long userId) {
        // 1. Önce veritabanında bu ID'ye sahip bir kullanıcı var mı diye bakıyoruz.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Hata: " + userId + " ID'li kullanıcı bulunamadı!"));

        // 2. Kullanıcıyı bulduysak, aboneliğin içine yerleştiriyoruz.
        subscription.setUser(user);

        // 3. Şimdi her şeyiyle hazır olan aboneliği kaydediyoruz.
        return subscriptionRepository.save(subscription);
    }

    // Kullanıcıya göre abonelikleri listeleme
    public List<Subscription> getSubscriptionsByUserId(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    // Abonelik silme
    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }
}
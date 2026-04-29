package com.noyon.system.controller;

import com.noyon.system.entity.Subscription;
import com.noyon.system.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*") // Bu çok kritik! Emrah'ın bağlanmasını sağlar.
@Tag(name = "Abonelik Yönetimi")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Operation(summary = "Belirli bir kullanıcıya yeni abonelik ekler")
    @PostMapping("/add/{userId}")
    public ResponseEntity<Subscription> add(@RequestBody Subscription subscription, @PathVariable Long userId) {
        // Service katmanına hem aboneliği hem de kullanıcı ID'sini gönderiyoruz.
        return ResponseEntity.ok(subscriptionService.addSubscription(subscription, userId));
    }

    @Operation(summary = "Kullanıcı ID'sine göre tüm abonelikleri getirir")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUserId(userId));
    }

    @Operation(summary = "ID bazlı abonelik siler")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok("Abonelik başarıyla silindi.");
    }
}
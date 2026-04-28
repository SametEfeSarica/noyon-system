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
@CrossOrigin(origins = "*")
@Tag(name = "Abonelik Yönetimi")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Operation(summary = "Yeni abonelik ekler")
    @PostMapping("/add")
    public ResponseEntity<Subscription> addSubscription(@RequestBody Subscription subscription) {
        return ResponseEntity.ok(subscriptionService.saveSubscription(subscription));
    }

    @Operation(summary = "Kullanıcı ID'sine göre tüm abonelikleri getirir")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUser(userId));
    }

    @Operation(summary = "Abonelik siler")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok("Abonelik başarıyla silindi.");
    }
}
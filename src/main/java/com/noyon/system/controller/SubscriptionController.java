package com.noyon.system.controller;

import com.noyon.system.entity.Subscription;
import com.noyon.system.repository.UserRepository;
import com.noyon.system.response.ApiResponse;
import com.noyon.system.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Abonelik Yönetimi")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    @Data
    static class SubscriptionRequest {
        @NotBlank(message = "Platform adı zorunludur.")
        private String platformName;

        @DecimalMin(value = "0.0", message = "Tutar 0'dan büyük olmalıdır.")
        private Double amount;

        @Min(1) @Max(31)
        private Integer renewalDay;

        private String currency;
        private String category;
    }

    private Long resolveUserId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername()).orElseThrow().getId();
    }

    @Operation(summary = "Tüm aktif abonelikleri getir")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Subscription>>> getAll(
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(ApiResponse.ok("Abonelikler getirildi.",
                subscriptionService.getSubscriptionsByUserId(userId)));
    }

    @Operation(summary = "Yeni abonelik ekle")
    @PostMapping
    public ResponseEntity<ApiResponse<Subscription>> add(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody SubscriptionRequest request) {
        Long userId = resolveUserId(principal);

        Subscription sub = new Subscription();
        sub.setPlatformName(request.getPlatformName());
        sub.setAmount(request.getAmount());
        sub.setRenewalDay(request.getRenewalDay());
        sub.setCurrency(request.getCurrency() != null ? request.getCurrency() : "TRY");
        sub.setCategory(request.getCategory());

        Subscription saved = subscriptionService.addSubscription(sub, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Abonelik eklendi.", saved));
    }

    @Operation(summary = "Abonelik sil (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok(ApiResponse.ok("Abonelik silindi."));
    }

    @Operation(summary = "Çöp kutusundaki abonelikleri listele")
    @GetMapping("/trash")
    public ResponseEntity<ApiResponse<List<Subscription>>> getTrash(
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(ApiResponse.ok("Çöp kutusu getirildi.",
                subscriptionService.getTrashedSubscriptionsByUserId(userId)));
    }

    @Operation(summary = "Aboneliği çöp kutusundan geri yükle")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        subscriptionService.restoreSubscription(id);
        return ResponseEntity.ok(ApiResponse.ok("Abonelik geri yüklendi."));
    }
}
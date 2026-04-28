package com.noyon.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Data // Getter, Setter ve ToString'i otomatik ekler
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String platformName; // Netflix, Spotify vb.

    private LocalDate renewalDate; // Yenilenme tarihi

    private Double amount; // Ödenen para miktarı

    private String currency; // TRY, USD, EUR

    private Long userId; // Bu abonelik hangi kullanıcıya ait?
}
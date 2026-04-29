package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subscriptions")
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String platformName; // Örn: Netflix
    private Double amount;       // Örn: 159.90
    private Integer renewalDate; // Örn: 15 (Ayın kaçında yenileniyor?)
    private String currency;     // Örn: TRY

    @ManyToOne(fetch = FetchType.LAZY) // Birçok abonelik bir kullanıcıya bağlanabilir.
    @JoinColumn(name = "user_id")      // Veritabanında otomatik user_id sütunu açar.
    @JsonIgnore // Emrah veri çekerken sonsuz döngü olmasın diye burayı görmezden gelir.
    private User user;
}
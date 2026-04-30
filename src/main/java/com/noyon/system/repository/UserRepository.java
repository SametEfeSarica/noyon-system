package com.noyon.system.repository;

import com.noyon.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Giriş için e-posta sorgusu
    Optional<User> findByEmail(String email);

    // Kayıt kontrolleri
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
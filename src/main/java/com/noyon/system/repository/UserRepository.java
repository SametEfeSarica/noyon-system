package com.noyon.system.repository;

import com.noyon.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // E-posta adresine göre kullanıcıyı bulmak için (Login işlemi için lazım)
    Optional<User> findByEmail(String email);

    // Bu e-posta ile daha önce kayıt yapılmış mı kontrol etmek için
    boolean existsByEmail(String email);
}
package com.noyon.system.service;

import com.noyon.system.entity.User;
import com.noyon.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Kullanıcı Kaydı
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda!");
        }
        // Şifre güvenliği için normalde burada BCrypt kullanılır
        // Şimdilik temel yapıyı kuruyoruz
        return userRepository.save(user);
    }

    // Giriş Kontrolü
    public User loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt.get();
        } else {
            throw new RuntimeException("E-posta veya şifre hatalı!");
        }
    }
}
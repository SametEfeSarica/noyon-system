package com.noyon.system.service;

import com.noyon.system.entity.User;
import com.noyon.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // E-postayı küçük harfe çevir ve boşlukları temizle
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setUsername(user.getUsername().trim());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda!");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış!");
        }
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        String cleanEmail = email.trim().toLowerCase();
        String cleanPassword = password.trim();

        System.out.println("DEBUG: [" + cleanEmail + "] adresi ile giriş deneniyor.");

        return userRepository.findByEmail(cleanEmail)
                .filter(user -> {
                    boolean isMatch = user.getPassword().equals(cleanPassword);
                    if (!isMatch) {
                        System.out.println("DEBUG: Şifre hatalı! Gelen: [" + cleanPassword + "]");
                    }
                    return isMatch;
                })
                .orElseThrow(() -> {
                    System.out.println("DEBUG: E-posta bulunamadı!");
                    return new RuntimeException("E-posta veya şifre hatalı!");
                });
    }
}
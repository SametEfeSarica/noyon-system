package com.noyon.system.service;

import com.noyon.system.entity.User;
import com.noyon.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Kullanıcı Kaydı
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda!");
        }
        return userRepository.save(user);
    }

    // Kullanıcı Girişi (Username ve Password ile)
    public User loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Kullanıcı adı veya şifre hatalı!"));
    }
}
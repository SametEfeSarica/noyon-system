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
        // Boşlukları temizle (Giriş hatalarını önlemek için)
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());

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
        // Gelen veride boşluk varsa temizle
        String cleanUsername = username.trim();
        String cleanPassword = password.trim();

        System.out.println("DEBUG: Giriş denemesi başlatıldı.");
        System.out.println("DEBUG: Aranan Kullanıcı: [" + cleanUsername + "]");

        return userRepository.findByUsername(cleanUsername)
                .filter(user -> {
                    boolean isMatch = user.getPassword().equals(cleanPassword);
                    if (isMatch) {
                        System.out.println("DEBUG: Şifre eşleşti. Giriş başarılı.");
                    } else {
                        System.out.println("DEBUG: Kullanıcı bulundu ama ŞİFRE YANLIŞ!");
                        System.out.println("DEBUG: Gelen: [" + cleanPassword + "] | Kayıtlı: [" + user.getPassword() + "]");
                    }
                    return isMatch;
                })
                .orElseThrow(() -> {
                    System.out.println("DEBUG: HATA - [" + cleanUsername + "] adına sahip bir kullanıcı veritabanında yok!");
                    return new RuntimeException("Kullanıcı adı veya şifre hatalı!");
                });
    }
}
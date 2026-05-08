// service/UserCategoryService.java
package com.noyon.system.service;

import com.noyon.system.dto.UserCategoryDto;
import com.noyon.system.entity.User;
import com.noyon.system.entity.UserCategory;
import com.noyon.system.repository.UserCategoryRepository;
import com.noyon.system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCategoryService {

    private final UserCategoryRepository repo;
    private final UserRepository userRepository;

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Kullanıcı bulunamadı: " + userId));
    }

    private UserCategoryDto toDto(UserCategory c) {
        return new UserCategoryDto(c.getId(), c.getCategoryId(), c.getLabel(), c.getColor());
    }

    @Transactional(readOnly = true)
    public List<UserCategoryDto> getAll(Long userId) {
        return repo.findByUserId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public UserCategoryDto create(Long userId, UserCategoryDto dto) {
        User user = findUser(userId);
        UserCategory entity = UserCategory.builder()
                .categoryId(dto.getCategoryId())
                .label(dto.getLabel())
                .color(dto.getColor())
                .user(user)
                .build();
        return toDto(repo.save(entity));
    }

    @Transactional
    public UserCategoryDto update(Long id, UserCategoryDto dto) {
        UserCategory entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kategori bulunamadı: " + id));
        entity.setLabel(dto.getLabel());
        entity.setColor(dto.getColor());
        return toDto(repo.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    /**
     * Frontend'den gelen tam listeyi (saveAll) kaydeder.
     * Mevcut kategorileri silip yeniden yazar — basit ve güvenli.
     */
    @Transactional
    public List<UserCategoryDto> saveAll(Long userId, List<UserCategoryDto> dtos) {
        repo.findByUserId(userId).forEach(c -> repo.deleteById(c.getId()));
        User user = findUser(userId);
        List<UserCategory> saved = dtos.stream().map(dto -> repo.save(
                UserCategory.builder()
                        .categoryId(dto.getCategoryId())
                        .label(dto.getLabel())
                        .color(dto.getColor())
                        .user(user)
                        .build()
        )).collect(Collectors.toList());
        return saved.stream().map(this::toDto).collect(Collectors.toList());
    }
}
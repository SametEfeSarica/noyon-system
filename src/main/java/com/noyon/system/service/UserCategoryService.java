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
        return repo.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserCategoryDto> saveAll(Long userId, List<UserCategoryDto> dtos) {
        repo.deleteAllByUserId(userId);
        repo.flush();

        User user = findUser(userId);

        List<UserCategory> saved = dtos.stream()
                .map(dto -> UserCategory.builder()
                        .categoryId(dto.getCategoryId())
                        .label(dto.getLabel())
                        .color(dto.getColor())
                        .user(user)
                        .build())
                .map(repo::save)
                .collect(Collectors.toList());

        return saved.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
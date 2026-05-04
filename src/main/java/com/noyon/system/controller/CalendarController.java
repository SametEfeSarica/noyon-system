package com.noyon.system.controller;

import com.noyon.system.entity.CalendarEvent;
import com.noyon.system.entity.User;
import com.noyon.system.exception.ResourceNotFoundException;
import com.noyon.system.repository.CalendarRepository;
import com.noyon.system.repository.UserRepository;
import com.noyon.system.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    @Data
    static class CalendarEventRequest {
        @NotBlank(message = "Etkinlik başlığı boş olamaz.")
        private String title;

        @NotNull(message = "Tarih zorunludur.")
        private LocalDateTime eventDate;

        private String category;
        private String color;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CalendarEvent>>> getUserEvents(
            @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<CalendarEvent> events = calendarRepository.findByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Etkinlikler getirildi.", events));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CalendarEvent>> addEvent(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CalendarEventRequest request) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CalendarEvent event = CalendarEvent.builder()
                .title(request.getTitle())
                .eventDate(request.getEventDate())
                .category(request.getCategory())
                .color(request.getColor())
                .user(user)
                .build();

        CalendarEvent saved = calendarRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Etkinlik oluşturuldu.", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CalendarEvent event = calendarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CalendarEvent", id));

        if (!event.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bu etkinliğe erişim yetkiniz yok."));
        }

        calendarRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Etkinlik silindi."));
    }
}
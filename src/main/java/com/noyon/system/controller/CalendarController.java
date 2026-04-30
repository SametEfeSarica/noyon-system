package com.noyon.system.controller;

import com.noyon.system.entity.CalendarEvent;
import com.noyon.system.repository.CalendarRepository;
import com.noyon.system.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "*")
public class CalendarController {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    public CalendarController(CalendarRepository calendarRepository, UserRepository userRepository) {
        this.calendarRepository = calendarRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CalendarEvent>> getUserEvents(@PathVariable Long userId) {
        return ResponseEntity.ok(calendarRepository.findByUserId(userId));
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addEvent(@PathVariable Long userId, @RequestBody CalendarEvent event) {
        return userRepository.findById(userId).map(user -> {
            event.setUser(user);
            CalendarEvent savedEvent = calendarRepository.save(event);
            return ResponseEntity.ok(savedEvent);
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
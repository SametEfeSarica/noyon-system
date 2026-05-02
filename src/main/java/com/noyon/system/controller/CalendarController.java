package com.noyon.system.controller;

import com.noyon.system.entity.CalendarEvent;
import com.noyon.system.repository.CalendarRepository;
import com.noyon.system.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

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

    // DÜZELTME: Frontend ile uyum için "/add/{userId}" yerine "/add" yapıldı. ID gövdeden alınıyor.
    @PostMapping("/add")
    public ResponseEntity<?> addEvent(@RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        ObjectMapper mapper = new ObjectMapper();
        CalendarEvent event = mapper.convertValue(payload, CalendarEvent.class);

        return userRepository.findById(userId).map(user -> {
            event.setUser(user);
            CalendarEvent savedEvent = calendarRepository.save(event);
            return ResponseEntity.ok(savedEvent);
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
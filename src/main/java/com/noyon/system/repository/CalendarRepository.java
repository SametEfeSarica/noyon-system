package com.noyon.system.repository;

import com.noyon.system.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByUserId(Long userId);
}
package com.example.pulsar_fitness_app.controller;

import com.example.pulsar_fitness_app.dto.BookingDto;
import com.example.pulsar_fitness_app.service.BookingService;
import com.example.pulsar_fitness_app.service.ClientService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<BookingDto> bookWorkout(
            @RequestBody BookingRequest request,
            Authentication authentication) {

        Long clientId = request.getClientId();
        if (clientId == null && authentication != null) {
            String username = authentication.getName();
            clientId = clientService.getClientIdByUsername(username);
        }
        if (clientId == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Client ID is required");
        }
        if (request.getScheduleWorkoutId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule workout ID is required");
        }
        if (request.getWorkoutDate() == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Workout date is required");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.bookWorkout(request.getScheduleWorkoutId(), clientId, request.getWorkoutDate()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/mark-attendance")
    public ResponseEntity<BookingDto> markAttendance(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.markAttendance(id));
    }

    @PostMapping("/daily-job")
    public ResponseEntity<Void> markNotAttended(@RequestHeader(name = "X-Internal-Token", required = false) String provided) {
        bookingService.markNotAttended();
        return ResponseEntity.noContent().build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class BookingRequest {
        private Long scheduleWorkoutId;
        private Long clientId;
        private java.time.LocalDateTime workoutDate;
    }
}

package com.example.pulsar_fitness_app.controller;

import com.example.pulsar_fitness_app.dto.PasswordVerifyRequest;
import com.example.pulsar_fitness_app.dto.PasswordVerifyResponse;
import com.example.pulsar_fitness_app.dto.ScheduleWorkoutDto;
import com.example.pulsar_fitness_app.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleWorkoutDto>> getSchedule(
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (days != null) {
            return ResponseEntity.ok(scheduleService.getSchedule(days));
        } else if (date != null) {
            return ResponseEntity.ok(scheduleService.getScheduleByDate(date));
        } else if (startDate != null && endDate != null) {
            return ResponseEntity.ok(scheduleService.getScheduleByDateRange(startDate, endDate));
        } else {
            return ResponseEntity.ok(scheduleService.getSchedule(7)); // По умолчанию 7 дней
        }
    }

    @PostMapping
    public ResponseEntity<ScheduleWorkoutDto> createWorkout(@RequestBody ScheduleWorkoutDto workoutDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleService.createWorkout(workoutDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleWorkoutDto> updateWorkout(@PathVariable Long id, @RequestBody ScheduleWorkoutDto workoutDto) {
        return ResponseEntity.ok(scheduleService.updateWorkout(id, workoutDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        scheduleService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<ScheduleWorkoutDto>> getTrainerSchedule(
            @PathVariable Long trainerId,
            @RequestParam(required = false) Integer days) {
        return ResponseEntity.ok(scheduleService.getTrainerSchedule(trainerId, days));
    }
}

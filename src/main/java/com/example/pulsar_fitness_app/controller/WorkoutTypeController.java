package com.example.pulsar_fitness_app.controller;

import com.example.pulsar_fitness_app.dto.WorkoutTypeDto;
import com.example.pulsar_fitness_app.service.WorkoutTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-types")
@RequiredArgsConstructor
public class WorkoutTypeController {
    private final WorkoutTypeService workoutTypeService;

    @GetMapping
    public ResponseEntity<List<WorkoutTypeDto>> getAllWorkoutTypes() {
        return ResponseEntity.ok(workoutTypeService.getAllWorkoutTypes());
    }
}

package com.example.pulsar_fitness_app.controller;

import com.example.pulsar_fitness_app.dto.TrainerDto;
import com.example.pulsar_fitness_app.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<TrainerDto> getTrainerByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(trainerService.getTrainerByUserId(userId));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<TrainerDto> updateTrainerByUserId(
            @PathVariable Long userId,
            @RequestBody TrainerDto trainerDto) {
        return ResponseEntity.ok(trainerService.updateTrainerByUserId(userId, trainerDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TrainerDto>> getAllTrainers() {
        return ResponseEntity.ok(trainerService.getAllTrainers());
    }
}

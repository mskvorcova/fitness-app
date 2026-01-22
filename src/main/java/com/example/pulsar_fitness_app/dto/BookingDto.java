package com.example.pulsar_fitness_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private Long clientId;
    private Long scheduleWorkoutId;
    private LocalDateTime workoutDate;
    private String status;
    private String workoutName;
    private String trainerName;
}

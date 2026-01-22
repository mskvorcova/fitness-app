package com.example.pulsar_fitness_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleWorkoutDto {
    private Long id;
    private Long scheduleWorkoutId;
    private Long workoutTypeId;
    private String workoutName;
    private Long trainerId;
    private String trainerName;
    private String dayOfWeek;
    private LocalDate date;
    private LocalTime time;
    private String hall;
    private Integer capacity;
    private Integer enrolled;
}

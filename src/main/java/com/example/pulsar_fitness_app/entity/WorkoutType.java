package com.example.pulsar_fitness_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workout_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private String hall;

    @Column(nullable = false)
    private Integer capacity;

    @Column(length = 1000)
    private String description;
}

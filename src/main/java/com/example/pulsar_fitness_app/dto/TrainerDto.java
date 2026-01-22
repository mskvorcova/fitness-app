package com.example.pulsar_fitness_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto {
    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String email;
    private String description;
    private Integer experienceYears;
}

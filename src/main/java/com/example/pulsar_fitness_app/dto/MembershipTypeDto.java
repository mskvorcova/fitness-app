package com.example.pulsar_fitness_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTypeDto {
    private Long id;
    private String name;
    private String description;
    private Integer durationDays;
    private Double price;
}

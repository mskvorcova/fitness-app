package com.example.pulsar_fitness_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDto {
    private Long id;
    private Long clientId;
    private Long membershipTypeId;
    private String membershipTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;
    private String status;
}

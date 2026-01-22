package com.example.pulsar_fitness_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private LocalDate registrationDate;
    private String membershipTypeName;
    private LocalDate membershipExpiry;
    private String status;
}

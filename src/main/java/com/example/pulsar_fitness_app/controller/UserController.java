package com.example.pulsar_fitness_app.controller;

import com.example.pulsar_fitness_app.dto.UserDto;
import com.example.pulsar_fitness_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getCurrentUser(username));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(
            Authentication authentication,
            @RequestBody UpdateUserRequest request) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateCurrentUser(username, request));
    }

    @PostMapping("/clients")
    public ResponseEntity<UserDto> createClient(@RequestBody CreateClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createClient(request));
    }

    @PostMapping("/trainers")
    public ResponseEntity<UserDto> createTrainer(@RequestBody CreateTrainerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createTrainer(request));
    }


    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UpdateUserRequest {
        private String name;
        private String email;
        private String newPassword;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CreateClientRequest {
        private String username;
        private String name;
        private String email;
        private String phone;
        private String password;
        private Long membershipTypeId;
        private java.time.LocalDate membershipStartDate;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CreateTrainerRequest {
        private String username;
        private String name;
        private String email;
        private String password;
        private String description;
        private Integer experienceYears;
    }
}

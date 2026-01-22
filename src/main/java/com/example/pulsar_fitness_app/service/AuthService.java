package com.example.pulsar_fitness_app.service;

import com.example.pulsar_fitness_app.dto.LoginRequest;
import com.example.pulsar_fitness_app.dto.LoginResponse;
import com.example.pulsar_fitness_app.dto.UserDto;
import com.example.pulsar_fitness_app.entity.User;
import com.example.pulsar_fitness_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        
        String token = jwtService.generateToken(user.getUsername());

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().name());

        LoginResponse response = new LoginResponse();
        response.setUser(userDto);
        response.setToken(token);
        return response;
    }

    public Optional<User> getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }
}

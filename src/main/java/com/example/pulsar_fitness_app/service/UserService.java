package com.example.pulsar_fitness_app.service;

import com.example.pulsar_fitness_app.controller.UserController;
import com.example.pulsar_fitness_app.dto.UserDto;
import com.example.pulsar_fitness_app.entity.Client;
import com.example.pulsar_fitness_app.entity.Membership;
import com.example.pulsar_fitness_app.entity.MembershipType;
import com.example.pulsar_fitness_app.entity.Trainer;
import com.example.pulsar_fitness_app.entity.User;
import com.example.pulsar_fitness_app.repository.ClientRepository;
import com.example.pulsar_fitness_app.repository.MembershipRepository;
import com.example.pulsar_fitness_app.repository.MembershipTypeRepository;
import com.example.pulsar_fitness_app.repository.TrainerRepository;
import com.example.pulsar_fitness_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final TrainerRepository trainerRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipTypeRepository membershipTypeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToDto(user);
    }

    public UserDto updateCurrentUser(String username, UserController.UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String currentEmail = user.getEmail() != null ? user.getEmail() : "";
            if (!currentEmail.equals(request.getEmail())) {
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
                }
                user.setEmail(request.getEmail());
            }
        } else if (request.getEmail() != null && request.getEmail().trim().isEmpty()) {
            user.setEmail(null);
        }
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Transactional
    public UserDto createClient(UserController.CreateClientRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.CLIENT);

        User savedUser = userRepository.save(user);

        Client client = new Client();
        client.setUser(savedUser);
        client.setPhone(request.getPhone());
        Client savedClient = clientRepository.save(client);

        if (request.getMembershipTypeId() != null) {
            MembershipType membershipType = membershipTypeRepository.findById(request.getMembershipTypeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership type not found"));

            LocalDate startDate = request.getMembershipStartDate() != null 
                    ? request.getMembershipStartDate() 
                    : LocalDate.now();
            
            LocalDate endDate = startDate.plusDays(membershipType.getDurationDays());

            Membership membership = new Membership();
            membership.setClient(savedClient);
            membership.setMembershipType(membershipType);
            membership.setStartDate(startDate);
            membership.setEndDate(endDate);
            membership.setPrice(membershipType.getPrice());
            membership.setStatus(Membership.MembershipStatus.ACTIVE);
            
            membershipRepository.save(membership);
        }

        return mapToDto(savedUser);
    }

    @Transactional
    public UserDto createTrainer(UserController.CreateTrainerRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.TRAINER);

        User savedUser = userRepository.save(user);

        Trainer trainer = new Trainer();
        trainer.setUser(savedUser);
        if (request.getDescription() != null) {
            trainer.setDescription(request.getDescription());
        }
        if (request.getExperienceYears() != null) {
            trainer.setExperienceYears(request.getExperienceYears());
        }
        trainerRepository.save(trainer);

        return mapToDto(savedUser);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        return dto;
    }
}

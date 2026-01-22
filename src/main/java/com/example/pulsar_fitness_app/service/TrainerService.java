package com.example.pulsar_fitness_app.service;

import com.example.pulsar_fitness_app.dto.TrainerDto;
import com.example.pulsar_fitness_app.entity.Trainer;
import com.example.pulsar_fitness_app.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerRepository trainerRepository;

    public TrainerDto getTrainerByUserId(Long userId) {
        Trainer trainer = trainerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found"));
        return mapToDto(trainer);
    }

    public TrainerDto updateTrainerByUserId(Long userId, TrainerDto trainerDto) {
        Trainer trainer = trainerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found"));

        if (trainerDto.getDescription() != null) {
            trainer.setDescription(trainerDto.getDescription());
        }
        if (trainerDto.getExperienceYears() != null) {
            trainer.setExperienceYears(trainerDto.getExperienceYears());
        }

        Trainer updatedTrainer = trainerRepository.save(trainer);
        return mapToDto(updatedTrainer);
    }

    public void deleteTrainer(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found"));
        trainerRepository.delete(trainer);
    }

    public List<TrainerDto> getAllTrainers() {
        return trainerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TrainerDto mapToDto(Trainer trainer) {
        TrainerDto dto = new TrainerDto();
        dto.setId(trainer.getId());
        if (trainer.getUser() != null) {
            dto.setUserId(trainer.getUser().getId());
            dto.setName(trainer.getUser().getName());
            dto.setEmail(trainer.getUser().getEmail());
            dto.setUsername(trainer.getUser().getUsername());
        }
        dto.setDescription(trainer.getDescription());
        dto.setExperienceYears(trainer.getExperienceYears());
        return dto;
    }
}

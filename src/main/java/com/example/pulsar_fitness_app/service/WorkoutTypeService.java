package com.example.pulsar_fitness_app.service;

import com.example.pulsar_fitness_app.dto.WorkoutTypeDto;
import com.example.pulsar_fitness_app.entity.WorkoutType;
import com.example.pulsar_fitness_app.repository.WorkoutTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutTypeService {
    private final WorkoutTypeRepository workoutTypeRepository;

    public List<WorkoutTypeDto> getAllWorkoutTypes() {
        List<WorkoutType> workoutTypes = workoutTypeRepository.findAll();
        return workoutTypes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private WorkoutTypeDto mapToDto(WorkoutType workoutType) {
        WorkoutTypeDto dto = new WorkoutTypeDto();
        dto.setId(workoutType.getId());
        dto.setName(workoutType.getName());
        dto.setDescription(workoutType.getDescription());
        return dto;
    }
}

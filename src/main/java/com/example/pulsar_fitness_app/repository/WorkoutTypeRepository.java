package com.example.pulsar_fitness_app.repository;

import com.example.pulsar_fitness_app.entity.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Long> {
}

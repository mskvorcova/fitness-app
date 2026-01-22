package com.example.pulsar_fitness_app.repository;

import com.example.pulsar_fitness_app.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByClientId(Long clientId);
    List<Booking> findByScheduleWorkoutId(Long scheduleWorkoutId);
}

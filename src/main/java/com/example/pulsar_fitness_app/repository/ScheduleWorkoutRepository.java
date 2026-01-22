package com.example.pulsar_fitness_app.repository;

import com.example.pulsar_fitness_app.entity.Trainer;
import com.example.pulsar_fitness_app.entity.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleWorkoutRepository extends JpaRepository<WeeklySchedule, Long> {
    List<WeeklySchedule> findByDayOfWeek(WeeklySchedule.DayOfWeek dayOfWeek);
    List<WeeklySchedule> findByTrainer(Trainer trainer);
}

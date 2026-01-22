package com.example.pulsar_fitness_app.service;

import com.example.pulsar_fitness_app.dto.ScheduleWorkoutDto;
import com.example.pulsar_fitness_app.entity.Booking;
import com.example.pulsar_fitness_app.entity.Trainer;
import com.example.pulsar_fitness_app.entity.WeeklySchedule;
import com.example.pulsar_fitness_app.entity.WorkoutType;
import com.example.pulsar_fitness_app.repository.BookingRepository;
import com.example.pulsar_fitness_app.repository.ScheduleWorkoutRepository;
import com.example.pulsar_fitness_app.repository.TrainerRepository;
import com.example.pulsar_fitness_app.repository.WorkoutTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleWorkoutRepository scheduleWorkoutRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private final BookingRepository bookingRepository;
    private final TrainerRepository trainerRepository;

    public List<ScheduleWorkoutDto> getSchedule(Integer days) {
        List<WeeklySchedule> allSchedules = scheduleWorkoutRepository.findAll();
        LocalDate today = LocalDate.now();
        int daysCount = days != null ? days : 7;
        LocalDate endDate = today.plusDays(daysCount);

        return allSchedules.stream()
                .flatMap(schedule -> {
                    return generateDatesForSchedule(schedule, today, endDate).stream()
                            .map(date -> mapToDto(schedule, date));
                })
                .sorted(scheduleComparator())
                .collect(Collectors.toList());
    }

    public List<ScheduleWorkoutDto> getScheduleByDate(LocalDate date) {
        java.time.DayOfWeek requestedDayOfWeek = date.getDayOfWeek();
        WeeklySchedule.DayOfWeek scheduleDay = WeeklySchedule.DayOfWeek.valueOf(requestedDayOfWeek.name());

        List<WeeklySchedule> schedules = scheduleWorkoutRepository.findByDayOfWeek(scheduleDay);
        return schedules.stream()
                .map(schedule -> mapToDto(schedule, date))
                .collect(Collectors.toList());
    }

    public List<ScheduleWorkoutDto> getScheduleByDateRange(LocalDate startDate, LocalDate endDate) {
        List<WeeklySchedule> allSchedules = scheduleWorkoutRepository.findAll();
        return allSchedules.stream()
                .flatMap(schedule -> generateDatesForSchedule(schedule, startDate, endDate).stream()
                        .map(date -> mapToDto(schedule, date)))
                .sorted(scheduleComparator())
                .collect(Collectors.toList());
    }

    public ScheduleWorkoutDto createWorkout(ScheduleWorkoutDto workoutDto) {
        WorkoutType workoutType = workoutTypeRepository.findById(workoutDto.getWorkoutTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout type not found"));

        Trainer trainer = trainerRepository.findById(workoutDto.getTrainerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found"));

        WeeklySchedule schedule = new WeeklySchedule();
        schedule.setWorkoutType(workoutType);
        schedule.setTrainer(trainer);
        schedule.setStartTime(workoutDto.getTime());
        
        if (workoutDto.getDayOfWeek() != null && !workoutDto.getDayOfWeek().isEmpty()) {
            schedule.setDayOfWeek(WeeklySchedule.DayOfWeek.valueOf(workoutDto.getDayOfWeek()));
        } else if (workoutDto.getDate() != null) {
            java.time.DayOfWeek dayOfWeek = workoutDto.getDate().getDayOfWeek();
            schedule.setDayOfWeek(WeeklySchedule.DayOfWeek.valueOf(dayOfWeek.name()));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Day of week is required");
        }

        WeeklySchedule savedSchedule = scheduleWorkoutRepository.save(schedule);
        LocalDate nextDate = calculateNextDateForDayOfWeek(savedSchedule.getDayOfWeek());
        return mapToDto(savedSchedule, nextDate);
    }

    public ScheduleWorkoutDto updateWorkout(Long id, ScheduleWorkoutDto workoutDto) {
        WeeklySchedule schedule = scheduleWorkoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));

        WorkoutType workoutType = workoutTypeRepository.findById(workoutDto.getWorkoutTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout type not found"));

        Trainer trainer = trainerRepository.findById(workoutDto.getTrainerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found"));

        schedule.setWorkoutType(workoutType);
        schedule.setTrainer(trainer);
        schedule.setStartTime(workoutDto.getTime());

        if (workoutDto.getDayOfWeek() != null && !workoutDto.getDayOfWeek().isEmpty()) {
            schedule.setDayOfWeek(WeeklySchedule.DayOfWeek.valueOf(workoutDto.getDayOfWeek()));
        } else if (workoutDto.getDate() != null) {
            java.time.DayOfWeek dayOfWeek = workoutDto.getDate().getDayOfWeek();
            schedule.setDayOfWeek(WeeklySchedule.DayOfWeek.valueOf(dayOfWeek.name()));
        }

        WeeklySchedule updatedSchedule = scheduleWorkoutRepository.save(schedule);
        LocalDate nextDate = calculateNextDateForDayOfWeek(updatedSchedule.getDayOfWeek());
        return mapToDto(updatedSchedule, nextDate);
    }

    public void deleteWorkout(Long id) {
        WeeklySchedule schedule = scheduleWorkoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));
        scheduleWorkoutRepository.delete(schedule);
    }

    public List<ScheduleWorkoutDto> getTrainerSchedule(Long trainerId, Integer days) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found"));
        
        List<WeeklySchedule> schedules = scheduleWorkoutRepository.findByTrainer(trainer);
        
        LocalDate today = LocalDate.now();
        int daysCount = days != null ? days : 7;
        LocalDate endDate = today.plusDays(daysCount);

        return schedules.stream()
                .flatMap(schedule -> generateDatesForSchedule(schedule, today, endDate).stream()
                        .map(date -> mapToDto(schedule, date)))
                .sorted(scheduleComparator())
                .collect(Collectors.toList());
    }

    private List<LocalDate> generateDatesForSchedule(WeeklySchedule schedule, LocalDate startDate, LocalDate endDate) {
        java.time.DayOfWeek scheduleDayOfWeek = java.time.DayOfWeek.valueOf(schedule.getDayOfWeek().name());
        List<LocalDate> dates = new java.util.ArrayList<>();
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() == scheduleDayOfWeek) {
                dates.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return dates;
    }

    private LocalDate calculateNextDateForDayOfWeek(WeeklySchedule.DayOfWeek dayOfWeek) {
        LocalDate today = LocalDate.now();
        java.time.DayOfWeek todayDayOfWeek = today.getDayOfWeek();
        java.time.DayOfWeek scheduleDayOfWeek = java.time.DayOfWeek.valueOf(dayOfWeek.name());
        
        int daysToAdd = scheduleDayOfWeek.getValue() - todayDayOfWeek.getValue();
        if (daysToAdd < 0) {
            daysToAdd += 7;
        } else if (daysToAdd == 0) {
            daysToAdd = 7;
        }
        
        return today.plusDays(daysToAdd);
    }

    private ScheduleWorkoutDto mapToDto(WeeklySchedule schedule, LocalDate date) {
        ScheduleWorkoutDto dto = new ScheduleWorkoutDto();
        dto.setId(schedule.getId());
        dto.setScheduleWorkoutId(schedule.getId());
        dto.setWorkoutTypeId(schedule.getWorkoutType().getId());
        dto.setWorkoutName(schedule.getWorkoutType().getName());
        dto.setTrainerId(schedule.getTrainer().getId());
        dto.setTrainerName(schedule.getTrainer().getUser().getName());
        dto.setTime(schedule.getStartTime());
        dto.setDate(date);
        dto.setDayOfWeek(schedule.getDayOfWeek().name());
        dto.setHall(schedule.getWorkoutType().getHall());
        
        int enrolledCount = (int) bookingRepository.findByScheduleWorkoutId(schedule.getId()).stream()
                .filter(booking -> booking.getWorkoutDate().toLocalDate().equals(date) && 
                                  booking.getStatus() == Booking.BookingStatus.BOOKED)
                .count();
        dto.setEnrolled(enrolledCount);
        
        dto.setCapacity(schedule.getWorkoutType().getCapacity());
        return dto;
    }

    private Comparator<ScheduleWorkoutDto> scheduleComparator() {
        return Comparator
                .comparing(ScheduleWorkoutDto::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ScheduleWorkoutDto::getTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ScheduleWorkoutDto::getWorkoutName, Comparator.nullsLast(Comparator.naturalOrder()));
    }
}

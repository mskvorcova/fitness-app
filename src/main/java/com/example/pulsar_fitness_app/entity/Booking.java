package com.example.pulsar_fitness_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "schedule_workout_id", nullable = false)
    private WeeklySchedule scheduleWorkout;

    @Column(name = "workout_date", nullable = false)
    private LocalDateTime workoutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.BOOKED;

    public enum BookingStatus {
        BOOKED,
        ATTENDED,
        NOT_ATTENDED,
        CANCELLED
    }
}

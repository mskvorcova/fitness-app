package com.example.pulsar_fitness_app.service;

import com.example.pulsar_fitness_app.dto.BookingDto;
import com.example.pulsar_fitness_app.entity.Booking;
import com.example.pulsar_fitness_app.entity.Client;
import com.example.pulsar_fitness_app.entity.Membership;
import com.example.pulsar_fitness_app.entity.User;
import com.example.pulsar_fitness_app.entity.WeeklySchedule;
import com.example.pulsar_fitness_app.repository.BookingRepository;
import com.example.pulsar_fitness_app.repository.ClientRepository;
import com.example.pulsar_fitness_app.repository.MembershipRepository;
import com.example.pulsar_fitness_app.repository.ScheduleWorkoutRepository;
import com.example.pulsar_fitness_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final ScheduleWorkoutRepository scheduleWorkoutRepository;
    private final MembershipService membershipService;
    private final KafkaNumberService kafkaNumberService;

    public BookingDto bookWorkout(Long scheduleWorkoutId, Long clientId, LocalDateTime workoutDate) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
        WeeklySchedule scheduleWorkout = scheduleWorkoutRepository.findById(scheduleWorkoutId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule workout not found"));

        membershipService.validateMembership(clientId, workoutDate);

        Integer capacity = scheduleWorkout.getWorkoutType().getCapacity();
        if (capacity != null) {
            long enrolledCount = bookingRepository.findByScheduleWorkoutId(scheduleWorkoutId).stream()
                    .filter(booking -> booking.getWorkoutDate().toLocalDate().equals(workoutDate.toLocalDate()) &&
                                      booking.getStatus() == Booking.BookingStatus.BOOKED)
                    .count();
            
            if (enrolledCount >= capacity) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "No available spots for this workout. All places are booked.");
            }
        }

        Booking booking = new Booking();
        booking.setClient(client);
        booking.setScheduleWorkout(scheduleWorkout);
        booking.setWorkoutDate(workoutDate);
        booking.setStatus(Booking.BookingStatus.BOOKED);

        Booking savedBooking = bookingRepository.save(booking);
        return mapToDto(savedBooking);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public BookingDto markAttendance(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        
        if (booking.getStatus() != Booking.BookingStatus.BOOKED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only mark attendance for BOOKED bookings");
        }
        
        booking.setStatus(Booking.BookingStatus.ATTENDED);
        Booking updatedBooking = bookingRepository.save(booking);
        kafkaNumberService.sendNumber(1);
        return mapToDto(updatedBooking);
    }

    public void markNotAttended() {
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking b : bookings) {
            Booking.BookingStatus st = b.getStatus();
            if (st != Booking.BookingStatus.ATTENDED && st != Booking.BookingStatus.NOT_ATTENDED && st != Booking.BookingStatus.CANCELLED
                    && b.getWorkoutDate().isBefore(LocalDateTime.now())) {
                b.setStatus(Booking.BookingStatus.NOT_ATTENDED);
                bookingRepository.save(b);
                kafkaNumberService.sendNumber(-1);
            }
        }
    }

    public List<BookingDto> getBookingsByClientId(Long clientId) {
        List<Booking> bookings = bookingRepository.findByClientId(clientId);
        return bookings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getPastVisitsByClientId(Long clientId) {
        List<Booking> bookings = bookingRepository.findByClientId(clientId);
        return bookings.stream()
                .filter(booking -> booking.getStatus() == Booking.BookingStatus.ATTENDED || 
                                 booking.getStatus() == Booking.BookingStatus.NOT_ATTENDED)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BookingDto mapToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setClientId(booking.getClient().getId());
        dto.setScheduleWorkoutId(booking.getScheduleWorkout().getId());
        dto.setWorkoutDate(booking.getWorkoutDate());
        dto.setStatus(booking.getStatus().name());
        
        if (booking.getScheduleWorkout() != null && booking.getScheduleWorkout().getWorkoutType() != null) {
            dto.setWorkoutName(booking.getScheduleWorkout().getWorkoutType().getName());
        }
        if (booking.getScheduleWorkout() != null && booking.getScheduleWorkout().getTrainer() != null 
            && booking.getScheduleWorkout().getTrainer().getUser() != null) {
            dto.setTrainerName(booking.getScheduleWorkout().getTrainer().getUser().getName());
        }
        
        return dto;
    }
}

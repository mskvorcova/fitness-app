package com.example.pulsar_fitness_app.controller;

import com.example.pulsar_fitness_app.dto.BookingDto;
import com.example.pulsar_fitness_app.dto.ClientDto;
import com.example.pulsar_fitness_app.dto.MembershipDto;
import com.example.pulsar_fitness_app.service.BookingService;
import com.example.pulsar_fitness_app.service.ClientService;
import com.example.pulsar_fitness_app.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final BookingService bookingService;
    private final MembershipService membershipService;

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody ClientDto clientDto) {
        return ResponseEntity.ok(clientService.updateClient(id, clientDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/membership")
    public ResponseEntity<MembershipDto> getClientMembership(@PathVariable Long id) {
        return ResponseEntity.ok(membershipService.getMembershipByClientId(id));
    }

    @PostMapping("/{id}/membership")
    public ResponseEntity<MembershipDto> createClientMembership(
            @PathVariable Long id,
            @RequestBody MembershipDto membershipDto) {
        membershipDto.setClientId(id);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(membershipService.createMembership(membershipDto));
    }

    @PutMapping("/{id}/membership/{membershipId}")
    public ResponseEntity<MembershipDto> updateClientMembership(
            @PathVariable Long id,
            @PathVariable Long membershipId,
            @RequestBody MembershipDto membershipDto) {
        membershipDto.setClientId(id);
        return ResponseEntity.ok(membershipService.updateMembership(membershipId, membershipDto));
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingDto>> getClientBookings(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingsByClientId(id));
    }

    @GetMapping("/{id}/visits")
    public ResponseEntity<List<BookingDto>> getClientVisits(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getPastVisitsByClientId(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ClientDto> getClientByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(clientService.getClientByUserId(userId));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<ClientDto> updateClientByUserId(
            @PathVariable Long userId,
            @RequestBody ClientDto clientDto) {
        return ResponseEntity.ok(clientService.updateClientByUserId(userId, clientDto));
    }
}

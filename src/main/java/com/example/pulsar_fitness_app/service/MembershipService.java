package com.example.pulsar_fitness_app.service;

import com.example.pulsar_fitness_app.dto.MembershipDto;
import com.example.pulsar_fitness_app.entity.Client;
import com.example.pulsar_fitness_app.entity.Membership;
import com.example.pulsar_fitness_app.entity.MembershipType;
import com.example.pulsar_fitness_app.repository.ClientRepository;
import com.example.pulsar_fitness_app.repository.MembershipRepository;
import com.example.pulsar_fitness_app.repository.MembershipTypeRepository;
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
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final ClientRepository clientRepository;
    private final MembershipTypeRepository membershipTypeRepository;

    public MembershipDto getMembershipByClientId(Long clientId) {
        Membership membership = membershipRepository
                .findByClientIdAndStatus(clientId, Membership.MembershipStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Active membership not found for client"));
        return mapToDto(membership);
    }

    public MembershipDto createMembership(MembershipDto membershipDto) {
        Client client = clientRepository.findById(membershipDto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        MembershipType membershipType = membershipTypeRepository.findById(membershipDto.getMembershipTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership type not found"));

        Membership membership = new Membership();
        membership.setClient(client);
        membership.setMembershipType(membershipType);
        membership.setStartDate(membershipDto.getStartDate());
        membership.setEndDate(membershipDto.getEndDate());
        membership.setPrice(membershipDto.getPrice());
        membership.setStatus(Membership.MembershipStatus.valueOf(membershipDto.getStatus()));

        Membership savedMembership = membershipRepository.save(membership);
        return mapToDto(savedMembership);
    }

    public MembershipDto updateMembership(Long id, MembershipDto membershipDto) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));

        MembershipType membershipType = membershipTypeRepository.findById(membershipDto.getMembershipTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership type not found"));

        membership.setMembershipType(membershipType);
        membership.setStartDate(membershipDto.getStartDate());
        membership.setEndDate(membershipDto.getEndDate());
        membership.setPrice(membershipDto.getPrice());
        membership.setStatus(Membership.MembershipStatus.valueOf(membershipDto.getStatus()));

        Membership updatedMembership = membershipRepository.save(membership);
        return mapToDto(updatedMembership);
    }

    public void validateMembership(Long clientId, LocalDateTime workoutDate) {
        Optional<Membership> activeMembershipOpt =
                membershipRepository.findByClientIdAndStatus(clientId, Membership.MembershipStatus.ACTIVE);

        if (activeMembershipOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Client does not have an active membership. Please purchase a membership first."
            );
        }

        Membership activeMembership = activeMembershipOpt.get();

        LocalDate today = LocalDate.now();
        if (activeMembership.getEndDate() != null && activeMembership.getEndDate().isBefore(today)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Membership has expired. Please renew your membership."
            );
        }

        LocalDate workoutLocalDate = workoutDate.toLocalDate();
        if (activeMembership.getStartDate() != null && workoutLocalDate.isBefore(activeMembership.getStartDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Workout date is before membership start date."
            );
        }

        if (activeMembership.getEndDate() != null && workoutLocalDate.isAfter(activeMembership.getEndDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Workout date is after membership end date."
            );
        }
    }

    private MembershipDto mapToDto(Membership membership) {
        MembershipDto dto = new MembershipDto();
        dto.setId(membership.getId());
        dto.setClientId(membership.getClient().getId());
        dto.setMembershipTypeId(membership.getMembershipType().getId());
        dto.setMembershipTypeName(membership.getMembershipType().getName());
        dto.setStartDate(membership.getStartDate());
        dto.setEndDate(membership.getEndDate());
        dto.setPrice(membership.getPrice());
        dto.setStatus(membership.getStatus().name());
        return dto;
    }
}

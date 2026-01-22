package com.example.pulsar_fitness_app.service;

import com.example.pulsar_fitness_app.dto.MembershipTypeDto;
import com.example.pulsar_fitness_app.entity.MembershipType;
import com.example.pulsar_fitness_app.repository.MembershipTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipTypeService {
    private final MembershipTypeRepository membershipTypeRepository;

    public List<MembershipTypeDto> getAllMembershipTypes() {
        List<MembershipType> membershipTypes = membershipTypeRepository.findAll();
        return membershipTypes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public MembershipTypeDto createMembershipType(MembershipTypeDto membershipTypeDto) {
        MembershipType membershipType = new MembershipType();
        membershipType.setName(membershipTypeDto.getName());
        membershipType.setDescription(membershipTypeDto.getDescription());
        membershipType.setDurationDays(membershipTypeDto.getDurationDays());
        membershipType.setPrice(membershipTypeDto.getPrice());

        MembershipType savedMembershipType = membershipTypeRepository.save(membershipType);
        return mapToDto(savedMembershipType);
    }

    public MembershipTypeDto updateMembershipType(Long id, MembershipTypeDto membershipTypeDto) {
        MembershipType membershipType = membershipTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership type not found"));

        membershipType.setName(membershipTypeDto.getName());
        membershipType.setDescription(membershipTypeDto.getDescription());
        membershipType.setDurationDays(membershipTypeDto.getDurationDays());
        membershipType.setPrice(membershipTypeDto.getPrice());

        MembershipType updatedMembershipType = membershipTypeRepository.save(membershipType);
        return mapToDto(updatedMembershipType);
    }

    public void deleteMembershipType(Long id) {
        MembershipType membershipType = membershipTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership type not found"));
        membershipTypeRepository.delete(membershipType);
    }

    private MembershipTypeDto mapToDto(MembershipType membershipType) {
        MembershipTypeDto dto = new MembershipTypeDto();
        dto.setId(membershipType.getId());
        dto.setName(membershipType.getName());
        dto.setDescription(membershipType.getDescription());
        dto.setDurationDays(membershipType.getDurationDays());
        dto.setPrice(membershipType.getPrice());
        return dto;
    }
}

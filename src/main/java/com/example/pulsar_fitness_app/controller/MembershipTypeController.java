package com.example.pulsar_fitness_app.controller;

import com.example.pulsar_fitness_app.dto.MembershipTypeDto;
import com.example.pulsar_fitness_app.service.MembershipTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership-types")
@RequiredArgsConstructor
public class MembershipTypeController {
    private final MembershipTypeService membershipTypeService;

    @GetMapping
    public ResponseEntity<List<MembershipTypeDto>> getAllMembershipTypes() {
        return ResponseEntity.ok(membershipTypeService.getAllMembershipTypes());
    }


    @PostMapping
    public ResponseEntity<MembershipTypeDto> createMembershipType(@RequestBody MembershipTypeDto membershipTypeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipTypeService.createMembershipType(membershipTypeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembershipTypeDto> updateMembershipType(
            @PathVariable Long id,
            @RequestBody MembershipTypeDto membershipTypeDto) {
        return ResponseEntity.ok(membershipTypeService.updateMembershipType(id, membershipTypeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembershipType(@PathVariable Long id) {
        membershipTypeService.deleteMembershipType(id);
        return ResponseEntity.noContent().build();
    }
}

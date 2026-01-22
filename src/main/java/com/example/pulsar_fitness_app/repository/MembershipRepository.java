package com.example.pulsar_fitness_app.repository;

import com.example.pulsar_fitness_app.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByClientIdAndStatus(Long clientId, Membership.MembershipStatus status);
}

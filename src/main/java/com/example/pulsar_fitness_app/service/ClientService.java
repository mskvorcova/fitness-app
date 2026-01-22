package com.example.pulsar_fitness_app.service;

import com.example.pulsar_fitness_app.dto.ClientDto;
import com.example.pulsar_fitness_app.entity.Client;
import com.example.pulsar_fitness_app.entity.Membership;
import com.example.pulsar_fitness_app.entity.User;
import com.example.pulsar_fitness_app.repository.ClientRepository;
import com.example.pulsar_fitness_app.repository.MembershipRepository;
import com.example.pulsar_fitness_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public List<ClientDto> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        System.out.println(clients);
        return clients.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ClientDto getClientByUserId(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
        return mapToDto(client);
    }

    public ClientDto updateClient(Long id, ClientDto clientDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        client.setPhone(clientDto.getPhone());

        Client updatedClient = clientRepository.save(client);
        return mapToDto(updatedClient);
    }

    public ClientDto updateClientByUserId(Long userId, ClientDto clientDto) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        client.setPhone(clientDto.getPhone());

        Client updatedClient = clientRepository.save(client);
        return mapToDto(updatedClient);
    }

    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
        clientRepository.delete(client);
    }

    private ClientDto mapToDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        if (client.getUser() != null) {
            dto.setUserId(client.getUser().getId());
            dto.setName(client.getUser().getName());
            dto.setEmail(client.getUser().getEmail());
        }
        dto.setPhone(client.getPhone());
        dto.setRegistrationDate(client.getRegistrationDate());

        membershipRepository.findByClientIdAndStatus(client.getId(), Membership.MembershipStatus.ACTIVE)
                .ifPresentOrElse(
                        membership -> {
                            dto.setMembershipTypeName(membership.getMembershipType().getName());
                            dto.setMembershipExpiry(membership.getEndDate());
                            // Определяем статус на основе даты истечения
                            if (membership.getEndDate().isBefore(LocalDate.now())) {
                                dto.setStatus("expired");
                            } else {
                                dto.setStatus("active");
                            }
                        },
                        () -> {
                            dto.setMembershipTypeName(null);
                            dto.setMembershipExpiry(null);
                            dto.setStatus("no_membership");
                        }
                );

        return dto;
    }

    public Long getClientIdByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return clientRepository.findByUserId(user.getId())
                .map(Client::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found for user"));
    }
}

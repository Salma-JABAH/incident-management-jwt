package com.example.incidents.service;

import com.example.incidents.dto.Mapper;
import com.example.incidents.dto.StatisticsDto;
import com.example.incidents.dto.UserDto;
import com.example.incidents.entity.Role;
import com.example.incidents.entity.Status;
import com.example.incidents.entity.User;
import com.example.incidents.exception.ApiException;
import com.example.incidents.repository.IncidentRepository;
import com.example.incidents.repository.UserRepository;
import com.example.incidents.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private final UserRepository users;
    private final IncidentRepository incidents;
    private final CurrentUserService current;

    public AdminService(UserRepository users, IncidentRepository incidents, CurrentUserService current) {
        this.users = users;
        this.incidents = incidents;
        this.current = current;
    }

    @Transactional(readOnly = true)
    public List<UserDto> listUsers() {
        return users.findAll().stream().map(Mapper::toDto).toList();
    }

    public UserDto setEnabled(Long id, boolean enabled) {
        User target = users.findById(id).orElseThrow(() -> ApiException.notFound("Utilisateur introuvable"));
        if (!enabled && target.getId().equals(current.get().getId())) {
            throw ApiException.badRequest("Vous ne pouvez pas desactiver votre propre compte");
        }
        target.setEnabled(enabled);
        return Mapper.toDto(users.save(target));
    }

    @Transactional(readOnly = true)
    public StatisticsDto statistics() {
        return new StatisticsDto(
                users.count(),
                users.countByRole(Role.EMPLOYEE),
                users.countByRole(Role.TECHNICIAN),
                users.countByRole(Role.ADMIN),
                incidents.count(),
                incidents.countByStatus(Status.OPEN),
                incidents.countByStatus(Status.ASSIGNED),
                incidents.countByStatus(Status.IN_PROGRESS),
                incidents.countByStatus(Status.RESOLVED),
                incidents.countByStatus(Status.CLOSED));
    }
}

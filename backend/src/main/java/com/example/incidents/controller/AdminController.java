package com.example.incidents.controller;

import com.example.incidents.dto.StatisticsDto;
import com.example.incidents.dto.UserDto;
import com.example.incidents.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** /api/admin/** est deja restreint a ADMIN dans SecurityConfig ; @PreAuthorize ajoute une 2e barriere. */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService admin;

    public AdminController(AdminService admin) {
        this.admin = admin;
    }

    @GetMapping("/users")
    public List<UserDto> users() {
        return admin.listUsers();
    }

    @PutMapping("/users/{id}/enable")
    public UserDto enable(@PathVariable Long id) {
        return admin.setEnabled(id, true);
    }

    @PutMapping("/users/{id}/disable")
    public UserDto disable(@PathVariable Long id) {
        return admin.setEnabled(id, false);
    }

    @GetMapping("/statistics")
    public StatisticsDto statistics() {
        return admin.statistics();
    }
}

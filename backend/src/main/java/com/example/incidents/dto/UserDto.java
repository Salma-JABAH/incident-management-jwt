package com.example.incidents.dto;

import com.example.incidents.entity.Role;
import java.time.LocalDateTime;

public record UserDto(Long id, String fullName, String email, Role role, boolean enabled, LocalDateTime createdAt) {
}

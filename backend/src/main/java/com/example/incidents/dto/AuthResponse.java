package com.example.incidents.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(String token, String refreshToken, String role, String fullName) {
}

package com.example.incidents.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank(message = "Le nom complet est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
        String fullName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        String email) {
}

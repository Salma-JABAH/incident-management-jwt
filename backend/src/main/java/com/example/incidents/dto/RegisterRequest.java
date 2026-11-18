package com.example.incidents.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Pas de champ "role" : le role est toujours EMPLOYEE a l'inscription (un champ "role" envoye est ignore). */
public record RegisterRequest(
        @NotBlank(message = "Le nom complet est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
        String fullName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, max = 100, message = "Le mot de passe doit contenir au moins 6 caracteres")
        String password) {
}

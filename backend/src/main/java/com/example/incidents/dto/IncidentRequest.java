package com.example.incidents.dto;

import com.example.incidents.entity.Category;
import com.example.incidents.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IncidentRequest(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 150, message = "Le titre ne doit pas depasser 150 caracteres")
        String title,

        @NotBlank(message = "La description est obligatoire")
        @Size(max = 2000, message = "La description ne doit pas depasser 2000 caracteres")
        String description,

        @NotNull(message = "La categorie est obligatoire")
        Category category,

        @NotNull(message = "La priorite est obligatoire")
        Priority priority) {
}

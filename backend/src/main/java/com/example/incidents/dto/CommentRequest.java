package com.example.incidents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = "La note ne peut pas etre vide")
        @Size(max = 2000, message = "La note ne doit pas depasser 2000 caracteres")
        String content) {
}

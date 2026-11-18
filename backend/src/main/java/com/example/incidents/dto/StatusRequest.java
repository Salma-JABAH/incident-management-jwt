package com.example.incidents.dto;

import com.example.incidents.entity.Status;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull(message = "Le statut est obligatoire") Status status) {
}

package com.example.incidents.dto;

import java.time.LocalDateTime;

public record HistoryDto(Long id, String message, LocalDateTime createdAt) {
}

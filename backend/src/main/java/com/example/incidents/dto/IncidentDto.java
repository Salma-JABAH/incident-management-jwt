package com.example.incidents.dto;

import com.example.incidents.entity.Category;
import com.example.incidents.entity.Priority;
import com.example.incidents.entity.Status;
import java.time.LocalDateTime;

public record IncidentDto(
        Long id,
        String title,
        String description,
        Category category,
        Priority priority,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdById,
        String createdByName,
        Long assignedToId,
        String assignedToName) {
}

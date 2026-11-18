package com.example.incidents.dto;

public record StatisticsDto(
        long totalUsers,
        long employees,
        long technicians,
        long admins,
        long totalIncidents,
        long open,
        long assigned,
        long inProgress,
        long resolved,
        long closed) {
}

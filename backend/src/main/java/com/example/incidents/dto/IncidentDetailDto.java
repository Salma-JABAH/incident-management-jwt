package com.example.incidents.dto;

import java.util.List;

public record IncidentDetailDto(IncidentDto incident, List<HistoryDto> history, List<CommentDto> comments) {
}

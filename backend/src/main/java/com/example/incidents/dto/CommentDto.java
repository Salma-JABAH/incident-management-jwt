package com.example.incidents.dto;

import java.time.LocalDateTime;

public record CommentDto(Long id, String authorName, String content, LocalDateTime createdAt) {
}

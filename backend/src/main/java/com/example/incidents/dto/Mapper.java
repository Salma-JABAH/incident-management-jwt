package com.example.incidents.dto;

import com.example.incidents.entity.Incident;
import com.example.incidents.entity.User;

/** Conversion entites -> DTO. On n'expose jamais les entites (mot de passe, proxies LAZY...). */
public final class Mapper {

    private Mapper() {}

    public static UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.isEnabled(), u.getCreatedAt());
    }

    public static IncidentDto toDto(Incident i) {
        User c = i.getCreatedBy();
        User a = i.getAssignedTo();
        return new IncidentDto(
                i.getId(), i.getTitle(), i.getDescription(), i.getCategory(), i.getPriority(), i.getStatus(),
                i.getCreatedAt(), i.getUpdatedAt(),
                c.getId(), c.getFullName(),
                a != null ? a.getId() : null,
                a != null ? a.getFullName() : null);
    }

    public static IncidentDetailDto toDetailDto(Incident i) {
        return new IncidentDetailDto(
                toDto(i),
                i.getHistory().stream().map(h -> new HistoryDto(h.getId(), h.getMessage(), h.getCreatedAt())).toList(),
                i.getComments().stream()
                        .map(c -> new CommentDto(c.getId(), c.getAuthor().getFullName(), c.getContent(), c.getCreatedAt()))
                        .toList());
    }
}

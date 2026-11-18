package com.example.incidents.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Trace d'un changement important sur un incident (creation, affectation, changement de statut). */
@Entity
@Table(name = "incident_history")
public class IncidentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id")
    private Incident incident;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public IncidentHistory() {}

    public IncidentHistory(Incident incident, String message) {
        this.incident = incident;
        this.message = message;
    }

    public Long getId() { return id; }
    public Incident getIncident() { return incident; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

package com.example.incidents.controller;

import com.example.incidents.dto.*;
import com.example.incidents.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<IncidentDto> create(@Valid @RequestBody IncidentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<IncidentDto> my() {
        return service.myIncidents();
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public List<IncidentDto> assigned() {
        return service.assignedIncidents();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<IncidentDto> all() {
        return service.allIncidents();
    }

    /** Authentifie ; le service verifie le proprietaire / technicien affecte / admin. */
    @GetMapping("/{id}")
    public IncidentDetailDto details(@PathVariable Long id) {
        return service.getDetail(id);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('TECHNICIAN','ADMIN')")
    public IncidentDto updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        return service.updateStatus(id, req.status());
    }

    @PutMapping("/{id}/assign/{technicianId}")
    @PreAuthorize("hasRole('ADMIN')")
    public IncidentDto assign(@PathVariable Long id, @PathVariable Long technicianId) {
        return service.assign(id, technicianId);
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<IncidentDetailDto> addComment(@PathVariable Long id, @Valid @RequestBody CommentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addComment(id, req.content()));
    }
}

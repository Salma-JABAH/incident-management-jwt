package com.example.incidents.service;

import com.example.incidents.dto.*;
import com.example.incidents.entity.*;
import com.example.incidents.exception.ApiException;
import com.example.incidents.repository.IncidentRepository;
import com.example.incidents.repository.UserRepository;
import com.example.incidents.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class IncidentService {

    private final IncidentRepository incidents;
    private final UserRepository users;
    private final CurrentUserService current;

    public IncidentService(IncidentRepository incidents, UserRepository users, CurrentUserService current) {
        this.incidents = incidents;
        this.users = users;
        this.current = current;
    }

    public IncidentDto create(IncidentRequest req) {
        User me = current.get();
        Incident i = new Incident();
        i.setTitle(req.title().trim());
        i.setDescription(req.description().trim());
        i.setCategory(req.category());
        i.setPriority(req.priority());
        i.setStatus(Status.OPEN);
        i.setCreatedBy(me);
        i.addHistory("Incident cree par " + me.getFullName());
        return Mapper.toDto(incidents.save(i));
    }

    @Transactional(readOnly = true)
    public List<IncidentDto> myIncidents() {
        return incidents.findByCreatedByOrderByCreatedAtDesc(current.get()).stream().map(Mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<IncidentDto> assignedIncidents() {
        return incidents.findByAssignedToOrderByCreatedAtDesc(current.get()).stream().map(Mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<IncidentDto> allIncidents() {
        return incidents.findAllByOrderByCreatedAtDesc().stream().map(Mapper::toDto).toList();
    }

    /** Detail d'un incident : le controle du proprietaire est fait ICI, cote serveur. */
    @Transactional(readOnly = true)
    public IncidentDetailDto getDetail(Long id) {
        User me = current.get();
        Incident i = find(id);
        checkCanView(i, me);
        return Mapper.toDetailDto(i);
    }

    public IncidentDto updateStatus(Long id, Status newStatus) {
        User me = current.get();
        Incident i = find(id);

        // Regle 5 : un technicien ne modifie que les incidents qui lui sont affectes
        if (me.getRole() == Role.TECHNICIAN && !isAssignedTo(i, me)) {
            throw ApiException.forbidden("Acces interdit : cet incident ne vous est pas affecte");
        }
        // Regle 8 : un incident CLOSED n'est plus modifiable
        if (i.getStatus() == Status.CLOSED) {
            throw ApiException.badRequest("Cet incident est clos : son statut ne peut plus etre modifie");
        }
        if (newStatus == i.getStatus()) {
            throw ApiException.badRequest("L'incident a deja le statut " + newStatus);
        }
        // OPEN et ASSIGNED sont geres automatiquement (creation / affectation)
        if (newStatus == Status.OPEN || newStatus == Status.ASSIGNED) {
            throw ApiException.badRequest("Le statut " + newStatus + " est gere automatiquement par le systeme");
        }
        // Seul l'administrateur peut cloturer definitivement
        if (newStatus == Status.CLOSED && me.getRole() != Role.ADMIN) {
            throw ApiException.forbidden("Seul un administrateur peut cloturer un incident");
        }

        Status old = i.getStatus();
        i.setStatus(newStatus);
        i.addHistory(old + " -> " + newStatus + " (par " + me.getFullName() + ")");
        return Mapper.toDto(incidents.save(i));
    }

    /** Regle 6 : seul l'administrateur affecte (verifie aussi par @PreAuthorize dans le controleur). */
    public IncidentDto assign(Long incidentId, Long technicianId) {
        User me = current.get();
        if (me.getRole() != Role.ADMIN) {
            throw ApiException.forbidden("Seul un administrateur peut affecter un incident");
        }
        Incident i = find(incidentId);
        if (i.getStatus() == Status.CLOSED) {
            throw ApiException.badRequest("Impossible d'affecter un incident clos");
        }
        User tech = users.findById(technicianId)
                .orElseThrow(() -> ApiException.notFound("Technicien introuvable"));
        if (tech.getRole() != Role.TECHNICIAN) {
            throw ApiException.badRequest("L'utilisateur choisi n'est pas un technicien");
        }
        if (!tech.isEnabled()) {
            throw ApiException.badRequest("Le technicien choisi est desactive");
        }
        i.setAssignedTo(tech);
        if (i.getStatus() == Status.OPEN) {
            i.setStatus(Status.ASSIGNED);
        }
        i.addHistory("Affecte a " + tech.getFullName() + " (par " + me.getFullName() + ")");
        return Mapper.toDto(incidents.save(i));
    }

    public IncidentDetailDto addComment(Long id, String content) {
        User me = current.get();
        Incident i = find(id);
        if (me.getRole() != Role.TECHNICIAN || !isAssignedTo(i, me)) {
            throw ApiException.forbidden("Acces interdit : seul le technicien affecte peut ajouter une note");
        }
        if (i.getStatus() == Status.CLOSED) {
            throw ApiException.badRequest("Cet incident est clos : impossible d'ajouter une note");
        }
        i.addComment(me, content.trim());
        incidents.save(i);
        return Mapper.toDetailDto(i);
    }

    // ------------------------------------------------------------------ helpers

    private Incident find(Long id) {
        return incidents.findWithUsersById(id).orElseThrow(() -> ApiException.notFound("Incident introuvable"));
    }

    private boolean isAssignedTo(Incident i, User u) {
        return i.getAssignedTo() != null && Objects.equals(i.getAssignedTo().getId(), u.getId());
    }

    private void checkCanView(Incident i, User me) {
        switch (me.getRole()) {
            case ADMIN -> { /* acces total */ }
            case EMPLOYEE -> {
                if (!Objects.equals(i.getCreatedBy().getId(), me.getId())) {
                    throw ApiException.forbidden("Acces interdit : cet incident ne vous appartient pas");
                }
            }
            case TECHNICIAN -> {
                if (!isAssignedTo(i, me)) {
                    throw ApiException.forbidden("Acces interdit : cet incident ne vous est pas affecte");
                }
            }
        }
    }
}

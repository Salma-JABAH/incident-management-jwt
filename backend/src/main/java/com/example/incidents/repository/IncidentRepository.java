package com.example.incidents.repository;

import com.example.incidents.entity.Incident;
import com.example.incidents.entity.Status;
import com.example.incidents.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // L'EntityGraph charge createdBy / assignedTo en une seule requete (evite le N+1 du LAZY).
    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    List<Incident> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    List<Incident> findByAssignedToOrderByCreatedAtDesc(User assignedTo);

    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    List<Incident> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    Optional<Incident> findWithUsersById(Long id);

    long countByStatus(Status status);
}

package com.example.incidents.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

/** Format d'erreur unique : { "status": 404, "message": "Incident introuvable" } (+ "errors" pour la validation). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(int status, String message, Map<String, String> errors) {

    public ErrorResponse(int status, String message) {
        this(status, message, null);
    }
}

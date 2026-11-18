package com.example.incidents.security;

import com.example.incidents.entity.User;
import com.example.incidents.exception.ApiException;
import com.example.incidents.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/** Recupere l'utilisateur authentifie (SecurityContextHolder -> Authentication -> Principal = email). */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw ApiException.unauthorized("Authentification requise");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> ApiException.unauthorized("Utilisateur introuvable"));
    }
}

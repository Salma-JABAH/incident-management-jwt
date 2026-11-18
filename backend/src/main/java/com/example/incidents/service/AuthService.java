package com.example.incidents.service;

import com.example.incidents.dto.*;
import com.example.incidents.entity.RefreshToken;
import com.example.incidents.entity.Role;
import com.example.incidents.entity.User;
import com.example.incidents.exception.ApiException;
import com.example.incidents.repository.RefreshTokenRepository;
import com.example.incidents.repository.UserRepository;
import com.example.incidents.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final long refreshExpirationMs;

    public AuthService(UserRepository users, RefreshTokenRepository refreshTokens, PasswordEncoder encoder,
                       JwtService jwt, @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.encoder = encoder;
        this.jwt = jwt;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public UserDto register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw ApiException.conflict("Cet email est deja utilise");
        }
        // Regle metier : role impose cote serveur, jamais choisi par l'utilisateur
        User user = new User(req.fullName().trim(), email, encoder.encode(req.password()), Role.EMPLOYEE);
        return Mapper.toDto(users.save(user));
    }

    public AuthResponse login(LoginRequest req) {
        User user = users.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> ApiException.unauthorized("Email ou mot de passe incorrect"));
        if (!encoder.matches(req.password(), user.getPassword())) {
            throw ApiException.unauthorized("Email ou mot de passe incorrect");
        }
        if (!user.isEnabled()) {
            throw ApiException.forbidden("Compte desactive : contactez un administrateur");
        }
        return buildResponse(user, createRefreshToken(user));
    }

    /** Echange un refresh token valide contre un nouvel access token (rotation du refresh token). */
    public AuthResponse refresh(RefreshRequest req) {
        RefreshToken stored = refreshTokens.findByToken(req.refreshToken())
                .orElseThrow(() -> ApiException.unauthorized("Refresh token invalide"));
        if (stored.isRevoked() || stored.isExpired()) {
            throw ApiException.unauthorized("Refresh token expire ou revoque, reconnectez-vous");
        }
        User user = stored.getUser();
        if (!user.isEnabled()) {
            throw ApiException.forbidden("Compte desactive : contactez un administrateur");
        }
        stored.setRevoked(true); // l'ancien refresh token ne peut plus servir
        return buildResponse(user, createRefreshToken(user));
    }

    /** Deconnexion : revoque le refresh token (l'access token expirera naturellement). */
    public void logout(String refreshToken) {
        refreshTokens.findByToken(refreshToken).ifPresent(t -> t.setRevoked(true));
    }

    private String createRefreshToken(User user) {
        String value = UUID.randomUUID() + "-" + UUID.randomUUID();
        refreshTokens.save(new RefreshToken(value, user, Instant.now().plusMillis(refreshExpirationMs)));
        return value;
    }

    private AuthResponse buildResponse(User user, String refreshToken) {
        return new AuthResponse(jwt.generateToken(user), refreshToken, user.getRole().name(), user.getFullName());
    }
}

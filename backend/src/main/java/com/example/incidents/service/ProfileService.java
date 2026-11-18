package com.example.incidents.service;

import com.example.incidents.dto.*;
import com.example.incidents.entity.User;
import com.example.incidents.exception.ApiException;
import com.example.incidents.repository.UserRepository;
import com.example.incidents.security.CurrentUserService;
import com.example.incidents.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {

    private final CurrentUserService current;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public ProfileService(CurrentUserService current, UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.current = current;
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional(readOnly = true)
    public UserDto getProfile() {
        return Mapper.toDto(current.get());
    }

    /**
     * Met a jour nom / email. Le subject du JWT etant l'email, un nouveau token est renvoye
     * (sinon l'ancien token ne correspondrait plus a aucun utilisateur).
     */
    public AuthResponse updateProfile(ProfileUpdateRequest req) {
        User user = current.get();
        String email = req.email().trim().toLowerCase();
        if (!email.equals(user.getEmail()) && users.existsByEmail(email)) {
            throw ApiException.conflict("Cet email est deja utilise");
        }
        user.setFullName(req.fullName().trim());
        user.setEmail(email);
        users.save(user);
        return new AuthResponse(jwt.generateToken(user), null, user.getRole().name(), user.getFullName());
    }

    public MessageResponse changePassword(ChangePasswordRequest req) {
        User user = current.get();
        if (!encoder.matches(req.oldPassword(), user.getPassword())) {
            throw ApiException.badRequest("L'ancien mot de passe est incorrect");
        }
        if (!req.newPassword().equals(req.confirmPassword())) {
            throw ApiException.badRequest("La confirmation ne correspond pas au nouveau mot de passe");
        }
        if (encoder.matches(req.newPassword(), user.getPassword())) {
            throw ApiException.badRequest("Le nouveau mot de passe doit etre different de l'ancien");
        }
        user.setPassword(encoder.encode(req.newPassword()));
        users.save(user);
        return new MessageResponse("Mot de passe modifie avec succes");
    }
}

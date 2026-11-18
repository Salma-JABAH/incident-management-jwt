package com.example.incidents.security;

import com.example.incidents.entity.User;
import com.example.incidents.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Lit l'en-tete "Authorization: Bearer <JWT>", valide le token et place l'utilisateur dans le SecurityContext.
 * Si le token est absent / invalide / expire, la requete reste anonyme et Spring Security renvoie 401.
 * Le role utilise pour les autorisations est celui de la base (un changement de role ou une
 * desactivation de compte est donc pris en compte immediatement, meme avec un ancien token).
 */
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.parse(token);
                Optional<User> user = userRepository.findByEmail(claims.getSubject());
                user.filter(User::isEnabled).ifPresent(u -> {
                    var auth = new UsernamePasswordAuthenticationToken(
                            u.getEmail(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name())));
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            } catch (JwtException | IllegalArgumentException ex) {
                // token invalide, signature incorrecte ou expire : on ne s'authentifie pas
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}

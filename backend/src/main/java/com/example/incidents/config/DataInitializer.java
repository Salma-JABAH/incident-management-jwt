package com.example.incidents.config;

import com.example.incidents.entity.Role;
import com.example.incidents.entity.User;
import com.example.incidents.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Comptes de test. Les roles ADMIN et TECHNICIAN ne peuvent pas etre obtenus via /auth/register,
 * ils sont donc crees ici au demarrage (mots de passe encodes avec BCrypt).
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            create(repo, encoder, "Admin Principal", "admin@example.com", "Admin123!", Role.ADMIN);
            create(repo, encoder, "Karim Technicien", "tech1@example.com", "Tech123!", Role.TECHNICIAN);
            create(repo, encoder, "Salma Technicienne", "tech2@example.com", "Tech123!", Role.TECHNICIAN);
            create(repo, encoder, "Ahmed Benali", "employee1@example.com", "Employee123!", Role.EMPLOYEE);
            create(repo, encoder, "Sara El Idrissi", "employee2@example.com", "Employee123!", Role.EMPLOYEE);
        };
    }

    private void create(UserRepository repo, PasswordEncoder encoder, String name, String email, String pwd, Role role) {
        if (!repo.existsByEmail(email)) {
            repo.save(new User(name, email, encoder.encode(pwd), role));
        }
    }
}

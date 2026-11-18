package com.example.incidents.controller;

import com.example.incidents.dto.*;
import com.example.incidents.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profile;

    public ProfileController(ProfileService profile) {
        this.profile = profile;
    }

    @GetMapping
    public UserDto get() {
        return profile.getProfile();
    }

    @PutMapping
    public AuthResponse update(@Valid @RequestBody ProfileUpdateRequest req) {
        return profile.updateProfile(req);
    }

    @PutMapping("/password")
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        return profile.changePassword(req);
    }
}

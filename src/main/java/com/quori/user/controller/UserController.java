package com.quori.user.controller;

import com.quori.user.dto.RegisterRequest;
import com.quori.user.dto.UpdateUserRequest;
import com.quori.user.dto.UserResponse;
import com.quori.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register after Firebase signup")
    public UserResponse register(@AuthenticationPrincipal Jwt jwt,
                                 @Valid @RequestBody RegisterRequest request) {
        return service.register(jwt, request);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return service.getCurrentUser(jwt);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public UserResponse update(@AuthenticationPrincipal Jwt jwt,
                               @Valid @RequestBody UpdateUserRequest request) {
        return service.updateCurrentUser(jwt, request);
    }
}

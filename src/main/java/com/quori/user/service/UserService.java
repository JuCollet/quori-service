package com.quori.user.service;

import com.quori.user.dto.RegisterRequest;
import com.quori.user.dto.UpdateUserRequest;
import com.quori.user.dto.UserResponse;
import com.quori.user.entity.AppUser;
import com.quori.user.repository.AppUserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final AppUserRepository repository;

    public UserService(AppUserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserResponse register(Jwt jwt, RegisterRequest request) {
        var firebaseUid = jwt.getSubject();

        if (repository.existsByFirebaseUid(firebaseUid)) {
            throw new IllegalStateException("User already registered");
        }

        var user = new AppUser();
        user.setFirebaseUid(firebaseUid);
        user.setEmail(jwt.getClaimAsString("email"));
        user.setDisplayName(request.displayName());
        user.setRole(request.role());

        return UserResponse.from(repository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Jwt jwt) {
        return UserResponse.from(findByJwt(jwt));
    }

    @Transactional
    public UserResponse updateCurrentUser(Jwt jwt, UpdateUserRequest request) {
        var user = findByJwt(jwt);
        user.setDisplayName(request.displayName());
        return UserResponse.from(repository.save(user));
    }

    private AppUser findByJwt(Jwt jwt) {
        return repository.findByFirebaseUid(jwt.getSubject())
                .orElseThrow(() -> new IllegalStateException("User not registered"));
    }
}

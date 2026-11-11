package com.quori.organization.service;

import com.quori.common.NotFoundException;
import com.quori.organization.dto.CreateOrganizationRequest;
import com.quori.organization.dto.OrganizationResponse;
import com.quori.organization.entity.Organization;
import com.quori.organization.repository.OrganizationRepository;
import com.quori.user.entity.AppUser;
import com.quori.user.repository.AppUserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository repository;
    private final AppUserRepository appUserRepository;

    public OrganizationService(OrganizationRepository repository,
                               AppUserRepository appUserRepository) {
        this.repository = repository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public OrganizationResponse create(Jwt jwt, CreateOrganizationRequest request) {
        var owner = findUserByJwt(jwt);
        var entity = new Organization();
        entity.setName(request.name());
        entity.setOwner(owner);
        return OrganizationResponse.from(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getById(UUID id) {
        return OrganizationResponse.from(findOrThrow(id));
    }

    @Transactional
    public OrganizationResponse update(UUID id, CreateOrganizationRequest request) {
        var entity = findOrThrow(id);
        entity.setName(request.name());
        return OrganizationResponse.from(repository.save(entity));
    }

    public Organization findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization", id));
    }

    private AppUser findUserByJwt(Jwt jwt) {
        return appUserRepository.findByFirebaseUid(jwt.getSubject())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}

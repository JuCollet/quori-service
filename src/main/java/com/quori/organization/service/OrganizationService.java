package com.quori.organization.service;

import com.quori.common.NotFoundException;
import com.quori.organization.dto.CreateOrganizationRequest;
import com.quori.organization.dto.OrganizationResponse;
import com.quori.organization.entity.Organization;
import com.quori.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository repository;

    public OrganizationService(OrganizationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public OrganizationResponse create(CreateOrganizationRequest request) {
        var entity = new Organization();
        entity.setName(request.name());
        entity.setOwnerEmail(request.ownerEmail());
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
        entity.setOwnerEmail(request.ownerEmail());
        return OrganizationResponse.from(repository.save(entity));
    }

    public Organization findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization", id));
    }
}

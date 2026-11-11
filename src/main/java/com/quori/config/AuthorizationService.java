package com.quori.config;

import com.quori.event.repository.EventRepository;
import com.quori.organization.repository.OrganizationRepository;
import com.quori.product.repository.ProductRepository;
import com.quori.user.repository.AppUserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("authz")
public class AuthorizationService {

    private final AppUserRepository appUserRepository;
    private final OrganizationRepository organizationRepository;
    private final EventRepository eventRepository;
    private final ProductRepository productRepository;

    public AuthorizationService(AppUserRepository appUserRepository,
                                OrganizationRepository organizationRepository,
                                EventRepository eventRepository,
                                ProductRepository productRepository) {
        this.appUserRepository = appUserRepository;
        this.organizationRepository = organizationRepository;
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
    }

    public boolean isOrganizationOwner(Jwt jwt, UUID organizationId) {
        var user = appUserRepository.findByFirebaseUid(jwt.getSubject()).orElse(null);
        if (user == null) return false;
        return organizationRepository.findById(organizationId)
                .map(org -> org.getOwner().getId().equals(user.getId()))
                .orElse(false);
    }

    public boolean isEventOrganizationOwner(Jwt jwt, UUID eventId) {
        var user = appUserRepository.findByFirebaseUid(jwt.getSubject()).orElse(null);
        if (user == null) return false;
        return eventRepository.findById(eventId)
                .map(event -> event.getOrganization().getOwner().getId().equals(user.getId()))
                .orElse(false);
    }

    public boolean isProductOrganizationOwner(Jwt jwt, UUID productId) {
        var user = appUserRepository.findByFirebaseUid(jwt.getSubject()).orElse(null);
        if (user == null) return false;
        return productRepository.findById(productId)
                .map(product -> product.getEvent().getOrganization().getOwner().getId().equals(user.getId()))
                .orElse(false);
    }
}

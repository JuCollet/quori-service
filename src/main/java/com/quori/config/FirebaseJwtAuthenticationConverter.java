package com.quori.config;

import com.quori.user.repository.AppUserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public class FirebaseJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AppUserRepository appUserRepository;

    public FirebaseJwtAuthenticationConverter(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        var firebaseUid = jwt.getSubject();

        var authorities = appUserRepository.findByFirebaseUid(firebaseUid)
                .map(user -> List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .orElse(List.of(new SimpleGrantedAuthority("ROLE_UNREGISTERED")));

        return new JwtAuthenticationToken(jwt, authorities, firebaseUid);
    }
}

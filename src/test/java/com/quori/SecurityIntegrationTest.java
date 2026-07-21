package com.quori;

import com.quori.user.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAs(String uid, String email) {
        var user = appUserRepository.findByFirebaseUid(uid).orElse(null);
        var role = user != null ? user.getRole().name() : "UNREGISTERED";

        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(uid).claim("email", email))
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Test
    void unauthenticatedRequest_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/organizations/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swaggerUi_shouldBePublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedUser_canRegister() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("firebase-uid-123")
                                        .claim("email", "test@quori.app"))
                                .authorities(new SimpleGrantedAuthority("ROLE_UNREGISTERED")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"displayName": "Test User", "role": "ORGANIZER"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@quori.app"))
                .andExpect(jsonPath("$.role").value("ORGANIZER"));
    }

    @Test
    void authenticatedUser_canAccessMe() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("firebase-uid-me").claim("email", "me@quori.app"))
                        .authorities(new SimpleGrantedAuthority("ROLE_UNREGISTERED")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"displayName": "Me User", "role": "CUSTOMER"}
                        """));

        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwtAs("firebase-uid-me", "me@quori.app")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Me User"));
    }

    @Test
    void customerRole_cannotCreateOrganization() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("firebase-uid-customer").claim("email", "customer@quori.app"))
                        .authorities(new SimpleGrantedAuthority("ROLE_UNREGISTERED")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"displayName": "Customer", "role": "CUSTOMER"}
                        """));

        mockMvc.perform(post("/api/v1/organizations")
                        .with(jwtAs("firebase-uid-customer", "customer@quori.app"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Org"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void organizerRole_canCreateOrganization() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("firebase-uid-organizer").claim("email", "organizer@quori.app"))
                        .authorities(new SimpleGrantedAuthority("ROLE_UNREGISTERED")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"displayName": "Organizer", "role": "ORGANIZER"}
                        """));

        mockMvc.perform(post("/api/v1/organizations")
                        .with(jwtAs("firebase-uid-organizer", "organizer@quori.app"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "My Org"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("My Org"))
                .andExpect(jsonPath("$.ownerDisplayName").value("Organizer"));
    }
}

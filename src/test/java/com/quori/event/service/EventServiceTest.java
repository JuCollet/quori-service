package com.quori.event.service;

import com.quori.common.NotFoundException;
import com.quori.event.dto.CreateEventRequest;
import com.quori.event.dto.UpdateEventStatusRequest;
import com.quori.event.entity.Event;
import com.quori.event.entity.EventStatus;
import com.quori.event.repository.EventRepository;
import com.quori.organization.entity.Organization;
import com.quori.organization.service.OrganizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository repository;

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private EventService service;

    @Test
    void create_shouldReturnEventWithDraftStatus() {
        var orgId = UUID.randomUUID();
        var org = new Organization();
        org.setId(orgId);
        org.setName("Test Org");

        when(organizationService.findOrThrow(orgId)).thenReturn(org);
        when(repository.save(any(Event.class))).thenAnswer(invocation -> {
            var event = invocation.getArgument(0, Event.class);
            event.setId(UUID.randomUUID());
            invokeOnCreate(event);
            return event;
        });

        var request = new CreateEventRequest("Festival", "Description", LocalDate.of(2026, 7, 15), LocalDate.of(2026, 7, 16));
        var response = service.create(orgId, request);

        assertThat(response.name()).isEqualTo("Festival");
        assertThat(response.status()).isEqualTo(EventStatus.DRAFT);
        assertThat(response.organizationId()).isEqualTo(orgId);
    }

    @Test
    void updateStatus_shouldRejectInvalidTransition() {
        var eventId = UUID.randomUUID();
        var event = new Event();
        event.setId(eventId);
        event.setStatus(EventStatus.DRAFT);

        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        var request = new UpdateEventStatusRequest(EventStatus.SETTLED);

        assertThatThrownBy(() -> service.updateStatus(eventId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot transition from DRAFT to SETTLED");
    }

    @Test
    void updateStatus_shouldAllowValidTransition() {
        var eventId = UUID.randomUUID();
        var event = new Event();
        event.setId(eventId);
        event.setStatus(EventStatus.DRAFT);
        var org = new Organization();
        org.setId(UUID.randomUUID());
        event.setOrganization(org);
        invokeOnCreate(event);

        when(repository.findById(eventId)).thenReturn(Optional.of(event));
        when(repository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.updateStatus(eventId, new UpdateEventStatusRequest(EventStatus.OPEN));
        assertThat(response.status()).isEqualTo(EventStatus.OPEN);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(NotFoundException.class);
    }

    private static void invokeOnCreate(Object entity) {
        try {
            Method m = com.quori.common.BaseEntity.class.getDeclaredMethod("onCreate");
            m.setAccessible(true);
            m.invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

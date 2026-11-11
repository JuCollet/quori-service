package com.quori.user.repository;

import com.quori.user.entity.EventMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventMembershipRepository extends JpaRepository<EventMembership, UUID> {

    List<EventMembership> findByEventId(UUID eventId);

    List<EventMembership> findByUserId(UUID userId);
}

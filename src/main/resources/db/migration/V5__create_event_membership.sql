CREATE TABLE event_membership (
    id         UUID PRIMARY KEY,
    event_id   UUID        NOT NULL REFERENCES event (id),
    user_id    UUID        NOT NULL REFERENCES app_user (id),
    role       VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (event_id, user_id)
);

CREATE INDEX idx_event_membership_event ON event_membership (event_id);
CREATE INDEX idx_event_membership_user ON event_membership (user_id);

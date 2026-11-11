ALTER TABLE organization ADD COLUMN owner_id UUID;

ALTER TABLE organization ADD CONSTRAINT fk_organization_owner
    FOREIGN KEY (owner_id) REFERENCES app_user(id);

-- For existing rows (if any), owner_id must be set manually before enforcing NOT NULL
ALTER TABLE organization ALTER COLUMN owner_id SET NOT NULL;

ALTER TABLE organization DROP COLUMN owner_email;

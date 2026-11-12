CREATE TABLE resources (
    resource_id UUID DEFAULT gen_random_uuid(),
    path VARCHAR(1000) NOT NULL,
    name VARCHAR(255) NOT NULL,
    size BIGINT NULL,
    type ResourceType NOT NULL,

    CONSTRAINT resources_pk PRIMARY KEY (resource_id)
)
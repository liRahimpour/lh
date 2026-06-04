CREATE TABLE source_files (
                              id UUID PRIMARY KEY,
                              project_id UUID NOT NULL,
                              path TEXT NOT NULL,
                              filename VARCHAR(255) NOT NULL,
                              extension VARCHAR(50),
                              language VARCHAR(50) NOT NULL,
                              size_bytes BIGINT NOT NULL,
                              content_hash VARCHAR(128),
                              storage_key TEXT,
                              created_at TIMESTAMP NOT NULL,

                              CONSTRAINT fk_source_files_project
                                  FOREIGN KEY (project_id)
                                      REFERENCES projects(id)
                                      ON DELETE CASCADE
);

CREATE INDEX idx_source_files_project_id
    ON source_files(project_id);

CREATE INDEX idx_source_files_language
    ON source_files(language);

CREATE INDEX idx_source_files_content_hash
    ON source_files(content_hash);
CREATE TABLE code_symbols (
                              id UUID PRIMARY KEY,
                              project_id UUID NOT NULL,
                              source_file_id UUID NOT NULL,
                              type VARCHAR(50) NOT NULL,
                              name VARCHAR(255) NOT NULL,
                              fully_qualified_name TEXT,
                              start_line INT,
                              end_line INT,
                              created_at TIMESTAMP NOT NULL,

                              CONSTRAINT fk_code_symbols_project
                                  FOREIGN KEY (project_id)
                                      REFERENCES projects(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_code_symbols_source_file
                                  FOREIGN KEY (source_file_id)
                                      REFERENCES source_files(id)
                                      ON DELETE CASCADE
);

CREATE INDEX idx_code_symbols_project_id
    ON code_symbols(project_id);

CREATE INDEX idx_code_symbols_source_file_id
    ON code_symbols(source_file_id);

CREATE INDEX idx_code_symbols_type
    ON code_symbols(type);

CREATE INDEX idx_code_symbols_name
    ON code_symbols(name);